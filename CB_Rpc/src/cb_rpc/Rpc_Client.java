package cb_rpc;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class Rpc_Client {
	// XmlRpc Objecte für den Zugriff auf den RPC-Server
	private XmlRpcClient client = null;

	public Rpc_Client() {
		
	}
	
	/**
	 * Erstellt die Config-Objecte für den Zugriff auf den PCharge-Server über XmlRpc
	 */
	private void createRpcConfig() {
		client = null;
		// create configuration
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		
		try {
			URL url = new URL("http://192.168.100.115:9911/xmlrpc");
			config.setServerURL(url);
		} catch (MalformedURLException e) {
			System.out.println("SendRpcToPChargeServer - Error: " + e.getMessage());
			return;
		}
		config.setEnabledForExtensions(true);

		client = new XmlRpcClient();
		client.setConfig(config);		
	}

	/**
	 * Sendet eine XmlRpc-Nachricht an den RPC-Server und liefert dessen
	 * Antwort zurück
	 * 
	 * @param message
	 * @return
	 */
	public Integer sendRpcToPChargeServer() {
		if (client == null)
			createRpcConfig();
		if (client == null) {
			System.out.println("SendRpcToServer - Cannot create Client!");
			return new Integer(-1);
		}
		try {
			System.out.println("SendRpcToServer");
			Object obj = client.execute("Rpc_Functions.Add", new Object[] { new Integer(1), new Integer(2) });
			if ((obj == null) || (!(obj instanceof Integer))) {
				System.out.println("SendRpcToServer - Result == null");
				return new Integer(-1);
			} else {
				System.out.println("SendRpcToServer - Result = " + obj.toString());
			//	return (int) obj;
				Integer i = (Integer) obj;
				return i;
			}
		} catch (Exception ex) {
			System.out.println(ex.toString() + " - " + ex.getMessage());
			return new Integer(-1);
		}

	}


}


