package cb_server;

import CB_Core.DB.Database;
import CB_Core.DB.Database_Core;
import CB_Core.Settings.SettingCategory;
import CB_Core.Settings.SettingInt;
import CB_Core.Settings.SettingModus;
import CB_Core.Settings.SettingStoreType;
import CB_Core.Settings.SettingString;
import CB_Core.Settings.SettingsList;

public class SettingsClass extends SettingsList {

	private static final long serialVersionUID = 3368580647613308244L;

	public SettingInt Port;
	public SettingString GcLogin;

	public SettingsClass() {
		SettingCategory cat = SettingCategory.Login;
		
		addSetting(GcLogin = new SettingString("GcLogin", cat, SettingModus.Normal, "", SettingStoreType.Global));
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
