package CB_TestRpc;

import CB_Core.Types.Cache;
import CB_RpcCore.Functions.RpcAnswer_GetCacheList;
import CB_RpcCore.Functions.RpcAnswer_GetExportList;
import CB_RpcCore.Functions.RpcMessage_GetCacheList;
import CB_RpcCore.Functions.RpcMessage_GetExportList;
import cb_rpc.Rpc_Client;
import cb_rpc.Functions.RpcAnswer;
import cb_rpc.Functions.RpcMessage;

public class TestRpc {
    public static void main(String[] args) throws Exception {
    	System.out.println("Test started");
    	Rpc_Client client = new Rpc_Client();
    	RpcAnswer answer = client.sendRpcToServer(new RpcMessage_GetExportList());
    	if (answer instanceof RpcAnswer_GetExportList) {
    		String s = answer.toString();
    		System.out.println(((RpcAnswer_GetExportList) answer).getList().size());
    	}
    	
    	answer = client.sendRpcToServer(new RpcMessage_GetCacheList(1, 0, 10));
    	if (answer instanceof RpcAnswer_GetCacheList) {
    		RpcAnswer_GetCacheList gclAnswer = (RpcAnswer_GetCacheList) answer;
    		System.out.println("************* CacheList ***************");
//    		for (Cache cache : gclAnswer.getCacheList()) {
//    			System.out.println(cache.Name);
//    		}
     	}
    }

}
