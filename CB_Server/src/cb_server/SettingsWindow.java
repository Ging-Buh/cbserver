package cb_server;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

//import fi.jasoft.qrcode.QRCode;


public class SettingsWindow  extends Window {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	final private static  SettingsWindow INSTANZ = new SettingsWindow();
	
	public static SettingsWindow getInstanz()
	{
		return INSTANZ;
	}
	
	
	private SettingsWindow() {
		
		
		
		
		
        super("Server Settings"); // Set window caption
       
              
        this.setWidth(80, Unit.PERCENTAGE);
        this.setHeight(80, Unit.PERCENTAGE);
        
        
        center();
        
        // Some basic content for the window
        VerticalLayout content = new VerticalLayout();
        content.addComponent(new Label("Just say it's OK!"));
        content.setMargin(true);
        setContent(content);
        


        // Trivial logic for closing the sub-window
        Button ok = new Button("OK");
        ok.addClickListener(new ClickListener() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
                close(); // Close the sub-window
            }
        });
        content.addComponent(ok);
        
        
        
//        QRCode code = new QRCode();
//        code.setWidth(150, Unit.PIXELS);
//        code.setHeight(150, Unit.PIXELS);
//        code.setValue("Hallo Hubert ;-)");
//        
//        content.addComponent(code);
        
    }
	
	
}
