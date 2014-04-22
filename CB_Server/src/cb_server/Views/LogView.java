package cb_server.Views;

import CB_Core.DB.Database;
import CB_Core.Types.CacheLite;
import CB_Core.Types.LogEntry;
import CB_Core.Types.Waypoint;
import CB_Utils.Lists.CB_List;
import cb_server.Events.SelectedCacheChangedEventList;
import cb_server.Events.SelectedCacheChangedEventListner;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class LogView extends CB_ViewBase implements SelectedCacheChangedEventListner {

	private static final long serialVersionUID = 2314353959099189624L;
	private VerticalLayout list;
	private Panel panel;
	
	public LogView() {
		super();
		list = new VerticalLayout();
		panel = new Panel();
		panel.setContent(list);
		panel.setHeight("100%");
		this.setCompositionRoot(panel);
		
		Label label = new Label("<table border=\"1\" frame=\"void\" cellspacing=\"5\" cellpadding=\"5\"><tr><th>hallo <br> <i>Hubert</i> aslkfjalskd fklas jf�lkasjlkfj as�lkdf jasljf la�sjg lkadjglk asdjglkajdlkdgj slkgjla kjglaj glaj lgkajlk gjasd�lkgj alsdkg jl�ksdgj l�akdsjg lkajglkjdglkksjdlgkjsdlkfgj sd�lkfg jsd�lkgj�slkdjg sdgjs dlkgsdlkjg sdjgs jfglsjd glsdj glsjdl�g jsd�lfgj sld�kgjsldjgsld</th></td></table>", ContentMode.HTML);
		list.addComponent(label);
		
		label = new Label("<table border=\"1\" frame=\"void\" cellspacing=\"5\" cellpadding=\"5\"><tr><th>hallo <br> <i>Martina</i></th></td></table>", ContentMode.HTML);
		list.addComponent(label);
		
		this.setSizeFull();
		
		SelectedCacheChangedEventList.Add(this);

	}

	@Override
	public void SelectedCacheChangedEvent(CacheLite cache2, Waypoint waypoint) {
		list.removeAllComponents();
		
		CB_List<LogEntry> cleanLogs = new CB_List<LogEntry>();
		cleanLogs = Database.Logs(cache2);// cache.Logs();
		
		for (int i = 0, n = cleanLogs.size(); i < n; i++)
		{
			LogEntry logEntry = cleanLogs.get(i);
			String log = "<table border=\"1\" frame=\"void\" cellspacing=\"0\" cellpadding=\"5\" style=\"margin: 5px;\">";
			log += "<tr><th>" + logEntry.Type + " - " + logEntry.Finder + " - " +  logEntry.Timestamp.toLocaleString() + "</th></tr>";
			Label label = new Label(log + "<tr><td>" + logEntry.Comment + "</td></tr></table>", ContentMode.HTML);
			list.addComponent(label);
		}
	}
	


}
