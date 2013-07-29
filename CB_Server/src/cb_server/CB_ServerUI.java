package cb_server;

import java.util.ArrayList;
import java.util.Arrays;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.shared.BaseLayer;
import org.vaadin.addon.leaflet.shared.Control;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("cb_server")
public class CB_ServerUI extends UI {
    private LMap leafletMap;

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		Button button = new Button("Click Me");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				layout.addComponent(new Label("Thank you for clicking"));
			}
		});
//		layout.addComponent(button);
				
		
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
//        leafletPolyline.addClickListener(listener);
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
	}

}