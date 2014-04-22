package cb_server.Views;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;

import CB_Core.DB.Database;
import CB_Core.Enums.Attributes;
import CB_Core.Import.DescriptionImageGrabber;
import CB_Core.Settings.CB_Core_Settings;
import CB_Core.Types.Cache;
import CB_Core.Types.CacheLite;
import CB_Core.Types.Waypoint;
import cb_server.Config;
import cb_server.Events.SelectedCacheChangedEventList;
import cb_server.Events.SelectedCacheChangedEventListner;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class DescriptionView extends Panel implements SelectedCacheChangedEventListner {
	private static final long serialVersionUID = -2798716474691088797L;
	public Label browser;
	private LinkedList<String> NonLocalImages = new LinkedList<String>();
	private LinkedList<String> NonLocalImagesUrl = new LinkedList<String>();
	private Cache cache = null;
	
	public DescriptionView() {
		this.browser = new Label("Description");
		browser.setContentMode(ContentMode.HTML);
		this.setContent(browser);
		browser.setSizeUndefined();
		this.setSizeFull();
		
		SelectedCacheChangedEventList.Add(this);
	}

	@Override
	public void SelectedCacheChangedEvent(CacheLite cacheLite, Waypoint waypoint) {
		NonLocalImages.clear();
		NonLocalImagesUrl.clear();

		cache = new Cache(cacheLite);
		String cachehtml = Database.GetDescription(cache);
		String html = DescriptionImageGrabber.ResolveImages(cache, cachehtml, false, NonLocalImages, NonLocalImagesUrl);
		// Replace local path with URL because Browser can not show Images with local path.
		
		URI uri = Page.getCurrent().getLocation();
		if (uri != null) {
			String newUrl = uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort() + "/images";
			html = html.replace("file://" + CB_Core_Settings.DescriptionImageFolder.getValue(), newUrl);
		}
//		if (!Config.DescriptionNoAttributes.getValue()) html = getAttributesHtml(cache) + html;

		// add 2 empty lines so that the last line of description can be selected with the markers
		html += "</br></br>";
		browser.setValue(html);
		
	}

	private String getAttributesHtml(Cache cache)
	{
		StringBuilder sb = new StringBuilder();

		Iterator<Attributes> attrs = cache.getAttributes().iterator();

		if (attrs == null || !attrs.hasNext()) return "";

		do
		{
			Attributes attribute = attrs.next();
			File result = new File(Config.WorkPath + "/data/Attributes/" + attribute.getImageName() + ".png");

			sb.append("<form action=\"Attr\">");
			sb.append("<input name=\"Button\" type=\"image\" src=\"file://" + result.getAbsolutePath() + "\" value=\" "
					+ attribute.getImageName() + " \">");
		}
		while (attrs.hasNext());

		sb.append("</form>");

		if (sb.length() > 0) sb.append("<br>");
		return sb.toString();
	}


}
