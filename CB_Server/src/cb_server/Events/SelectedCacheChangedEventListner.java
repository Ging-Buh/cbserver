package cb_server.Events;

import CB_Core.Types.Cache;
import CB_Core.Types.Waypoint;

// this is an interface for all Objects which sould receive the selectedCacheChanged Event


public interface SelectedCacheChangedEventListner 
{
		public void SelectedCacheChangedEvent(Cache cache, Waypoint waypoint);
}
