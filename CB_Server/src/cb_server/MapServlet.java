package cb_server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.TileBitmap;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.awt.ext_AwtGraphicFactory;
import org.mapsforge.map.awt.ext_AwtTileBitmap;
import org.mapsforge.map.layer.renderer.SERVER_DatabaseRenderer;
import org.mapsforge.map.layer.renderer.RendererJob;
import org.mapsforge.map.model.DisplayModel;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.rendertheme.ExternalRenderTheme;

import CB_Locator.Map.Descriptor;
import CB_Locator.Map.Layer;
import CB_Locator.Map.Layer.Type;
import CB_Locator.Map.ManagerBase;
import CB_Locator.Map.TileGL;
import de.Map.DesktopManager;

public class MapServlet extends HttpServlet {
	private static final long serialVersionUID = 2094731483963312861L;
	private String greeting = "Hello World";

	public MapServlet() {
		DisplayModel model = new DisplayModel();
		DesktopManager manager = new DesktopManager(model);
				
		File mapFile=new File("./cachebox/repository/maps/germany.map");
		File RenderThemeFile=new File("./cachebox/repository/maps/osmarender/osmarender.xml");
		ExternalRenderTheme renderTheme = null;
		try {
			renderTheme = new ExternalRenderTheme(RenderThemeFile);
		} catch (FileNotFoundException e) {
						e.printStackTrace();
		}
		
		MapDatabase	MF_mapDatabase = new MapDatabase();
		MF_mapDatabase.closeFile();
		MF_mapDatabase.openFile(mapFile);
		
		GraphicFactory Mapsforge_Factory =new ext_AwtGraphicFactory(1);
		SERVER_DatabaseRenderer databaseRenderer = new SERVER_DatabaseRenderer(MF_mapDatabase, Mapsforge_Factory);
		
		
		Tile ti=new Tile(34986, 22738, (byte) 16);
		RendererJob job = new RendererJob(ti, mapFile, renderTheme, model, 1, false);
		TileBitmap tile= databaseRenderer.executeJob(job);
		
		
		// Hi Hubert, wie willst Du das Image zur�ck geben?
		// Als OutputStream? Dann w�re das der Richtige Code, um es als PNG zur�ck zu geben.
		
		OutputStream os= new ByteArrayOutputStream();
		
		try {
			tile.compress(os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Oder auf der Platte Speichern 
		ext_AwtTileBitmap bmp=(ext_AwtTileBitmap) tile;
		BufferedImage bufferedImage =bmp.getBufferedImage();
		
		File outputfile = new File("./cachebox/repository/maps/testimage.jpg");
		try {
			ImageIO.write(bufferedImage, "jpg", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
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
