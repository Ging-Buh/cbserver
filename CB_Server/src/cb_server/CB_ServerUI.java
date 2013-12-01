package cb_server;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.artur.icepush.ICEPush;

import CB_Core.DB.Database;
import cb_server.Views.CacheListView;
import cb_server.Views.DescriptionView;
import cb_server.Views.MapView;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
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
		
		final com.vaadin.ui.TextField gcLogin = new TextField("GCLogin");
		gcLogin.setValue(Config.settings.GcLogin.getValue());
		
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
		
		
		
		
		final Button open = new Button("Open Settings-Window");
		open.addClickListener(new ClickListener() {
		    public void buttonClick(ClickEvent event) {
		    	SettingsWindow sub = SettingsWindow.getInstanz();
		        
		    	if(!UI.getCurrent().getWindows().contains(sub))
		    		
		        // Add it to the root component
		        UI.getCurrent().addWindow(sub);
		    }
		});
		
		
		
		MapView mv = new MapView();
		DescriptionView dv = new DescriptionView();
		CacheListView clv = new CacheListView();
		
		// VerticalLayout für Header, Inhalt und Footer erstellen
		VerticalLayout vl = new VerticalLayout();
		this.setContent(vl);
		Panel header = new Panel();	// Header
		HorizontalSplitPanel content = new HorizontalSplitPanel();	// Inhalt
		Panel footer = new Panel();	// Footer
		
		vl.addComponent(header);
		vl.addComponent(content);
		vl.addComponent(footer);
		
		vl.setSizeFull();
		vl.setExpandRatio(content, 1);	// Inhalt muss den größten Bereich einnehmen
		
		// Inhalt vom Header
		Button bo = new Button("Settings");
		header.setContent(open);
		
		// Inhalt vom Content
		content.setSizeFull();
		

		TabSheet tabLinks = new TabSheet();
		content.setFirstComponent(tabLinks);
		tabLinks.setSizeFull();
	
		TabSheet tabRechts = new TabSheet();
		content.setSecondComponent(tabRechts);
		tabRechts.setSizeFull();
		tabRechts.addTab(dv, "DescriptionView");
		tabLinks.addTab(clv, "CacheList");
		tabRechts.addTab(mv, "MapView");
		
		// Inhalt vom Footer
		Button bu = new Button("Unten");
		footer.setContent(bu);
		


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

