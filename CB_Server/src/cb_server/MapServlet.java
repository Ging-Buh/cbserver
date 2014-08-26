/* 
 * Copyright (C) 2011-2014 team-cachebox.de
 *
 * Licensed under the : GNU General Public License (GPL);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cb_server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.TileBitmap;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mapsforge.map.layer.renderer.DatabaseRenderer;
import org.mapsforge.map.layer.renderer.RendererJob;
import org.mapsforge.map.model.DisplayModel;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.rendertheme.ExternalRenderTheme;

import de.Map.DesktopManager;

public class MapServlet extends HttpServlet {
	private static final long serialVersionUID = 2094731483963312861L;
	private final DatabaseRenderer databaseRenderer;
	private final File mapFile;
	private ExternalRenderTheme renderTheme;
	private final DisplayModel model;
	private static Object syncObject = new Object();

	public MapServlet() {
		model = new DisplayModel();
		new DesktopManager(model);

		mapFile = new File(Config.WorkPath + "/repository/maps/germany.map");
		File RenderThemeFile = new File(Config.WorkPath + "/repository/maps/osmarender/osmarender.xml");
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
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		synchronized (syncObject) {

			String sQuery = request.getPathInfo().replace(".png", "");
			String[] query = sQuery.split("/");
			int z = Integer.valueOf(query[1]);
			int x = Integer.valueOf(query[2]);
			int y = Integer.valueOf(query[3]);

			response.setContentType("image/png");
			response.setStatus(HttpServletResponse.SC_OK);

			Tile ti = new Tile(x, y, (byte) z);
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
