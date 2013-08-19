package cb_server;

import CB_Core.DB.Database;
import CB_Core.Settings.SettingsClass_Core;
import CB_Utils.DB.Database_Core;
import CB_Utils.Settings.SettingCategory;
import CB_Utils.Settings.SettingInt;
import CB_Utils.Settings.SettingModus;
import CB_Utils.Settings.SettingStoreType;


public class SettingsClass extends SettingsClass_Core {

	private static final long serialVersionUID = 3368580647613308244L;

	public SettingInt Port;

	public SettingsClass() {
		super();
		SettingCategory cat = SettingCategory.Login;
		
		Port = new SettingInt("Port", cat, SettingModus.Normal, 7765, SettingStoreType.Global);
		
		this.add(Port);
	}
	
	@Override
	protected Database_Core getSettingsDB() {
		return Database.Settings;
	}

	@Override
	protected Database_Core getDataDB() {
		return Database.Data;
	}

}
