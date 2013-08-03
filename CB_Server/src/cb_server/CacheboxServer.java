package cb_server;


import java.net.URI;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import cb_rpc.Rpc_Server;
import cb_server.DB.CBServerDB;

import CB_Core.CoreSettingsForward;
import CB_Core.FilterProperties;
import CB_Core.DAO.CacheListDAO;
import CB_Core.DB.Database;
import CB_Core.DB.Database.DatabaseType;
import CB_Core.Types.Categories;
import CB_Core.Util.FileIO;





public class CacheboxServer
{
    public static void main(String[] args) throws Exception
    {
    	System.out.println(System.getProperty("sun.net.http.allowRestrictedHeaders"));
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
		System.out.println("Hallo Jetty Vaadin Server");
    	System.out.println("Initialize Config");
    	InitialConfig();
    	Config.settings.ReadFromDB();
    	Config.settings.WriteToDB();
    	InitialCacheDB();
  	
    	Rpc_Server rpcServer = new Rpc_Server();
   
    	
        Server server = new Server(8085); 
        
        WebAppContext webapp = new WebAppContext();
        
        webapp.setDescriptor("");
        webapp.setResourceBase("./WebContent");
        webapp.setContextPath("/cbserver");
        
        webapp.setParentLoaderPriority(true);
        server.setHandler(webapp);
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