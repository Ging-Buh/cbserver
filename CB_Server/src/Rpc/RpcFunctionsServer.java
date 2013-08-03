package Rpc;

import cb_rpc.Functions.RpcAnswer;

public class RpcFunctionsServer {

	public RpcAnswer Msg(Integer i) {
		// TODO Auto-generated method stub
		return new RpcAnswer(-1);
	}
	
	public int Add(int i, int j) {
		// TODO Auto-generated method stub
		return (i + j) * 2;
	}
}
