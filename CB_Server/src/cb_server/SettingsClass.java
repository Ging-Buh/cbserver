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

import CB_Core.DB.Database;
import CB_Core.Settings.CB_Core_Settings;
import CB_UI_Base.settings.CB_UI_Base_Settings;
import CB_Utils.DB.Database_Core;
import CB_Utils.Settings.SettingsList;
import CB_Utils.Util.iChanged;
import cb_rpc.Settings.CB_Rpc_Settings;
import cb_server.Import.ImportScheduler;

public class SettingsClass extends SettingsList implements CBS_Settings, CB_Core_Settings, CB_UI_Base_Settings, CB_Rpc_Settings {

	private static final long serialVersionUID = 3368580647613308244L;

	public SettingsClass() {
		super();

		PQImportInterval.addChangedEventListner(new iChanged() {
			@Override
			public void isChanged() {
				ImportScheduler.importScheduler.start();
			}
		});
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
