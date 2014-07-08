package cb_server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.ResourceBitmap;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.graphics.TileBitmap;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mortbay.log.Log;

public class IconServlet extends HttpServlet {
	private static final long serialVersionUID = 1205779103262021876L;

	public IconServlet() {

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sQuery = request.getPathInfo();
		String[] query = sQuery.split("/");
		int size = Integer.parseInt(query[1]);
		int cacheTyp = Integer.parseInt(query[2]);
		boolean selected = query[3].equals("1");
		boolean deactivated = query[4].equals("0");
		boolean archived = query.length > 5 ? query[5].equals("1") : false;
		boolean found = query.length > 6 ? query[6].equals("1") : false;
		boolean owner = query.length > 7 ? query[7].equals("1") : false;
		boolean background = query.length > 8 ? query[8].equals("1") : false;
		int difficulty = query.length > 9 ? Integer.parseInt(query[9]) : 0;
		int terrain = query.length > 9 ? Integer.parseInt(query[10]) : 0;

		response.setContentType("image/png");
		response.setStatus(HttpServletResponse.SC_OK);
		InputStream is = null;
		InputStream is2 = null;
		ResourceBitmap bmp = null;
		ResourceBitmap bmp2 = null;
		if (background) {
			if (selected) {
				is = getClass().getResourceAsStream("/icons/shaddowrect-selected.png");
			} else {
				is = getClass().getResourceAsStream("/icons/shaddowrect.png");
			}
			bmp = AwtGraphicFactory.INSTANCE.createResourceBitmap(is, 0);
			is2 = getClass().getResourceAsStream("/icons/" + cacheTyp + ".png");
			bmp2 = AwtGraphicFactory.INSTANCE.createResourceBitmap(is2, 0);
		} else {
			is = getClass().getResourceAsStream("/icons/" + cacheTyp + ".png");
			bmp = AwtGraphicFactory.INSTANCE.createResourceBitmap(is, 0);
		}

		Canvas canvas = AwtGraphicFactory.INSTANCE.createCanvas();
		canvas.setBitmap(bmp);
		if (bmp2 != null) {
			canvas.drawBitmap(bmp2, 8, 8);
		}

		if (deactivated) {
			// Roter durchstreichen
			Paint p = AwtGraphicFactory.INSTANCE.createPaint();
			p.setColor(Color.RED);
			p.setStrokeWidth(4);
			p.setStyle(Style.STROKE);
			canvas.drawLine(5, 5, 42, 42, p);
		}
		try {
			bmp.compress(response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
