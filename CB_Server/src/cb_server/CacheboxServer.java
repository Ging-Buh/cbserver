package cb_server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cb_rpc.Rpc_Server;
import cb_server.DB.CBServerDB;
import cb_server.Import.ImportScheduler;
import CB_Core.CoreSettingsForward;
import CB_Core.FilterProperties;
import CB_Core.DAO.CacheListDAO;
import CB_Core.DB.Database;
import CB_Core.DB.Database.DatabaseType;
import CB_Core.Settings.CB_Core_Settings;
import CB_Core.Types.Categories;
import CB_Utils.Plattform;
import CB_Utils.Util.FileIO;
import Rpc.RpcFunctionsServer;

public class CacheboxServer {
	public static Logger log;

	public static void main(String[] args) throws Exception {
		
		Plattform.used=Plattform.Server;
		
		log = LoggerFactory.getLogger(CacheboxServer.class);
		writeLockFile("cbserver.lock");
		log.debug(System.getProperty("sun.net.http.allowRestrictedHeaders"));
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
		
		//copyWebContent();
		
		log.info("Hallo Jetty Vaadin Server");
		log.debug("Initialize Config");
		InitialConfig();
		InitialCacheDB();
		Config.settings.ReadFromDB();
		ImportScheduler.importScheduler.start();
		// Changed default Port to 8085
		int port = 8085;
		try {
			port = Integer.valueOf(args[0]);
		} catch (Exception ex) {
			// Default Port 80 einstellen
		}
		RpcFunctionsServer.jettyPort = port;
		@SuppressWarnings("unused")
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

		// Map
		ServletContextHandler mapContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		mapContext.setContextPath("/map");
		mapContext.addServlet(new ServletHolder(new MapServlet()), "/*");
		
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { webapp, webappImages, webappSpoiler, mapContext });
		
		server.setHandler(contexts);

		server.start();
		log.info("Vaadin Server started on port " + port);
		server.join();

	}

	
	/**
	 * Copy the WebContentFolder if need
	 */
	@SuppressWarnings("unused")
	private static void copyWebContent()
	{
		
		File root= new File("./");
		File WebContentFolder =new File(root.getAbsolutePath()+"/Webcontent");
		if(WebContentFolder.exists()&&WebContentFolder.isDirectory())
		{
			log.info("WebContentFolder exist, NOP");
		}
		else
		{
			log.info("Missing WebContentFolder, copy from Jar");

			 try
		     {
		          String destPath =root.getAbsolutePath()+"/WebContent/VAADIN";
		          String JarFolder="/VAADIN";
		          CopyJarFolder(destPath, JarFolder); 
		          
		           destPath =root.getAbsolutePath()+"/WebContent/WEB-INF";
		           JarFolder="/WEB-INF";
		          CopyJarFolder(destPath, JarFolder); 
		          
		           destPath =root.getAbsolutePath()+"/WebContent/META-INF";
		           JarFolder="/META-INF";
		          CopyJarFolder(destPath, JarFolder); 
		     }

		     catch(Exception e)
		     {
		    	 e.printStackTrace();
		     }
			
		}
		
		
	}


	private static void CopyJarFolder(String destPath, String JarFolder)
			throws URISyntaxException, FileNotFoundException, IOException {
		File dir = new File(destPath);  
		  dir.mkdirs();  
		  
		  URL url =CacheboxServer.class.getClass().getResource(JarFolder);
		  URI uri= new URI(url.toString());
		  
		  File resource = new File(uri);
		  File[] listResource = resource.listFiles();
		  String[] files=resource.list();
		  for (int i = 0; i < files.length; i++) 
		  {
			  if(listResource[i].isDirectory())
			  {
				  //Recursive call
				 String rcursiveJarFolder=JarFolder + "/" + listResource[i].getName();
				 String recursiveDestPath=destPath + "/" + listResource[i].getName();
				 CopyJarFolder(recursiveDestPath,rcursiveJarFolder);
				 continue;
			  }
			  
			   File dstfile1=new File(dir,files[i]);
		       FileInputStream is1 = new FileInputStream(listResource[i]);
		       FileOutputStream fos1 = new FileOutputStream(dstfile1);
		       int b1;
		       while((b1 = is1.read()) != -1) 
		       {
		            fos1.write(b1);
		       }
		       fos1.close();
		       is1.close();
		  }
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

		String sqlWhere = lastFilter.getSqlWhere(CB_Core_Settings.GcLogin.getValue());
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