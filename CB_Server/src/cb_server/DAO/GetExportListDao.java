package cb_server.DAO;

import cb_rpc.Functions.RpcAnswer;
import cb_rpc.Functions.RpcAnswer_Error;
import CB_Core.DB.CoreCursor;
import CB_Core.DB.Database;
import CB_RpcCore.Functions.RpcAnswer_GetExportList;

public class GetExportListDao {

	public RpcAnswer getList() {

		try {
			CoreCursor reader = Database.Data.rawQuery(
					"select Id, Description from SdfExport", null);

			RpcAnswer_GetExportList result = new RpcAnswer_GetExportList(0);
			if (reader.getCount() > 0) {
				reader.moveToFirst();
				while (reader.isAfterLast() == false) {
					int id = reader.getInt(0);
					String desc = reader.getString(1);
					result.addListItem(id, desc);
					reader.moveToNext();
				}
			}
			reader.close();
			return result;
		} catch (Exception ex) {
			RpcAnswer result = new RpcAnswer_Error(-1, ex.getMessage());
			return result;
		}

	}

}
