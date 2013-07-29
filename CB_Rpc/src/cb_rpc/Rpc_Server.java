package cb_rpc;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

public class Rpc_Server {
	// WebServer f�r die RPC-Communication
	private WebServer webServer = null;

	public Rpc_Server() {
		startWebserver();
	}

	/**
	 * Startet den Webserver, der die XmlRpc-Meldungen empf�ngt
	 */
	private void startWebserver() {
		try {
			webServer = new WebServer(9911);

			XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

			PropertyHandlerMapping phm = new PropertyHandlerMapping();

			phm.addHandler("Rpc_Functions", cb_rpc.Rpc_Functions.class);

			xmlRpcServer.setHandlerMapping(phm);

			webServer.setParanoid(false);
			XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
			serverConfig.setEnabledForExtensions(true);
			serverConfig.setContentLengthOptional(false);

			webServer.start();
			System.out.println("RpcWebServer started");
		} catch (Exception ex) {
			System.out.println("Error starting RpcWebServer: " + ex.getMessage());
		}

	}

}