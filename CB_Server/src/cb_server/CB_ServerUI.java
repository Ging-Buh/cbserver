package cb_server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.shared.BaseLayer;
import org.vaadin.addon.leaflet.shared.Control;
import org.vaadin.addon.leaflet.shared.Point;
import org.vaadin.artur.icepush.ICEPush;

import CB_Core.DB.Database;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;

@SuppressWarnings("serial")
@Theme("cb_server")
@PreserveOnRefresh
public class CB_ServerUI extends UI {
	private LMap leafletMap;
	static private UI that;
	private final ICEPush pusher = new ICEPush();
	private final MyExecutor executor = new MyExecutor();
	
	@Override
	protected void init(VaadinRequest request) {
		that = this;
		pusher.extend(this);
		// Force locale "English"
		MessageBox.RESOURCE_FACTORY.setResourceLocale(Locale.ENGLISH);
		// You can use MessageBox.RESOURCES_FACTORY.setResourceBundle(basename);
		// to localize to your language

		final HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(true);
		setContent(layout);

		VerticalLayout vertical = new VerticalLayout();

		final com.vaadin.ui.TextField gcLogin = new TextField("GCLogin");
		gcLogin.setValue(Config.settings.GcLogin.getValue());
		vertical.addComponent(gcLogin);

		final Button button = new Button("Caches: "
				+ Database.Data.Query.size());
		button.setStyleName("test");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Config.settings.GcLogin.setValue(gcLogin.getValue());
				Config.settings.WriteToDB();
				MessageBox.showPlain(Icon.INFO, "Settings", "Gespeichert",
						ButtonId.OK);
			}
		});
		vertical.addComponent(button);

		layout.addComponent(vertical);

		leafletMap = new LMap();
		leafletMap.setCenter(60.4525, 22.301);
		leafletMap.setZoomLevel(15);
		leafletMap.setControls(new ArrayList<Control>(Arrays.asList(Control
				.values())));

		BaseLayer baselayer = new BaseLayer();
		baselayer.setName("CloudMade");
		baselayer.setAttributionString("&copy;OpenStreetMap contributors");

		LPolyline leafletPolyline = new LPolyline(new Point(60.45, 22.295),
				new Point(60.4555, 22.301), new Point(60.45, 22.307));
		leafletPolyline.setColor("#FF00FF");
		leafletPolyline.setFill(true);
		leafletPolyline.setFillColor("#00FF00");
		// leafletPolyline.addClickListener(listener);
		leafletMap.addComponent(leafletPolyline);

		// Note, this url should only be used for testing purposes. If you wish
		// to use cloudmade base maps, get your own API key.
		baselayer
				.setUrl("http://{s}.tile.cloudmade.com/a751804431c2443ab399100902c651e8/997/256/{z}/{x}/{y}.png");

		// This will make everything sharper on "retina devices", but also text
		// quite small
		// baselayer.setDetectRetina(true);

		BaseLayer pk = new BaseLayer();
		pk.setName("Peruskartta");
		pk.setUrl("http://{s}.kartat.kapsi.fi/peruskartta/{z}/{x}/{y}.png");
		pk.setAttributionString("Maanmittauslaitos, hosted by kartat.kapsi.fi");
		pk.setMaxZoom(18);
		pk.setSubDomains("tile2");
		pk.setDetectRetina(true);

		leafletMap.setBaseLayers(pk, baselayer);
		leafletMap.setWidth("100%");
		leafletMap.setHeight("100%");

		layout.setWidth("100%");
		layout.setHeight("100%");
		layout.addComponent(leafletMap);
		
		

		button.setImmediate(true);
		that.setImmediate(true);
		TimerTask action = new TimerTask() {
			public void run() {
				try {
					changeValue(button);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		};

		Timer caretaker = new Timer();
		caretaker.schedule(action, 1000, 5000);

		button.setCaption("c1");

	}

	private void changeValue(final Button button) {
		executor.execute(new Runnable() {
			public void run() {
				getSession().lock();
				try {
					button.setCaption(button.getCaption() + "x");
					
					//NOTE: Comment this line below and problem will go away
					pusher.push();
				} finally {
					getSession().unlock();
				}
			}
		});
	}
	class MyExecutor extends ThreadPoolExecutor {
		public MyExecutor() {
			super(5, 20, 20, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		}
	}
}

