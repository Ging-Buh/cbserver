package cb_server.Views;

import java.io.Serializable;
import java.util.Date;

import CB_Core.DB.Database;
import CB_Core.Enums.CacheTypes;
import CB_Core.Types.Cache;
import CB_Core.Types.CacheList;
import CB_Core.Types.Cache;
import CB_Core.Types.Waypoint;
import cb_server.Events.SelectedCacheChangedEventList;
import cb_server.Views.CacheListView.CacheBean;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class CacheListView extends CB_ViewBase {

	private static final long serialVersionUID = -8341714748837951953L;
	public Table table;
	private CacheContainer beans;
	private String host;

	public CacheListView() {
		super();
		host = com.vaadin.server.Page.getCurrent().getLocation().getScheme() + "://" + com.vaadin.server.Page.getCurrent().getLocation().getAuthority() + "/";
		beans = new CacheContainer();
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
		//		table.setVisibleColumns(new Object[]{"Description"});

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
		/*		table.addGeneratedColumn("icon", new Table.ColumnGenerator() {
					private static final long serialVersionUID = 5199037506976926798L;

					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						Cache cache = null;
						if (itemId instanceof CacheBean) {
							cache = ((CacheBean)itemId).cache;
						}
						return new Embedded("", new ExternalResource(getCacheIcon(cache, 16, 0, false, false) + ".png"));
					}
					
				});
		*/
	}

	private String getCacheIcon(Cache cache, int iconSize, int backgroundSize, boolean selected, boolean showDT) {
		String url = host + "ics/";
		url += "C";
		url += String.format("%02d", cache.Type.ordinal()); // 2 stellig
		if (cache.isArchived())
			url += "A";
		if (!cache.isAvailable())
			url += "N";
		if (cache.isFound())
			url += "F";
		if (cache.ImTheOwner())
			url += "O";
		if (cache.CorrectedCoordiantesOrMysterySolved())
			url += "S";
		if (cache.HasStartWaypoint())
			url += "T";
		if (selected)
			url += "L";
		if (showDT) {
			url += "_D" + (int) (cache.getDifficulty() * 2);
			url += "_T" + (int) (cache.getTerrain() * 2);
		}
		url += "_S" + iconSize;
		if (backgroundSize > 0) {
			url += "_B" + backgroundSize;
		}
		return url;
	}

	private int getMapIcon(Cache cache) {
		int IconId;
		if (cache.ImTheOwner())
			IconId = 26;
		else if (cache.isFound())
			IconId = 19;
		else if ((cache.Type == CacheTypes.Mystery) && cache.CorrectedCoordiantesOrMysterySolved())
			IconId = 21;
		else if ((cache.Type == CacheTypes.Multi) && cache.HasStartWaypoint())
			IconId = 23; // Multi mit Startpunkt
		else if ((cache.Type == CacheTypes.Mystery) && cache.HasStartWaypoint())
			IconId = 25; // Mystery ohne Final aber mit Startpunkt
		else if ((cache.Type == CacheTypes.Munzee))
			IconId = 22;
		else
			IconId = cache.Type.ordinal();

		return IconId;
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
					BeanItem<CacheBean> item = beans.addBean(new CacheBean(cacheList.get(i)));
					table.setItemIcon(item, new ExternalResource(getCacheIcon(cacheList.get(i), 16, 0, false, false) + ".png"));
				}
			} finally {
				table.getUI().getSession().unlock();
			}
			table.setVisibleColumns(new Object[]{"icon", "GCCode", "name", "state", "country"});
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
		private String State;
		private String Country;
		private Cache cache;
		private Resource icon;

		public CacheBean(Cache cacheLite) {
			this.cache = cacheLite;
			this.setGCCode("");
			this.setName("");
			this.setState("");
			this.setCountry("");
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

		public void setState(String state) {
			this.State = state;
		}
		
		public String getState() {
			return cache.getState();
		}
		
		public void setCountry(String desc) {
			this.Country = desc;
		}

		public String getCountry() {
			return cache.getCountry();
		}

		public Embedded getIcon() {
			return new Embedded("", new ExternalResource(getCacheIcon(cache, 16, 0, false, false) + ".png"));
		}
	}

	public class CacheContainer extends BeanItemContainer<CacheBean> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2808380911891848063L;

		public CacheContainer() {
			super(CacheBean.class);
		}

		
		
	}
}
