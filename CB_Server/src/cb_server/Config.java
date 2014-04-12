package cb_server;

import CB_Utils.Config_Core;



public class Config extends Config_Core {
	
	public Config(String workPath) {
		super(workPath);
			}

	public static SettingsClass settings;

	public static void Initialize(String workPath)
	{
		WorkPath = workPath;
		settings = new SettingsClass();
		
	}

	@Override
	protected void acceptChanges() {
		// TODO Auto-generated method stub
		
	}

	

}
