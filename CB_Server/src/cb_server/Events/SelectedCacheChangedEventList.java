package cb_server.Events;

import java.util.ArrayList;

import CB_Core.CoreSettingsForward;
import CB_Core.DB.Database;
import CB_Core.Enums.CacheTypes;
import CB_Core.Settings.CB_Core_Settings;
import CB_Core.Types.Cache;
import CB_Core.Types.CacheLite;
import CB_Core.Types.WaypointLite;

public class SelectedCacheChangedEventList {
	public static ArrayList<SelectedCacheChangedEventListner> list = new ArrayList<SelectedCacheChangedEventListner>();
	// hier werden der aktuell ausgewählte cache und wp gespeichert
	public static CacheLite Cache;
	public static WaypointLite Waypoint;

	public static void Add(SelectedCacheChangedEventListner event) {
		synchronized (list) {
			if (!list.contains(event))
				list.add(event);
		}
	}

	public static void Remove(SelectedCacheChangedEventListner event) {
		synchronized (list) {
			list.remove(event);
		}
	}

	public static void Call(CacheLite cache2, WaypointLite waypoint) {
		synchronized (list) {
			for (SelectedCacheChangedEventListner event : list) {
				if (event == null)
					continue;
				Cache = cache2;
				Waypoint = waypoint;
				event.SelectedCacheChangedEvent(cache2, waypoint);
			}
		}

	}

}
