package cb_server;

import CB_Utils.Config_Core;



public class Config extends Config_Core {
	public static SettingsClass settings;

	public static void Initialize(String workPath)
	{
		WorkPath = workPath;
		settings = new SettingsClass();
	}

	

}
