package cb_server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cb_rpc.Rpc_Server;
import cb_server.DB.CBServerDB;
import cb_server.Import.ImportScheduler;
import cb_server.Import.ServerImporterProgress;
import CB_Core.CoreSettingsForward;
import CB_Core.FilterProperties;
import CB_Core.Api.ApiGroundspeakResult;
import CB_Core.Api.ApiGroundspeak_GetPocketQueryData;
import CB_Core.Api.ApiGroundspeak_SearchForGeocaches;
import CB_Core.Api.GroundspeakAPI;
import CB_Core.Api.PocketQuery;
import CB_Core.Api.PocketQuery.PQ;
import CB_Core.Api.SearchGC;
import CB_Core.Api.SearchGCOwner;
import CB_Core.DAO.CacheListDAO;
import CB_Core.DAO.CategoryDAO;
import CB_Core.DB.Database;
import CB_Core.DB.Database.DatabaseType;
import CB_Core.Import.Importer;
import CB_Core.Import.ImporterProgress;
import CB_Core.Types.Cache;
import CB_Core.Types.Categories;
import CB_Core.Types.Category;
import CB_Core.Types.GpxFilename;
import CB_Core.Types.ImageEntry;
import CB_Core.Types.LogEntry;
import CB_Locator.Coordinate;
import CB_Utils.Config_Core;
import CB_Utils.Util.FileIO;
import Rpc.RpcFunctionsServer;

public class CacheboxServer {
	public static Logger log;

	public static void main(String[] args) throws Exception {
		log = LoggerFactory.getLogger(CacheboxServer.class);
		writeLockFile("cbserver.lock");
		log.debug(System.getProperty("sun.net.http.allowRestrictedHeaders"));
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
		log.info("Hallo Jetty Vaadin Server");
		log.debug("Initialize Config");
		InitialConfig();
		InitialCacheDB();
		Config.settings.ReadFromDB();
		ImportScheduler.importScheduler.start();
		int port = 80;
		try {
			port = Integer.valueOf(args[0]);
		} catch (Exception ex) {
			// Default Port 80 einstellen
		}
		RpcFunctionsServer.jettyPort = port;
		Rpc_Server rpcServer = new Rpc_Server(RpcFunctionsServer.class);

		// Server server = new Server(8085);
		Server server = new Server(port);

		// VAADIN Part
		WebAppContext webapp = new WebAppContext();
		webapp.setDescriptor("");
		webapp.setResourceBase("./WebContent");
		webapp.setContextPath("/cbserver");
		webapp.setParentLoaderPriority(true);

		// Images
		WebAppContext webappImages = new WebAppContext();
		webappImages.setDescriptor("");
		webappImages.setResourceBase(Config.WorkPath + "/repository/images");
		webappImages.setContextPath("/images");
		webappImages.setParentLoaderPriority(true);

		// Spoiler
		WebAppContext webappSpoiler = new WebAppContext();
		webappSpoiler.setDescriptor("");
		webappSpoiler.setResourceBase(Config.WorkPath + "/repository/spoilers");
		webappSpoiler.setContextPath("/spoilers");
		webappSpoiler.setParentLoaderPriority(true);

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { webapp, webappImages, webappSpoiler });

		server.setHandler(contexts);

		
		ImportScheduler importScheduler = new ImportScheduler();
		
		if (false) {
			// Import PQs
			ArrayList<PQ> pqList = new ArrayList<PQ>();
			log.debug("Load PQ-List");
			CB_Core.Api.PocketQuery.GetPocketQueryList(pqList);
			log.debug("Load PQ-List ready");
			ApiGroundspeak_GetPocketQueryData ipq = new ApiGroundspeak_GetPocketQueryData();
			for (PQ pq : pqList) {
				log.debug("Load PQ " + pq.Name);
				if (!pq.Name.equals("80 Tage"))
					continue;

				if (true) {
					// Zipped Pocketquery
					int i = PocketQuery.DownloadSinglePocketQuery(pq, Config.PocketQueryFolder.getValue());
					System.out.println(i);
				} else {

					// Category suchen, die dazu gehört
					CategoryDAO categoryDAO = new CategoryDAO();
					Category category = categoryDAO.GetCategory(CoreSettingsForward.Categories, pq.Name);
					if (category != null) // should not happen!!!
					{
						GpxFilename gpxFilename = categoryDAO.CreateNewGpxFilename(category, pq.Name);
						if (gpxFilename != null) {

							ipq.setPQ(pq);
							ApiGroundspeakResult res = ipq.execute();
							log.debug("Load PQ " + pq.Name + " ready");

							if (res.getResult() == 0) {
								ArrayList<String> caches = ipq.getCaches();
								for (int i = 0; i <= caches.size() / 50; i++) {
									ArrayList<String> gcCodes = new ArrayList<>();
									for (int j = 0; j < 50; j++) {
										if (i * 50 + j < caches.size()) {
											gcCodes.add(caches.get(i * 50 + j));
										}
									}
									if (gcCodes.size() == 0) {
										continue;
									}
									log.debug("Import 50 Caches from " + pq.Name + " (" + String.valueOf(i * 50) + "-" + String.valueOf(i * 50 + gcCodes.size() - 1) + ")");
									ArrayList<Cache> apiCaches = new ArrayList<Cache>();
									ArrayList<LogEntry> apiLogs = new ArrayList<LogEntry>();
									ArrayList<ImageEntry> apiImages = new ArrayList<ImageEntry>();
									SearchGC search = new SearchGC(gcCodes);
									ApiGroundspeak_SearchForGeocaches apis = new ApiGroundspeak_SearchForGeocaches(search, apiCaches, apiLogs, apiImages, gpxFilename.Id);
									apis.execute();
									if (apiCaches.size() > 0) {
										GroundspeakAPI.WriteCachesLogsImages_toDB(apiCaches, apiLogs, apiImages);
									}

								}
							}
						}
					}
				}
			}

			// Import ZIP GPX Files
			Importer importer = new Importer();
			ServerImporterProgress ip = new ServerImporterProgress();
			try {
				boolean importPQfromGC = true;
				boolean importGPX = true;
				boolean importImages = true;
				boolean importSpoiler = true;
				// if Import PQ
				if (importPQfromGC) {
					ip.addStep(ip.new Step("importGC", 4));
				}
				// if Import GPX
				if (importGPX) {
					ip.addStep(ip.new Step("ExtractZip", 1));
					ip.addStep(ip.new Step("AnalyseGPX", 1));
					ip.addStep(ip.new Step("ImportGPX", 4));
				}
				// if Import Vote
				ip.addStep(ip.new Step("sendGcVote", 1));
				ip.addStep(ip.new Step("importGcVote", 4));
				// if Import Images
				if (importImages) {
					ip.addStep(ip.new Step("importImages", 4));
				}
				// if Clean Logs
				ip.addStep(ip.new Step("DeleteLogs", 1));
				// if CompactDB
				ip.addStep(ip.new Step("CompactDB", 1));

				if (pqList != null && pqList.size() > 0) {
				}
				if (importGPX) {
					System.gc();
					long startTime = System.currentTimeMillis();

					Database.Data.beginTransaction();
					Database.Data.Query.clear();
					try {

						importer.importGpx(Config.PocketQueryFolder.getValue(), ip);

						Database.Data.setTransactionSuccessful();
					} catch (Exception exc) {
						exc.printStackTrace();
					}
					Database.Data.endTransaction();

					//					if (BreakawayImportThread.isCanceld())
					//					{
					//						cancelImport();
					//						ip.ProgressChangeMsg("", "");
					//						return;
					//					}

					log.debug("Import  GPX Import took " + (System.currentTimeMillis() - startTime) + "ms");

					System.gc();

					// del alten entpackten Ordener wenn vorhanden?
					File directory = new File(Config.PocketQueryFolder.getValue());
					File[] filelist = directory.listFiles();
					for (File tmp : filelist) {
						if (tmp.isDirectory()) {
							ArrayList<File> ordnerInhalt = FileIO.recursiveDirectoryReader(tmp, new ArrayList<File>());
							for (File tmp2 : ordnerInhalt) {
								tmp2.delete();
							}

						}
						tmp.delete();
					}

				}
				if (importImages || importSpoiler) {
					
//						dis.setAnimationType(AnimationType.Download);
						int result = importer.importImagesNew(ip, importImages, importSpoiler, "");

						if (result == GroundspeakAPI.CONNECTION_TIMEOUT)
						{
//							GL.that.Toast(ConnectionError.INSTANCE);
							ip.ProgressChangeMsg("", "");
							return;
						}

						if (result == GroundspeakAPI.API_IS_UNAVAILABLE)
						{
//							GL.that.Toast(ApiUnavailable.INSTANCE);
							ip.ProgressChangeMsg("", "");
							return;
						}

//						if (BreakawayImportThread.isCanceld())
//						{
//							cancelImport();
//							ip.ProgressChangeMsg("", "");
//							return;
//						}
//						dis.setAnimationType(AnimationType.Work);
					
				}
			} catch (/*Interrupted*/Exception e) {
				// import canceld
				//					cancelImport();
				//					FilterProperties props = GlobalCore.LastFilter;
				//					EditFilterSettings.ApplyFilter(props);
				ip.ProgressChangeMsg("", "");
				return;
			}

		}
		if (false) {
			SearchGCOwner search = new SearchGCOwner(50, new Coordinate(48.1, 12.1), 100000, "Rosa BoBs!");

			// alle per API importierten Caches landen in der Category und
			// GpxFilename
			// API-Import
			// Category suchen, die dazu gehört
			CategoryDAO categoryDAO = new CategoryDAO();
			Category category = categoryDAO.GetCategory(CoreSettingsForward.Categories, "API-Import");
			if (category != null) // should not happen!!!
			{
				GpxFilename gpxFilename = categoryDAO.CreateNewGpxFilename(category, "API-Import");
				if (gpxFilename != null) {

					ArrayList<Cache> apiCaches = new ArrayList<Cache>();
					ArrayList<LogEntry> apiLogs = new ArrayList<LogEntry>();
					ArrayList<ImageEntry> apiImages = new ArrayList<ImageEntry>();
					ApiGroundspeak_SearchForGeocaches apis = new ApiGroundspeak_SearchForGeocaches(search, apiCaches, apiLogs, apiImages, gpxFilename.Id);
					apis.execute();
				}
			}
		}
		server.start();
		log.info("Vaadin Server started on port " + port);
		server.join();

	}

	public static void writeLockFile(String filename) {
		String prePid = ManagementFactory.getRuntimeMXBean().getName();
		String pid = null;
		for (int i = 0; i < prePid.length(); i++)
			if (prePid.charAt(i) == '@')
				pid = prePid.substring(0, i);
		writeTextToFile(filename, pid);
	}

	public static void writeTextToFile(String filename, String text) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			out.write(text);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void InitialCacheDB() {
		Database.Data.StartUp(Config.WorkPath + "/cachebox.db3");
		FilterProperties lastFilter = new FilterProperties(FilterProperties.presets[0].toString());

		String sqlWhere = lastFilter.getSqlWhere(Config.settings.GcLogin.getValue());
		CoreSettingsForward.Categories = new Categories();
		Database.Data.GPXFilenameUpdateCacheCount();

		synchronized (Database.Data.Query) {
			CacheListDAO cacheListDAO = new CacheListDAO();
			cacheListDAO.ReadCacheList(Database.Data.Query, sqlWhere, false, false);
		}

	}

	public static void InitialConfig() {

		if (Config.settings != null && Config.settings.isLoaded())
			return;

		// Read Config
		String workPath = "cachebox";
		// nachschauen ob im aktuellen Ordner eine cachebox.db3 vorhanden ist und in diesem Fall den aktuellen Ordner als WorkPath verwenden
		File file = new File("cachebox.db3");
		if (file.exists()) {
			workPath = "";
		}
		File file2 = new File(workPath);
		workPath = file2.getAbsolutePath();
		log.info("WorkPath: " + workPath);

		Config.Initialize(workPath);

		// hier muss die Config Db initialisiert werden
		try {
			Database.Settings = new CBServerDB(DatabaseType.Settings);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Database.Settings.StartUp(Config.WorkPath + "/User/Config.db3");

		Config.settings.ReadFromDB();

		try {
			Database.Data = new CBServerDB(DatabaseType.CacheBox);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Database.FieldNotes = new CBServerDB(DatabaseType.FieldNotes);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (!FileIO.createDirectory(Config.WorkPath + "/User"))
			return;
		Database.FieldNotes.StartUp(Config.WorkPath + "/User/FieldNotes.db3");
	}

}