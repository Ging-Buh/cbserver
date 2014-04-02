package cb_server;
import java.util.ArrayList;
import java.util.Iterator;


import CB_Utils.Settings.SettingBase;
import CB_Utils.Settings.SettingBool;
import CB_Utils.Settings.SettingCategory;
import CB_Utils.Settings.SettingDouble;
import CB_Utils.Settings.SettingEnum;
import CB_Utils.Settings.SettingFile;
import CB_Utils.Settings.SettingFloat;
import CB_Utils.Settings.SettingFolder;
import CB_Utils.Settings.SettingInt;
import CB_Utils.Settings.SettingIntArray;
import CB_Utils.Settings.SettingModus;
import CB_Utils.Settings.SettingStoreType;


import CB_Utils.Settings.SettingString;
import CB_Utils.Settings.SettingStringArray;
import CB_Utils.Settings.SettingTime;
import CB_Utils.Settings.SettingsAudio;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.jasoft.qrcode.QRCode;

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


	private ArrayList<SettingCategory> Categorys;
	
	
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
        
        QRCode code = new QRCode();
        code.setWidth(150, Unit.PIXELS);
        code.setHeight(150, Unit.PIXELS);
        code.setValue("Hallo Hubert ;-)");
        
        content.addComponent(code);
        
        
        fillContent();
        
    }
	
	
	private void fillContent()
	{
		// Categorie List zusammen stellen

				if (Categorys == null)
				{
					Categorys = new ArrayList<SettingCategory>();
				}

				Categorys.clear();
				SettingCategory[] tmp = SettingCategory.values();
				for (SettingCategory item : tmp)
				{
					if (item != SettingCategory.Button)
					{
						Categorys.add(item);
					}

				}
		
				Iterator<SettingCategory> iteratorCat = Categorys.iterator();
				if (iteratorCat != null && iteratorCat.hasNext())
				{

					ArrayList<SettingBase<?>> SortedSettingList = new ArrayList<SettingBase<?>>();

					for (Iterator<SettingBase<?>> it = Config.settings.iterator(); it.hasNext();)
					{
						SettingBase<?> setting = it.next();
						SortedSettingList.add(setting);
					}

					do
					{
						int position = 0;

						SettingCategory cat = iteratorCat.next();
						

						// add Cat einträge
											
						VerticalLayout lay = new VerticalLayout();
												int entryCount = 0;
						
					
						// int layoutHeight = 0;
						for (Iterator<SettingBase<?>> it = SortedSettingList.iterator(); it.hasNext();)
						{
							SettingBase<?> settingItem = it.next();
							if (settingItem.getCategory().name().equals(cat.name()))
							{
								// item nur zur Liste Hinzufügen, wenn der
								// SettingModus
								// dies auch zu lässt.
//								if (settingItem.getModus() != SettingModus.develop || GlobalCore.isDevelop())
//								{

									if ((settingItem.getModus() == SettingModus.Normal)
											 && (settingItem.getModus() != SettingModus.Never))
									{

										final Component view = getView(settingItem, position++);

										lay.addComponent(view);
										entryCount++;
										Config.settings.indexOf(settingItem);
										
									}
//								}
							}
						}

						if (entryCount > 0)
						{
							
							addControlToLinearLayout(lay, 100);

							
						}

					}
					while (iteratorCat.hasNext());

				}
		
	}

	
	
	private void addControlToLinearLayout(Component view, float itemMargin)
	{
		// TODO
	}
	
	
	private Component getView(SettingBase<?> SB, int BackgroundChanger)
	{
		if (SB instanceof SettingBool)
		{
			return getBoolView((SettingBool) SB, BackgroundChanger);
		}
		else if (SB instanceof SettingIntArray)
		{
			return getIntArrayView((SettingIntArray) SB, BackgroundChanger);
		}
		else if (SB instanceof SettingStringArray)
		{
			return getStringArrayView((SettingStringArray) SB, BackgroundChanger);
		}
		else if (SB instanceof SettingTime)
		{
			return getTimeView((SettingTime) SB, BackgroundChanger);
		}
		else if (SB instanceof SettingInt)
		{
			return getIntView((SettingInt) SB, BackgroundChanger);
		}
		else if (SB instanceof SettingDouble)
		{
			return getDblView((SettingDouble) SB, BackgroundChanger);
		}
		else if (SB instanceof SettingFloat)
		{
			return getFloatView((SettingFloat) SB, BackgroundChanger);
		}
		else if (SB instanceof SettingFolder)
		{
			return getFolderView((SettingFolder) SB, BackgroundChanger);
		}
		else if (SB instanceof SettingFile)
		{
			return getFileView((SettingFile) SB, BackgroundChanger);
		}
		else if (SB instanceof SettingEnum)
		{
			return getEnumView((SettingEnum<?>) SB, BackgroundChanger);
		}
		else if (SB instanceof SettingString)
		{
			return getStringView((SettingString) SB, BackgroundChanger);
		}
		else if (SB instanceof SettingsAudio)
		{
			return getAudioView((SettingsAudio) SB, BackgroundChanger);
		}

		return null;
	}


	private Component getAudioView(SettingsAudio sB, int backgroundChanger) {
		// TODO Auto-generated method stub
		return null;
	}


	private Component getStringView(SettingString sB, int backgroundChanger) {
		// TODO Auto-generated method stub
		return null;
	}


	private Component getEnumView(SettingEnum<?> sB, int backgroundChanger) {
		// TODO Auto-generated method stub
		return null;
	}


	private Component getFileView(SettingFile sB, int backgroundChanger) {
		// TODO Auto-generated method stub
		return null;
	}


	private Component getFolderView(SettingFolder sB, int backgroundChanger) {
		// TODO Auto-generated method stub
		return null;
	}


	private Component getFloatView(SettingFloat sB, int backgroundChanger) {
		// TODO Auto-generated method stub
		return null;
	}


	private Component getDblView(SettingDouble sB, int backgroundChanger) {
		// TODO Auto-generated method stub
		return null;
	}


	private Component getIntView(SettingInt sB, int backgroundChanger) {
		// TODO Auto-generated method stub
		return null;
	}


	private Component getTimeView(SettingTime sB, int backgroundChanger) {
		// TODO Auto-generated method stub
		return null;
	}


	private Component getStringArrayView(SettingStringArray sB,
			int backgroundChanger) {
		// TODO Auto-generated method stub
		return null;
	}


	private Component getIntArrayView(SettingIntArray sB, int backgroundChanger) {
		// TODO Auto-generated method stub
		return null;
	}


	private Component getBoolView(SettingBool sB, int backgroundChanger) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
