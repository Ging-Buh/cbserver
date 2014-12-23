package cb_server.Views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CB_Core.Events.CachListChangedEventList;
import CB_Core.Events.CacheListChangedEventListner;
import cb_server.Events.SelectedCacheChangedEventList;
import cb_server.Events.SelectedCacheChangedEventListner;

import com.vaadin.ui.CustomComponent;

public abstract class CB_ViewBase extends CustomComponent implements SelectedCacheChangedEventListner, CacheListChangedEventListner {

	private static final long serialVersionUID = 9051645487161218696L;
	protected Logger log;

	//	protected CacheList cacheList = null;

	public CB_ViewBase() {
		super();
		log = LoggerFactory.getLogger(CB_ViewBase.class);
		SelectedCacheChangedEventList.Add(this);
		CachListChangedEventList.Add(this);
	}

	@Override
	public void CacheListChangedEvent() {
		this.getUI().access(new Runnable() {
			@Override
			public void run() {
				cacheListChanged();
			}
		});
	}

	public void cacheListChanged() {
		log.debug("CacheListChanged");
	}

	public void removeFromListener() {
		SelectedCacheChangedEventList.Remove(this);
		CachListChangedEventList.Remove(this);

	}

}