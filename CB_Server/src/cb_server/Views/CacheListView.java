package cb_server.Views;

import java.io.Serializable;
import java.util.Date;

import CB_Core.DB.Database;
import CB_Core.Types.Cache;
import CB_Core.Types.CacheList;
import CB_Core.Types.Cache;
import CB_Core.Types.Waypoint;
import cb_server.Events.SelectedCacheChangedEventList;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class CacheListView extends CB_ViewBase {

	private static final long serialVersionUID = -8341714748837951953L;
	public Table table;
	private BeanItemContainer<CacheBean> beans;

	public CacheListView() {
		super();
		beans = new BeanItemContainer<CacheBean>(CacheBean.class);
		// beans.setBeanIdProperty("GCCode");

		//		for (int i = 0, n = Database.Data.Query.size(); i < n; i++) {
		//			beans.addBean(new CacheBean(Database.Data.Query.get(i)));
		//		}

		this.table = new Table("CacheList", beans);
		this.setCompositionRoot(table);
		this.setSizeFull();
		table.setSizeFull();
		table.setSelectable(true);
		table.setImmediate(true);

		//		table.addGeneratedColumn("NewCol", new DescriptionColumnGenerator());
		//		table.setColumnHeader("NewCol", "NC");
		// Have to set explicitly to hide the "equatorial" property
		//		table.setVisibleColumns(new Object[]{"GCCode", "Name", "Description"});

		table.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -1246546962581855595L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Object o = table.getValue();
				if (o instanceof CacheBean) {
					Cache cache = ((CacheBean) o).cache;
					Waypoint waypoint = cache.GetFinalWaypoint();
					if (waypoint == null) {
						waypoint = cache.GetStartWaypoint();
					}
					SelectedCacheChangedEventList.Call(cache, waypoint);
				}
			}
		});

	}

	@Override
	public void cacheListChanged(CacheList cacheList) {
		super.cacheListChanged(cacheList);
		log.debug("Remove all Beans");
		beans.removeAllItems();
		log.debug("Add new Beans for new CacheList");
		try {
			table.getUI().getSession().lock();
			try {
				for (int i = 0, n = cacheList.size(); i < n; i++) {
					beans.addBean(new CacheBean(cacheList.get(i)));
				}
			} finally {
			   table.getUI().getSession().unlock();
			}
		} catch (Exception ex) {
			System.out.println("lskjdl");
		}
		getUI().push();
	}

	class DescriptionColumnGenerator implements Table.ColumnGenerator {
		private static final long serialVersionUID = 3741451390162331681L;

		/**
		 * Generates the cell containing the Date value. The column is
		 * irrelevant in this use case.
		 */
		public Component generateCell(Table source, Object itemId, Object columnId) {
			Item it = source.getItem(itemId);
			it.getItemPropertyIds();
			Property prop = source.getItem(itemId).getItemProperty("GCCode");
			//            if (prop.getType().equals(String.class)) {
			Label label = new Label((String) prop.getValue() + "--");
			label.addStyleName("column-type-date");
			return label;
			//            }

			//            return null;
		}
	}

	public class CacheBean implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5457794531550146509L;
		private String GCCode;
		private String Name;
		private String Description;
		private Cache cache;

		public CacheBean(Cache cacheLite) {
			this.cache = cacheLite;
			this.setGCCode("");
			this.setName("");
			this.setDescription("");
		}

		public String getName() {
			return cache.getName();
		}

		public void setName(String name) {
			Name = name;
		}

		public String getGCCode() {
			return cache.getGcCode();
		}

		public void setGCCode(String gCCode) {
			GCCode = gCCode;
		}

		public void setDescription(String desc) {
			this.Description = desc;
		}

		public String getDescription() {
			return "Hallo " + cache.getGcCode();
		}
	}
}
