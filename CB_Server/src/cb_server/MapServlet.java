package cb_server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.TileBitmap;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mapsforge.map.awt.AwtResourceBitmap;
import org.mapsforge.map.awt.AwtTileBitmap;
import org.mapsforge.map.awt.ext_AwtGraphicFactory;
import org.mapsforge.map.awt.ext_AwtTileBitmap;
import org.mapsforge.map.layer.renderer.DatabaseRenderer;
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
	private DatabaseRenderer databaseRenderer;
	private File mapFile;
	private ExternalRenderTheme renderTheme;
	private DisplayModel model;
	private static Object syncObject = new Object();
	public MapServlet() {
		model = new DisplayModel();
		DesktopManager manager = new DesktopManager(model);

		mapFile = new File("./cachebox/repository/maps/germany.map");
		File RenderThemeFile = new File("./cachebox/repository/maps/osmarender/osmarender.xml");
		renderTheme = null;
		try {
			renderTheme = new ExternalRenderTheme(RenderThemeFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		MapDatabase MF_mapDatabase = new MapDatabase();
		MF_mapDatabase.closeFile();
		MF_mapDatabase.openFile(mapFile);

		GraphicFactory Mapsforge_Factory = AwtGraphicFactory.INSTANCE;
		databaseRenderer = new DatabaseRenderer(MF_mapDatabase, Mapsforge_Factory);
		/*
				Tile ti = new Tile(34986, 22738, (byte) 16);
				RendererJob job = new RendererJob(ti, mapFile, renderTheme, model, 1, false);
				TileBitmap tile = databaseRenderer.executeJob(job);

				// Oder auf der Platte Speichern 
				AwtTileBitmap bmp = (AwtTileBitmap) tile;
			try {	
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				
					bmp.compress(os);
				
				
				byte[] imageInByte = os.toByteArray();
				
				InputStream in = new ByteArrayInputStream(imageInByte);
				BufferedImage bufferedImage = ImageIO.read(in);
	 

				File outputfile = new File("./cachebox/repository/maps/testimage.jpg");
				try {
					ImageIO.write(bufferedImage, "jpg", outputfile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			*/
	}

	public MapServlet(String greeting) {
		this();
		this.greeting = greeting;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		synchronized (syncObject) {
			response.setContentType("image/png");
			response.setStatus(HttpServletResponse.SC_OK);

			Tile ti = new Tile(34986, 22738, (byte) 16);
			RendererJob job = new RendererJob(ti, mapFile, renderTheme, model, 1, false);
			TileBitmap tile = databaseRenderer.executeJob(job);

			try {
				tile.compress(response.getOutputStream());
				//			tile.compress(os);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
