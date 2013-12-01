package cb_server.Views;

import java.io.Serializable;
import java.util.Date;

import CB_Core.DB.Database;
import CB_Core.Types.Cache;
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

public class CacheListView extends Panel {

	private static final long serialVersionUID = -8341714748837951953L;
	public Table table;

	public CacheListView() {		
		
	    BeanItemContainer<CacheBean> beans = new BeanItemContainer<CacheBean>(CacheBean.class);
	   // beans.setBeanIdProperty("GCCode");
	    
	    for (Cache cache : Database.Data.Query) {
	    	beans.addBean(new CacheBean(cache));
	    }

	    

		this.table = new Table("CacheList", beans);
		this.setContent(table);
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
					SelectedCacheChangedEventList.Call(((CacheBean)o).cache, null);
				}
				System.out.println(o.toString());
			}
		});
		
	}

    class DescriptionColumnGenerator implements Table.ColumnGenerator {
        private static final long serialVersionUID = 3741451390162331681L;

        /**
         * Generates the cell containing the Date value. The column is
         * irrelevant in this use case.
         */
        public Component generateCell(Table source, Object itemId,
                Object columnId) {
        	Item it = source.getItem(itemId);
        	it.getItemPropertyIds();
            Property prop = source.getItem(itemId).getItemProperty("GCCode");
//            if (prop.getType().equals(String.class)) {
                Label label = new Label((String)prop.getValue() + "--");
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
		
		public CacheBean(Cache cache) {
			this.cache = cache;
			this.setGCCode("");
			this.setName("");
			this.setDescription("");
		}

		public String getName() {
			return cache.Name;
		}

		public void setName(String name) {
			Name = name;
		}

		public String getGCCode() {
			return cache.GcCode;
		}

		public void setGCCode(String gCCode) {
			GCCode = gCCode;
		}
		
		public void setDescription(String desc) {
			this.Description = desc;
		}
		
		public String getDescription() {
			return "Hallo " + cache.GcCode;
		}
	}
}
