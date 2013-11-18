package cb_server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

import cb_rpc.Rpc_Server;
import cb_server.DB.CBServerDB;
import CB_Core.CoreSettingsForward;
import CB_Core.FilterProperties;
import CB_Core.DAO.CacheListDAO;
import CB_Core.DB.Database;
import CB_Core.DB.Database.DatabaseType;
import CB_Core.Types.Categories;
import CB_Utils.Util.FileIO;
import Rpc.RpcFunctionsServer;

public class CacheboxServer {
	public static void main(String[] args) throws Exception {
		writeLockFile("cbserver.lock");
		System.out.println(System
				.getProperty("sun.net.http.allowRestrictedHeaders"));
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
		System.out.println("Hallo Jetty Vaadin Server");
		System.out.println("Initialize Config");
		InitialConfig();
		InitialCacheDB();

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
		webapp.setContextPath("/");
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
		contexts.setHandlers(new Handler[] { webapp, webappImages,
				webappSpoiler });

		server.setHandler(contexts);

		server.start();
		System.out.println("Vaadin Server started on port " + port);
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
		FilterProperties lastFilter = new FilterProperties(
				FilterProperties.presets[0].toString());

		String sqlWhere = lastFilter.getSqlWhere(Config.settings.GcLogin
				.getValue());
		CoreSettingsForward.Categories = new Categories();
		Database.Data.GPXFilenameUpdateCacheCount();

		synchronized (Database.Data.Query) {
			CacheListDAO cacheListDAO = new CacheListDAO();
			cacheListDAO.ReadCacheList(Database.Data.Query, sqlWhere);
		}

	}

	public static void InitialConfig() {

		if (Config.settings != null && Config.settings.isLoaded())
			return;

		// Read Config
		String workPath = "./cachebox";
		// nachschauen ob im aktuellen Ordner eine cachebox.db3 vorhanden ist und in diesem Fall den aktuellen Ordner als WorkPath verwenden
		File file = new File("./cachebox.db3");
		if (file.exists()) {
			workPath = "./";			
		}
		System.out.println("WorkPath: " + workPath);
		
		Config.Initialize(workPath);

		// hier muss die Config Db initialisiert werden
		try {
			Database.Settings = new CBServerDB(DatabaseType.Settings);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Database.Settings.StartUp(Config.WorkPath + "/User/Config.db3");

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