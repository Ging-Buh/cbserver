package cb_server;

import cb_server.Import.ImportScheduler;
import CB_Core.DB.Database;
import CB_Core.Settings.CB_Core_Settings;
import CB_Utils.DB.Database_Core;
import CB_Utils.Settings.SettingBool;
import CB_Utils.Settings.SettingCategory;
import CB_Utils.Settings.SettingInt;
import CB_Utils.Settings.SettingModus;
import CB_Utils.Settings.SettingStoreType;
import CB_Utils.Settings.SettingString;
import CB_Utils.Settings.SettingsList;
import CB_Utils.Util.iChanged;


public class SettingsClass extends SettingsList implements CB_Core_Settings {

	private static final long serialVersionUID = 3368580647613308244L;

	public SettingInt Port;
	public SettingInt PQImportInterval;	// in Hours
	public SettingString PQImportNames; // PQ's to import automatically, ";" seperated

	public SettingsClass() {
		super();
		SettingCategory cat = SettingCategory.Login;
		
		Port = new SettingInt("Port", cat, SettingModus.Normal, 7765, SettingStoreType.Global);
		PQImportInterval = new SettingInt("PQImportInterval (hours)", SettingCategory.API, SettingModus.Normal, 0, SettingStoreType.Global);
		PQImportInterval.addChangedEventListner(new iChanged() {
			@Override
			public void isChanged() {
				ImportScheduler.importScheduler.start();
			}
		});
		PQImportNames = new SettingString("PQImportNames", SettingCategory.API, SettingModus.Normal, "", SettingStoreType.Local);
		this.add(Port);
		this.add(PQImportInterval);
		this.add(PQImportNames);
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
