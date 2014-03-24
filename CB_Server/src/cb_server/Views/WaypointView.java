package cb_server.Views;

import java.io.Serializable;

import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedListener.TableListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableFooterEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableHeaderEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableRowEvent;

import CB_Core.DB.Database;
import CB_Core.Types.Cache;
import CB_Core.Types.Waypoint;
import cb_server.Events.SelectedCacheChangedEventList;
import cb_server.Events.SelectedCacheChangedEventListner;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;

public class WaypointView extends Panel implements SelectedCacheChangedEventListner {

	private static final long serialVersionUID = -5376708430763812238L;
	private BeanItemContainer<WaypointBean> beans;
	public Table table;
	private boolean doNotUpdate = false;
	
	public WaypointView() {
	    beans = new BeanItemContainer<WaypointBean>(WaypointBean.class);
		
		this.table = new Table("WaypointList", beans);
		this.setContent(table);
		this.setSizeFull();
		table.setSizeFull();
		table.setSelectable(true);
		table.setImmediate(true);

		table.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -1246546962581855595L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Object o = table.getValue();
				if (o instanceof WaypointBean) {
					doNotUpdate = true;
					SelectedCacheChangedEventList.Call(SelectedCacheChangedEventList.Cache, ((WaypointBean)o).waypoint);
					doNotUpdate = false;
				}
			}
		});
		SelectedCacheChangedEventList.Add(this);
		
		// Context Menu
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.addItem("Edit");
		contextMenu.addContextMenuTableListener(new TableListener() {
			
			@Override
			public void onContextMenuOpenFromRow(ContextMenuOpenedOnTableRowEvent event) {
			}
			
			@Override
			public void onContextMenuOpenFromHeader(
					ContextMenuOpenedOnTableHeaderEvent event) {
			}
			
			@Override
			public void onContextMenuOpenFromFooter(
					ContextMenuOpenedOnTableFooterEvent event) {
			}
		});
		contextMenu.setOpenAutomatically(true);
		contextMenu.addItemClickListener(new ContextMenuItemClickListener() {
			
			@Override
			public void contextMenuItemClicked(ContextMenuItemClickEvent event) {
				String s = "";
				
			}
		});
		contextMenu.setAsTableContextMenu(table);
	}

	@Override
	public void SelectedCacheChangedEvent(Cache cache, Waypoint waypoint) {
		if (doNotUpdate) return;
		beans.removeAllItems();
		beans.addBean(new WaypointBean(SelectedCacheChangedEventList.Cache, null));
		for (int i=0,n=cache.waypoints.size(); i<n; i++){
			beans.addBean(new WaypointBean(SelectedCacheChangedEventList.Cache, cache.waypoints.get(i)));
		}
		table.setData(beans);
	}

	
	public class WaypointBean implements Serializable {
		
		private static final long serialVersionUID = -6410465924774846650L;
		private String GCCode;
		private String Title;
		private String Description;
		private Cache cache;
		private Waypoint waypoint;
		
		public WaypointBean(Cache cache, Waypoint waypoint) {
			this.cache = cache;
			this.waypoint = waypoint;
			this.setGCCode("");
			this.setTitle("");
		}

		public String getTitle() {
			if (waypoint == null)
				return cache.Name;
			else
				return waypoint.Title;
		}

		public void setTitle(String title) {
			Title = title;
		}

		public String getGCCode() {
			if (waypoint == null) {
				return cache.GcCode;
			} else {
				return waypoint.GcCode;
			}
		}

		public void setGCCode(String gCCode) {
			GCCode = gCCode;
		}
		
	}
}