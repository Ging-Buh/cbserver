package cb_server.Views.Dialogs;

import CB_Core.Types.Waypoint;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;


public class WaypointDialog extends Window {
	public interface ReturnListner
	{
		public void returnedWP(Waypoint wp);
	}

	private ReturnListner returnListner;


	private static final long serialVersionUID = 8222163724284799469L;
	private Waypoint waypoint;
	private TextField tfName;
	private TextField tfDescription;
	
	public WaypointDialog(Waypoint waypoint, ReturnListner returnListener) {
		super("Edit Waypoint");
		this.waypoint = waypoint;
		this.returnListner = returnListener;
		setModal(true);
		setResizable(false);
		VerticalLayout content = new VerticalLayout();
		setContent(content);
		
		GridLayout layoutContent = new GridLayout();
		content.addComponent(layoutContent);
		
		tfName = new TextField();
		tfName.setCaption("Titel:");
		tfName.setValue(waypoint.getTitle());
		layoutContent.addComponent(tfName);
		
		tfDescription = new TextField();
		tfDescription.setCaption("Description:");
		tfDescription.setValue(waypoint.getDescription());
		layoutContent.addComponent(tfDescription);
		
		HorizontalLayout layoutButtons = new HorizontalLayout();
		content.addComponent(layoutButtons);
		addOKCancelButtons(layoutButtons);
	}

	private void addOKCancelButtons(AbstractLayout content) {
		Button bOK = new Button("OK");
		content.addComponent(bOK);
		Button bCancel = new Button("Abbrechen");
		content.addComponent(bCancel);
		
		bOK.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 8451830127082999189L;

			@Override
			public void buttonClick(ClickEvent event) {
				waypoint.setTitle(tfName.getValue());
				waypoint.setDescription(tfDescription.getValue());
				if (returnListner != null)
				{
					returnListner.returnedWP(waypoint);
				}
				close();
			}
		});
		
		bCancel.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 6832736272605304637L;

			@Override
			public void buttonClick(ClickEvent event) {
				close();				
			}
		});
	}
	
	
}
