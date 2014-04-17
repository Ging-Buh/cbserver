package cb_server.Import;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CB_Core.CoreSettingsForward;
import CB_Core.Api.ApiGroundspeakResult;
import CB_Core.Api.ApiGroundspeak_GetPocketQueryData;
import CB_Core.Api.ApiGroundspeak_SearchForGeocaches;
import CB_Core.Api.GroundspeakAPI;
import CB_Core.Api.PocketQuery;
import CB_Core.Api.SearchGC;
import CB_Core.Api.PocketQuery.PQ;
import CB_Core.DAO.CategoryDAO;
import CB_Core.DB.Database;
import CB_Core.Import.Importer;
import CB_Core.Import.ImporterProgress.Step;
import CB_Core.Types.Cache;
import CB_Core.Types.Category;
import CB_Core.Types.GpxFilename;
import CB_Core.Types.ImageEntry;
import CB_Core.Types.LogEntry;
import CB_Utils.Util.FileIO;
import cb_server.CacheboxServer;
import cb_server.Config;

public class ImportScheduler implements Runnable {
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static Logger log;

	public ImportScheduler() {
		log = LoggerFactory.getLogger(CacheboxServer.class);
		scheduler.scheduleAtFixedRate(this, 0, 60 * 24, TimeUnit.MINUTES);
	}

	@Override
	public void run() {
		System.out.println("run Import");
		log.info("Start Import");
		try {
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

				if (importPQfromGC) {
					ip.setJobMax("importGC", 10);
					ip.ProgressChangeMsg("importGC", "Download PQ-List from GC");
					// Import PQs
					ArrayList<PQ> pqList = new ArrayList<PQ>();
					log.debug("Load PQ-List");
					CB_Core.Api.PocketQuery.GetPocketQueryList(pqList);
					ip.setJobMax("importGC", pqList.size() + 1);
					log.debug("Load PQ-List ready");
					ApiGroundspeak_GetPocketQueryData ipq = new ApiGroundspeak_GetPocketQueryData();
					for (PQ pq : pqList) {
						ip.ProgressInkrement("importGC", "Download PQ - " + pq.Name, false);
						log.debug("Load PQ " + pq.Name);
						if (!pq.Name.equals("80 Tage"))
							continue;

						// Zipped Pocketquery
						int i = PocketQuery.DownloadSinglePocketQuery(pq, Config.PocketQueryFolder.getValue());
						System.out.println(i);
					}
					ip.ProgressInkrement("importGC", "Download PQ-List from GC finished", true);
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

					//					dis.setAnimationType(AnimationType.Download);
					int result = importer.importImagesNew(ip, importImages, importSpoiler, "");

					if (result == GroundspeakAPI.CONNECTION_TIMEOUT) {
						//						GL.that.Toast(ConnectionError.INSTANCE);
						ip.ProgressChangeMsg("", "");
						return;
					}

					if (result == GroundspeakAPI.API_IS_UNAVAILABLE) {
						//						GL.that.Toast(ApiUnavailable.INSTANCE);
						ip.ProgressChangeMsg("", "");
						return;
					}

					//					if (BreakawayImportThread.isCanceld())
					//					{
					//						cancelImport();
					//						ip.ProgressChangeMsg("", "");
					//						return;
					//					}
					//					dis.setAnimationType(AnimationType.Work);

				}
			} catch (/*Interrupted*/Exception e) {
				// import canceld
				//					cancelImport();
				//					FilterProperties props = GlobalCore.LastFilter;
				//					EditFilterSettings.ApplyFilter(props);
				ip.ProgressChangeMsg("", "");
				return;
			}
		} catch (Exception ex) {
			log.error("Import Error: " + ex.getMessage());
		}
		System.out.println("Import finished");
		log.info("Import finished");

	}
}
