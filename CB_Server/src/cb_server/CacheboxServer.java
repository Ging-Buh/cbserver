package cb_server;


import java.net.URI;
import java.net.URL;

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





public class CacheboxServer
{
    public static void main(String[] args) throws Exception
    {
    	System.out.println(System.getProperty("sun.net.http.allowRestrictedHeaders"));
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
		System.out.println("Hallo Jetty Vaadin Server");
    	System.out.println("Initialize Config");
    	InitialConfig();
    	InitialCacheDB();
  	
    	Rpc_Server rpcServer = new Rpc_Server(RpcFunctionsServer.class);
   
    	
//        Server server = new Server(8085); 
        Server server = new Server(80);
        
//        VAADIN Part
        WebAppContext webapp = new WebAppContext();
        webapp.setDescriptor("");
        webapp.setResourceBase("./WebContent");
        webapp.setContextPath("/cbserver");
        webapp.setParentLoaderPriority(true);
        
 // Images
        WebAppContext webappImages = new WebAppContext();
        webappImages.setDescriptor("");
        webappImages.setResourceBase("./cachebox/repository/images");
        webappImages.setContextPath("/images");
        webappImages.setParentLoaderPriority(true);

 // Spoiler
        WebAppContext webappSpoiler = new WebAppContext();
        webappSpoiler.setDescriptor("");
        webappSpoiler.setResourceBase("./cachebox/repository/spoilers");
        webappSpoiler.setContextPath("/spoilers");
        webappSpoiler.setParentLoaderPriority(true);

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { webapp, webappImages, webappSpoiler });
 
        server.setHandler(contexts);
       
        server.start();
        server.join();
    }
    
	private static void InitialCacheDB() {
		Database.Data.StartUp("./cachebox/cachebox.db3");
		FilterProperties lastFilter = new FilterProperties(FilterProperties.presets[0].toString());

		String sqlWhere = lastFilter.getSqlWhere(Config.settings.GcLogin.getValue());
		CoreSettingsForward.Categories = new Categories();
		Database.Data.GPXFilenameUpdateCacheCount();

		synchronized (Database.Data.Query)
		{
			CacheListDAO cacheListDAO = new CacheListDAO();
			cacheListDAO.ReadCacheList(Database.Data.Query, sqlWhere);
		}

	}

	
	public static void InitialConfig()
	{

		if (Config.settings != null && Config.settings.isLoaded()) return;

		// Read Config
		String workPath = "./cachebox";

		Config.Initialize(workPath);

		// hier muss die Config Db initialisiert werden
		try
		{
			Database.Settings = new CBServerDB(DatabaseType.Settings);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		Database.Settings.StartUp(Config.WorkPath + "/User/Config.db3");

		try
		{
			Database.Data = new CBServerDB(DatabaseType.CacheBox);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		try
		{
			Database.FieldNotes = new CBServerDB(DatabaseType.FieldNotes);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		if (!FileIO.createDirectory(Config.WorkPath + "/User")) return;
		Database.FieldNotes.StartUp(Config.WorkPath + "/User/FieldNotes.db3");
	}

}