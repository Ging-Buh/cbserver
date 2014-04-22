package cb_server.Views;

import CB_Core.DB.Database;
import CB_Core.Types.Cache;
import CB_Core.Types.CacheLite;
import CB_Core.Types.WaypointLite;
import cb_server.Events.SelectedCacheChangedEventList;
import cb_server.Events.SelectedCacheChangedEventListner;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class DescriptionView extends Panel implements SelectedCacheChangedEventListner {
	private static final long serialVersionUID = -2798716474691088797L;
	public Label browser;
	
	public DescriptionView() {
		this.browser = new Label("Description");
		browser.setContentMode(ContentMode.HTML);
		this.setContent(browser);
		browser.setSizeUndefined();
		this.setSizeFull();
		
		SelectedCacheChangedEventList.Add(this);
	}

	@Override
	public void SelectedCacheChangedEvent(CacheLite cache, WaypointLite waypoint) {

		String desc = Database.GetDescription(cache);
		browser.setValue(desc);
		
	}

}
