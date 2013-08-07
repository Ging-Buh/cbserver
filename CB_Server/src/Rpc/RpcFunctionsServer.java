package Rpc;

import CB_RpcCore.Functions.RpcAnswer_GetExportList;
import CB_RpcCore.Functions.RpcMessage_GetExportList;
import cb_rpc.Functions.RpcAnswer;
import cb_rpc.Functions.RpcMessage;
import cb_server.DAO.GetExportListDao;

public class RpcFunctionsServer {

	public RpcAnswer Msg(RpcMessage message) {
		if (message instanceof RpcMessage_GetExportList) {
			GetExportListDao dao = new GetExportListDao();
			
			RpcAnswer answer = dao.getList();
			return answer;
		}
		return new RpcAnswer(-1);
	}
	
	public int Add(int i, int j) {
		// TODO Auto-generated method stub
		return (i + j) * 2;
	}
}
