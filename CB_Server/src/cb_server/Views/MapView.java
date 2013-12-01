package cb_server.Views;

import java.util.ArrayList;
import java.util.Arrays;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LeafletLayer;
import org.vaadin.addon.leaflet.shared.BaseLayer;
import org.vaadin.addon.leaflet.shared.Control;
import org.vaadin.addon.leaflet.shared.Point;

import CB_Core.Types.Cache;
import CB_Core.Types.Waypoint;
import cb_server.Events.SelectedCacheChangedEventList;
import cb_server.Events.SelectedCacheChangedEventListner;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class MapView extends CustomComponent implements SelectedCacheChangedEventListner {

	private static final long serialVersionUID = 5665480835651086183L;
	public LMap leafletMap;

	public MapView() {
	
		leafletMap = new LMap();
		this.setCompositionRoot(leafletMap);
		this.setSizeFull();
		leafletMap.setSizeFull();
		leafletMap.setWidth("100%");
		leafletMap.setHeight("100%");
		leafletMap.setCenter(60.4525, 22.301);
		leafletMap.setZoomLevel(15);
		leafletMap.setControls(new ArrayList<Control>(Arrays.asList(Control
				.values())));

		LTileLayer baselayer = new LTileLayer();
	//	baselayer.setName("CloudMade");
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

		LTileLayer pk = new LTileLayer();
	    pk.setUrl("http://{s}.kartat.kapsi.fi/peruskartta/{z}/{x}/{y}.png");
		pk.setAttributionString("Maanmittauslaitos, hosted by kartat.kapsi.fi");
		pk.setMaxZoom(18);
		pk.setSubDomains("tile2");
		pk.setDetectRetina(true);

		leafletMap.addBaseLayer(pk, "");
		leafletMap.addBaseLayer(baselayer, "");

		
		// add to SelectedCacheChangedListener
		SelectedCacheChangedEventList.Add(this);
	}

	@Override
	public void SelectedCacheChangedEvent(Cache cache, Waypoint waypoint) {
		if (cache == null) {
			return;
		}
		if (waypoint == null) {
			leafletMap.setCenter(cache.Latitude(), cache.Longitude());
		} else {
			leafletMap.setCenter(waypoint.Pos.getLatitude(), waypoint.Pos.getLongitude());
		}
	}

}
