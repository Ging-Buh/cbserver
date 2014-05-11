package cb_server.Views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CB_Core.Types.CacheList;
import cb_server.CacheboxServer;

import com.vaadin.ui.CustomComponent;

public class CB_ViewBase extends CustomComponent {

	private static final long serialVersionUID = 9051645487161218696L;
	protected Logger log;
	protected CacheList cacheList = null;
	
	public CB_ViewBase() {
		super();
		log = LoggerFactory.getLogger(CB_ViewBase.class);
	}
	
	public void cacheListChanged(CacheList cacheList) {
		log.debug("CacheListChanged");
		this.cacheList = cacheList;
	}
}
