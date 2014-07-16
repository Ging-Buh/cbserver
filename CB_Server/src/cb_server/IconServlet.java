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
import org.mapsforge.core.graphics.Matrix;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.ResourceBitmap;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.graphics.TileBitmap;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mortbay.log.Log;

import CB_Core.Enums.CacheTypes;

public class IconServlet extends HttpServlet {
	private static final long serialVersionUID = 1205779103262021876L;

	public IconServlet() {

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sQuery = request.getPathInfo().substring(1);
		int pos = sQuery.indexOf(".");
		if (pos > 0) sQuery = sQuery.substring(0, pos);
		String[] query = sQuery.split("_");
		
		boolean cache = true;
		int cacheType = 0;
		boolean deactivated = false;
		boolean archived = false;
		boolean owner = false;
		boolean found = false;
		boolean solved = false;
		boolean hasStart = false;
		boolean selected = false;
		int difficulty = 0;
		int terrain = 0;
		int size = 0;
		int background = 0;
		for (int i = 0; i < query.length; i++) {
			switch (query[i].charAt(0)) {
			case 'C':	// Cache
			case 'W':   // Waypoint
				if (query[i].charAt(0) == 'W') 
					cache = false;
				cacheType = Integer.parseInt(query[i].substring(1, 3));
				deactivated = query[i].contains("D");
				archived = query[i].contains("A");
				found = query[i].contains("F");
				owner = query[i].contains("O");
				solved = query[i].contains("S");
				hasStart = query[i].contains("T");
				selected = query[i].contains("L");
				break;
			case 'D':	// Difficulty
				difficulty = Integer.parseInt(query[i].substring(1));
				break;
			case 'T':	// Terrain
				terrain = Integer.parseInt(query[i].substring(1));
				break;
			case 'B':	// Background size
				background = Integer.parseInt(query[i].substring(1));
				break;
			case 'S':	// Image size 
				size = Integer.parseInt(query[i].substring(1));
				break;
			}
		}
		if (background <= size) {
			// background wird == size übergeben. Wenn background nicht > size ist -> keinen Hintergrund
			background = 0;
		}
		
		String prefix = "32-";	// Prefix für die Icon-Dateien
		if (size <= 15) prefix = "15-";
		String postfix = "";
		if (solved) postfix = "S";
		String fileName = "/icons/" + prefix + cacheType + postfix + ".png";
		if (found) {
			fileName = "/icons/" + prefix + "Found.png";
		}

		response.setContentType("image/png");
		response.setStatus(HttpServletResponse.SC_OK);
		InputStream is = null;
		InputStream is2 = null;
		ResourceBitmap bmp = null;
		ResourceBitmap bmp2 = null;
		if (background > 0) {
			if (selected) {
				is = getClass().getResourceAsStream("/icons/shaddowrect-selected.png");
			} else {
				is = getClass().getResourceAsStream("/icons/shaddowrect.png");
			}
			bmp = AwtGraphicFactory.INSTANCE.createResourceBitmap(is, 0);
			is2 = getClass().getResourceAsStream(fileName);
			bmp2 = AwtGraphicFactory.INSTANCE.createResourceBitmap(is2, 0);
		} else {
			is = getClass().getResourceAsStream(fileName);
			bmp = AwtGraphicFactory.INSTANCE.createResourceBitmap(is, 0);
			background = size;
		}	

		TileBitmap bitmap = AwtGraphicFactory.INSTANCE.createTileBitmap(background, true);
		Canvas canvas = AwtGraphicFactory.INSTANCE.createCanvas();
		canvas.setBitmap(bitmap);
		Matrix matrix = AwtGraphicFactory.INSTANCE.createMatrix();
		matrix.scale((float) background / (float) bmp.getWidth(), (float) background / (float) bmp.getHeight());
		canvas.drawBitmap(bmp, matrix);
		if (bmp2 != null) {
			matrix = AwtGraphicFactory.INSTANCE.createMatrix();
			matrix.translate((background - size) / 2, (background - size) / 2);
			matrix.scale((float) size / (float) bmp2.getWidth(), (float) size / (float) bmp2.getHeight());
			canvas.drawBitmap(bmp2, matrix);
		}
		if (deactivated || archived) {
			// Roter durchstreichen
			Paint p = AwtGraphicFactory.INSTANCE.createPaint();
			int rand = 5;
			int width = 4;
			if (background <= 15) { 
				rand = 3;
				width = 2;
			}
			p.setColor(Color.RED);
			p.setStrokeWidth(width);
			p.setStyle(Style.STROKE);
			canvas.drawLine(rand, rand, background - rand, background - rand, p);
		}
		try {
			bitmap.compress(response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	protected void doGet_(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sQuery = request.getPathInfo();
		String[] query = sQuery.split("/");
		int size = Integer.parseInt(query[1]);
		int cacheTyp = Integer.parseInt(query[2]);
		boolean selected = query[3].equals("1");
		boolean deactivated = query[4].equals("0");
		deactivated = false;
		boolean archived = query.length > 5 ? query[5].equals("1") : false;
		boolean found = query.length > 6 ? query[6].equals("1") : false;
		boolean owner = query.length > 7 ? query[7].equals("1") : false;
		int backgroundSize = query.length > 8 ? Integer.parseInt(query[8]) : 0;
		int difficulty = query.length > 9 ? Integer.parseInt(query[9]) : 0;
		int terrain = query.length > 10 ? Integer.parseInt(query[10]) : 0;

		// Größen umrechnen
		switch (size) {
		case 0:
			size = 15;
			backgroundSize = 0;
			break;
		case 1:
			size = 16;
			if (backgroundSize > 0) backgroundSize = 20;
			break;
		case 2:
			size = 32;
			if (backgroundSize > 0) backgroundSize = 48;
			break;
		}

		response.setContentType("image/png");
		response.setStatus(HttpServletResponse.SC_OK);
		InputStream is = null;
		InputStream is2 = null;
		ResourceBitmap bmp = null;
		ResourceBitmap bmp2 = null;
		if (backgroundSize > 0) {
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
			backgroundSize = size;
		}

		TileBitmap bitmap = AwtGraphicFactory.INSTANCE.createTileBitmap(backgroundSize, true);
		Canvas canvas = AwtGraphicFactory.INSTANCE.createCanvas();
		canvas.setBitmap(bitmap);
		Matrix matrix = AwtGraphicFactory.INSTANCE.createMatrix();
		matrix.scale((float) backgroundSize / (float) bmp.getWidth(), (float) backgroundSize / (float) bmp.getHeight());
		canvas.drawBitmap(bmp, matrix);
		if (bmp2 != null) {
			matrix = AwtGraphicFactory.INSTANCE.createMatrix();
			matrix.translate((backgroundSize - size) / 2, (backgroundSize - size) / 2);
			matrix.scale((float) size / (float) bmp2.getWidth(), (float) size / (float) bmp2.getHeight());
			canvas.drawBitmap(bmp2, matrix);
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
			bitmap.compress(response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	


}
