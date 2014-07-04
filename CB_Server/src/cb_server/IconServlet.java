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
		//		FileInputStream fis = new FileInputStream("D:\\Cachebox\\CBServer\\CBServer_GIT\\CB_Server\\WebContent\\VAADIN\\themes\\cb_server\\icons\\0.png");
		//	       try {
		//	            int c;
		//	            while ((c = fis.read()) != -1) {
		//	            response.getWriter().write(c);
		//	            }
		//	        } finally {
		//	            if (fis != null) 
		//	                fis.close();
		//	                response.getWriter().close();
		//	        }
		response.setContentType("image/png");
		response.setStatus(HttpServletResponse.SC_OK);
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		InputStream is = null;
		if (selected) {
			is = getClass().getResourceAsStream("/icons/shaddowrect-selected.png");			
		} else {
			is = getClass().getResourceAsStream("/icons/shaddowrect.png");
		}
		ResourceBitmap bmp = AwtGraphicFactory.INSTANCE.createResourceBitmap(is, 0);
		InputStream is2 = getClass().getResourceAsStream("/icons/" + cacheTyp + ".png");
		ResourceBitmap bmp2 = AwtGraphicFactory.INSTANCE.createResourceBitmap(is2, 0);
		
		
//		TileBitmap bitmap = AwtGraphicFactory.INSTANCE.createTileBitmap(32, true);
		Canvas canvas = AwtGraphicFactory.INSTANCE.createCanvas();
		canvas.setBitmap(bmp);
		canvas.drawBitmap(bmp2, 8, 8);
		// Weiﬂer Hintergrund
//		canvas.fillColor(Color.WHITE);
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
