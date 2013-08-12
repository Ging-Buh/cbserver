package Rpc;

import java.util.ArrayList;
import java.util.HashMap;

import CB_Core.DAO.CacheListDAO;
import CB_Core.DAO.LogDAO;
import CB_Core.DB.Database;
import CB_Core.Types.Cache;
import CB_Core.Types.CacheList;
import CB_Core.Types.LogEntry;
import CB_RpcCore.Functions.RpcAnswer_GetCacheList;
import CB_RpcCore.Functions.RpcAnswer_GetExportList;
import CB_RpcCore.Functions.RpcMessage_GetCacheList;
import CB_RpcCore.Functions.RpcMessage_GetExportList;
import cb_rpc.Functions.RpcAnswer;
import cb_rpc.Functions.RpcMessage;
import cb_server.DAO.GetExportListDao;

public class RpcFunctionsServer {
	// speichert geladene CacheLists anhand der Categoriy
	private static HashMap<Long, CacheList> loadedCacheLists = new HashMap<Long, CacheList>();
	
	public RpcAnswer Msg(RpcMessage message) {
		if (message instanceof RpcMessage_GetExportList) {
			GetExportListDao dao = new GetExportListDao();
			
			RpcAnswer answer = dao.getList();
			return answer;
		} else if (message instanceof RpcMessage_GetCacheList) {
			RpcMessage_GetCacheList msg = (RpcMessage_GetCacheList) message;
			
			CacheList loadedCacheList = null;
			if (msg.getStartIndex() == 0) {
				// erster Aufruf -> CachListe erzeugen und aus DB laden
				if (loadedCacheLists.containsKey(msg.getCategoryId())) {
					// bereits vorhandene CacheList entfernen damit diese neu geladen werden kann
					loadedCacheLists.remove(msg.getCategoryId());
				}
				loadedCacheList = new CacheList();
				String joinString = "INNER JOIN GPXFilenames gpx on GpxFilename_Id=gpx.Id";
				String whereString = "gpx.CategoryId=" + msg.getCategoryId();
				CacheListDAO dao = new CacheListDAO();
				dao.ReadCacheList(loadedCacheList, joinString, whereString, true);
				// geladene CacheList zur Liste der gespeicherten CacheLists hinzufügen
				loadedCacheLists.put(msg.getCategoryId(), loadedCacheList);
			} else {
				// CacheList müsste bereits geladen sein -> nur noch daraus die entsprechenden Caches übertragen
				if (loadedCacheLists.containsKey(msg.getCategoryId())) {
					loadedCacheList = loadedCacheLists.get(msg.getCategoryId());
				}
			}
			if (loadedCacheList != null) {
				CacheList cacheList = new CacheList();

				int start = msg.getStartIndex();
				int count = msg.getCount();
				boolean dataAvailable = start + count < loadedCacheList.size() - 1;
				for (int i = start; i < start + count; i++) {
					if (i >= loadedCacheList.size()) {
						break;	// keine weiteren Daten
					}
					
					Cache cache = loadedCacheList.get(i);
					cacheList.add(cache);
				}
				
				RpcAnswer_GetCacheList answer = new RpcAnswer_GetCacheList(0);
				
				for (Cache cache : cacheList){
//					cache.longDescription = Database.GetDescription(cache);
					ArrayList<LogEntry> logs = Database.Logs(cache);
					int maxLogCount = 10;
					int actLogCount = 0;
					for (LogEntry log : logs) {
						actLogCount++;
						if (actLogCount > maxLogCount)
							break;
						answer.addLog(log);
					}
				}
				answer.setCacheList(cacheList);
				answer.setDataAvailable(dataAvailable);
				return answer;
			} else {
				// Fehler, keine CacheList geladen
				RpcAnswer_GetCacheList answer = new RpcAnswer_GetCacheList(-1);
				return answer;
			}
		}
		return new RpcAnswer(-1);
	}
	
	public int Add(int i, int j) {
		// TODO Auto-generated method stub
		return (i + j) * 2;
	}
}
