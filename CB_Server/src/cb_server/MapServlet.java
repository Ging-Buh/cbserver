package cb_server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mapsforge.map.model.DisplayModel;

import CB_Locator.Map.Descriptor;
import CB_Locator.Map.Layer;
import CB_Locator.Map.Layer.Type;
import CB_Locator.Map.TileGL;
import de.Map.DesktopManager;

public class MapServlet extends HttpServlet {
	private static final long serialVersionUID = 2094731483963312861L;
	private String greeting = "Hello World";

	public MapServlet() {
		DisplayModel model = new DisplayModel();
		DesktopManager manager = new DesktopManager(model);
		Layer layer = new Layer(Type.normal, "Freizeitkarte_BAYERN", "Freizeitkarte_BAYERN", "./cachebox/repository/maps/Freizeitkarte_BAYERN.map");
		layer.isMapsForge = true;
		Descriptor desc = new Descriptor(34986, 22738, 16, false);
//		TileGL tile = manager.getMapsforgePixMap(layer, desc, 1);
	}

	public MapServlet(String greeting) {
		this();
		this.greeting = greeting;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("<h1>" + greeting + "</h1>");
		response.getWriter().println("session=" + request.getSession(true).getId());
	}
}
