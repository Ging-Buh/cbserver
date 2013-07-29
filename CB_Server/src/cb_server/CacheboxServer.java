package cb_server;


import java.net.URI;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import cb_server.DB.CBServerDB;

import CB_Core.DB.Database;
import CB_Core.DB.Database.DatabaseType;
import CB_Core.Util.FileIO;





public class CacheboxServer
{
    public static void main(String[] args) throws Exception
    {
    	System.out.println("Hallo Jetty Vaadin Server");
    	System.out.println("Initialize Config");
/*    	InitialConfig();
    	Config.settings.ReadFromDB();
		System.out.println("Port: " + Config.settings.Port.getValue());
        Config.settings.Port.setValue(8876);
        Config.settings.WriteToDB();
  */  	
    	
    	
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