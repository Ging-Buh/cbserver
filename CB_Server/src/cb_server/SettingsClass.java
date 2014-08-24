/* 
 * Copyright (C) 2011-2014 team-cachebox.de
 *
 * Licensed under the : GNU General Public License (GPL);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import CB_Utils.Settings.SettingUsage;
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
		
		Port = new SettingInt("Port", cat, SettingModus.Normal, 7765, SettingStoreType.Global,SettingUsage.CBS);
		PQImportInterval = new SettingInt("PQImportInterval (hours)", SettingCategory.API, SettingModus.Normal, 0, SettingStoreType.Global,SettingUsage.CBS);
		PQImportInterval.addChangedEventListner(new iChanged() {
			@Override
			public void isChanged() {
				ImportScheduler.importScheduler.start();
			}
		});
		PQImportNames = new SettingString("PQImportNames", SettingCategory.API, SettingModus.Normal, "", SettingStoreType.Local,SettingUsage.CBS);
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
