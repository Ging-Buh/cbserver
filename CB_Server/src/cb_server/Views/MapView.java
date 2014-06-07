package cb_server.Views;

import java.util.ArrayList;
import java.util.Arrays;

import org.vaadin.addon.leaflet.LLayerGroup;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LeafletMoveEndEvent;
import org.vaadin.addon.leaflet.LeafletMoveEndListener;
import org.vaadin.addon.leaflet.shared.Bounds;
import org.vaadin.addon.leaflet.shared.Control;
import org.vaadin.addon.leaflet.shared.Point;

import CB_Core.DB.Database;
import CB_Core.Enums.CacheTypes;
import CB_Core.Events.CacheListChangedEventListner;
import CB_Core.Types.Cache;
import CB_Core.Types.Waypoint;
import cb_server.Events.SelectedCacheChangedEventList;
import cb_server.Events.SelectedCacheChangedEventListner;

import com.google.gwt.dev.util.collect.HashMap;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.CustomComponent;

public class MapView extends CB_ViewBase implements SelectedCacheChangedEventListner {

	private static final long serialVersionUID = 5665480835651086183L;
	public LMap leafletMap;
	String[] MapIconsSmall = { "small1yes", "small2yes", "small3yes", "small4yes", "small5yes", "small5solved", "small6yes", "small7yes", "small1no", "small2no", "small3no", "small4no", "small5no", "small5solved-no", "small6no", "small7no", "20", "22" };

	public MapView() {

		leafletMap = new LMap();
		this.setCompositionRoot(leafletMap);
		this.setSizeFull();
		leafletMap.setSizeFull();
		leafletMap.setWidth("100%");
		leafletMap.setHeight("100%");
		leafletMap.setCenter(60.4525, 22.301);
		leafletMap.setZoomLevel(15);
		leafletMap.setControls(new ArrayList<Control>(Arrays.asList(Control.values())));

		LTileLayer baselayer = new LTileLayer();
		// baselayer.setName("CloudMade");
		baselayer.setAttributionString("&copy;OpenStreetMap contributors");

		LPolyline leafletPolyline = new LPolyline(new Point(60.45, 22.295), new Point(60.4555, 22.301), new Point(60.45, 22.307));
		leafletPolyline.setColor("#FF00FF");
		leafletPolyline.setFill(true);
		leafletPolyline.setFillColor("#00FF00");
		// leafletPolyline.addClickListener(listener);
		leafletMap.addComponent(leafletPolyline);

		// Note, this url should only be used for testing purposes. If you wish
		// to use cloudmade base maps, get your own API key.
//		baselayer.setUrl("http://{s}.tile.cloudmade.com/a751804431c2443ab399100902c651e8/997/256/{z}/{x}/{y}.png");
		baselayer.setUrl("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png");

		// This will make everything sharper on "retina devices", but also text
		// quite small
		// baselayer.setDetectRetina(true);

		LTileLayer pk = new LTileLayer();
		pk.setUrl("http://{s}.kartat.kapsi.fi/peruskartta/{z}/{x}/{y}.png");
		pk.setAttributionString("Maanmittauslaitos, hosted by kartat.kapsi.fi");
		pk.setMaxZoom(18);
		pk.setSubDomains("tile2");
		pk.setDetectRetina(true);
		pk.setVisible(false);

		LTileLayer lk = new LTileLayer();
		lk.setUrl("http://localhost:8085/map/{z}/{x}/{y}.png");
		lk.setMaxZoom(19);
		lk.setDetectRetina(true);
		lk.setSubDomains("tile2");
		lk.setVisible(true);
		lk.setActive(false);
		
		leafletMap.addBaseLayer(pk, "");
		leafletMap.addBaseLayer(lk, "MapsForge");
		leafletMap.addBaseLayer(baselayer, "");

		leafletMap.addMoveEndListener(new LeafletMoveEndListener() {

			@Override
			public void onMoveEnd(LeafletMoveEndEvent event) {
				System.out.println("Move");
				updateIcons(event.getZoomLevel(), event.getBounds());

			}
		});

		// add to SelectedCacheChangedListener
		SelectedCacheChangedEventList.Add(this);

		// updateIcons(leafletMap.);
	}

	public void cacheListChanged(CB_Core.Types.CacheList cacheList) {
		super.cacheListChanged(cacheList);
	};
	
	HashMap<Long, LMarker> markers = null;
	LLayerGroup llg = null;
	HashMap<Long, LMarker> underlays = null;
	LLayerGroup llgu = null; // für Underlay Icons

	private void updateIcons(int zoom, Bounds bounds) {
		long start = System.currentTimeMillis();
		int iconSize = 0; // 8x8
		if ((zoom >= 13) && (zoom <= 14))
			iconSize = 1; // 13x13
		else if (zoom > 14)
			iconSize = 2; // default Images

		if (markers == null) {
			llg = new LLayerGroup();
			llgu = new LLayerGroup();
			markers = new HashMap<Long, LMarker>();
			underlays = new HashMap<Long, LMarker>();
			for (int i = 0, n = cacheList.size(); i < n; i++) {
				Cache cache = cacheList.get(i);
				Waypoint waypoint = cache.GetFinalWaypoint();
				if (waypoint == null) {
					waypoint = cache.GetStartWaypoint();
				}
				LMarker marker = null;
				if (waypoint != null) {
					marker = new LMarker(waypoint.Pos.getLatitude(), waypoint.Pos.getLongitude());
				} else {
					marker = new LMarker(cache.Latitude(), cache.Longitude());
				}
				marker.setIconSize(new Point(15, 15));
				marker.setIconAnchor(new Point(7, 7));
				marker.setTitle(cache.getName());
				marker.setPopup(cache.getShortDescription());

				marker.setIcon(new ThemeResource(getCacheIcon(cache, iconSize)));
				marker.setVisible(false);
				marker.setCaption("Caption");
				marker.setDescription("Description");
				marker.setLabel(null);
				markers.put(cache.Id, marker);
				llg.addComponent(marker);

				// Underlay Icons
				//				marker = new LMarker(cache.Latitude(),
				//						cache.Longitude());
				//				marker.setIconSize(new Point(48, 48));
				//				marker.setIconAnchor(new Point(24, 24));
				//				marker.setIcon(new ThemeResource(getUnderlayIcon(cache, null, iconSize)));
				//				
				//				marker.setVisible(false);
				//				llgu.addComponent(marker);
				//				underlays.put(cache.Id, marker);

			}
			leafletMap.addLayer(llg);
			//			leafletMap.addLayer(llgu);
		}

		for (int i = 0, n = cacheList.size(); i < n; i++) {
			Cache cache = cacheList.get(i);
			LMarker marker = null;
			try {
				marker = markers.get(cache.Id);
			} catch (Exception ex) {
				continue; // TODO
			}

			marker.setVisible(isInBounds(cache.Latitude(), cache.Longitude(), bounds));
			marker.setIcon(new ThemeResource(getCacheIcon(cache, iconSize)));

			//			LMarker uMarker = null;
			//			try {
			//				uMarker = underlays.get(cache.Id);
			//			} catch (Exception ex) {
			//				continue; // TODO
			//			}
			//			
			//			if (iconSize == 2) {
			//				uMarker.setVisible(isInBounds(cache.Latitude(), cache.Longitude(),
			//						bounds));
			//			} else {
			//				uMarker.setVisible(false);
			//			}
			//			uMarker.setVisible(false);
			marker.setActive(cache == SelectedCacheChangedEventList.getCache());
			switch (iconSize) {
			case 0:
				marker.setIconSize(new Point(12, 12));
				marker.setIconAnchor(new Point(6, 6));
				break;
			case 1:
				marker.setIconSize(new Point(24, 24));
				marker.setIconAnchor(new Point(12, 12));
				break;
			case 2:
				marker.setIconSize(new Point(32, 32));
				marker.setIconAnchor(new Point(16, 16));
				break;
			}
			if (zoom > 14) {
				marker.setLabel(cache.getName());
			} else {
				marker.setLabel(null);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("UpdateIcons Duration: " + String.valueOf(end - start));
	}

	private boolean isInBounds(double latitude, double longitude, Bounds bounds) {
		if (latitude > bounds.getNorthEastLat())
			return false;
		if (latitude < bounds.getSouthWestLat())
			return false;
		if (longitude < bounds.getSouthWestLon())
			return false;
		if (longitude > bounds.getNorthEastLon())
			return false;
		return true;
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

	private String getCacheIcon(Cache cache, int iconSize) {
		if ((iconSize < 1) && (cache != SelectedCacheChangedEventList.getCache())) {
			return getSmallMapIcon(cache);
		} else {
			// der SelectedCache wird immer mit den großen Symbolen dargestellt!
			return getMapIcon(cache);
		}
	}

	private String getMapIcon(Cache cache) {
		int IconId;
		if (cache.ImTheOwner())
			IconId = 26;
		else if (cache.isFound())
			IconId = 19;
		else if ((cache.Type == CacheTypes.Mystery) && cache.CorrectedCoordiantesOrMysterySolved())
			IconId = 21;
		else if ((cache.Type == CacheTypes.Multi) && cache.HasStartWaypoint())
			IconId = 23; // Multi mit Startpunkt
		else if ((cache.Type == CacheTypes.Mystery) && cache.HasStartWaypoint())
			IconId = 25; // Mystery ohne Final aber mit Startpunkt
		else if ((cache.Type == CacheTypes.Munzee))
			IconId = 22;
		else
			IconId = cache.Type.ordinal();

		if (IconId == 26)
			return "icons/start.png";
		else
			return "icons/" + IconId + ".png";
	}

	private String getSmallMapIcon(Cache cache) {
		int iconId = 0;

		switch (cache.Type) {
		case Traditional:
			iconId = 0;
			break;
		case Letterbox:
			iconId = 0;
			break;
		case Multi:
			if (cache.HasStartWaypoint())
				iconId = 1;
			else
				iconId = 1;
			break;
		case Event:
			iconId = 2;
			break;
		case MegaEvent:
			iconId = 2;
			break;
		case Virtual:
			iconId = 3;
			break;
		case Camera:
			iconId = 3;
			break;
		case Earth:
			iconId = 3;
			break;
		case Mystery: {
			if (cache.HasFinalWaypoint())
				iconId = 5;
			else if (cache.HasStartWaypoint())
				iconId = 5;
			else
				iconId = 4;
			break;
		}
		case Wherigo:
			iconId = 4;
			break;

		default:
			iconId = 0;
		}

		if (cache.isFound())
			iconId = 6;
		if (cache.ImTheOwner())
			iconId = 7;

		if (cache.isArchived() || !cache.isAvailable())
			iconId += 8;

		if (cache.Type == CacheTypes.MyParking)
			iconId = 16;
		if (cache.Type == CacheTypes.Munzee)
			iconId = 17;

		return "icons/" + MapIconsSmall[iconId] + ".png";

	}

	private String getUnderlayIcon(Cache cache, Waypoint waypoint, int iconSize) {
		if ((iconSize == 0) && (cache != SelectedCacheChangedEventList.getCache())) {
			return null;
		} else {
			if (waypoint == null) {
				if ((cache != null) && (cache == SelectedCacheChangedEventList.getCache())) {
					return "icons/shaddowrect-selected.png";
				} else {
					return "icons/shaddowrect.png";
				}
			} else {
				if (waypoint == SelectedCacheChangedEventList.getWaypoint()) {
					return "icons/shaddowrect-selected.png";
				} else {
					return "icons/shaddowrect.png";
				}
			}
		}
	}

}
