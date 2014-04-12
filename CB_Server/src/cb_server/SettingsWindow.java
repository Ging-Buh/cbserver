package cb_server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
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

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.jasoft.qrcode.QRCode;

public class SettingsWindow extends Window {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final private static SettingsWindow INSTANZ = new SettingsWindow();

	public static SettingsWindow getInstanz() {
		return INSTANZ;
	}

	private ArrayList<SettingCategory> Categorys;

	private VerticalLayout content;
	private VerticalLayout Settingscontent;

	private SettingsWindow() {

		super("Server Settings"); // Set window caption

		this.setWidth(30, Unit.PERCENTAGE);
		this.setHeight(80, Unit.PERCENTAGE);

		center();

		//save act settings for cancel restore
		Config.settings.SaveToLastValue();

		// Some basic content for the window
		content = new VerticalLayout();
		content.setMargin(true);
		setContent(content);

		addSaveCancelButtons();

		fillContent();

	}

	private void addSaveCancelButtons() {
		HorizontalLayout hl = new HorizontalLayout();
		com.vaadin.ui.Button btnSave = new Button("save");
		com.vaadin.ui.Button btnCancel = new Button("cancel");

		hl.addComponent(btnSave);
		hl.addComponent(btnCancel);

		btnCancel.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -4799987364890297976L;

			@Override
			public void buttonClick(ClickEvent event) {
				Config.settings.LoadFromLastValue();
				fillContent();
			}
		});

		btnSave.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -878673538684730570L;

			@Override
			public void buttonClick(ClickEvent event) {
				Config.settings.WriteToDB();
				Config.settings.SaveToLastValue();
				fillContent();
			}
		});

		content.addComponent(hl);
	}

	private void fillContent() {

		if (Settingscontent != null) {
			content.removeComponent(Settingscontent);
			Settingscontent = null;
		}

		Settingscontent = new VerticalLayout();
		content.addComponent(Settingscontent);

		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();

			//Getting IPAddress of localhost - getHostAddress return IP Address
			// in textual format
			String ipAddress = addr.getHostAddress();

			ipAddress = "";
			// Network Interfaces nach IPv4 Adressen durchsuchen
			try {
				Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
				for (NetworkInterface netint : Collections.list(nets)) {
					Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
					for (InetAddress inetAddress : Collections.list(inetAddresses)) {
						if (inetAddress.isLoopbackAddress())
							continue;
						if (inetAddress instanceof Inet4Address) {
							System.out.println("InetAddress: " + inetAddress);
							if (ipAddress.length() > 0) {
								ipAddress += ";";
							}
							ipAddress += inetAddress;
						}
					}
				}

			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			QRCode code = new QRCode();
			code.setWidth(150, Unit.PIXELS);
			code.setHeight(150, Unit.PIXELS);
			code.setValue(ipAddress);

			Settingscontent.addComponent(code);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Categorie List zusammen stellen

		if (Categorys == null) {
			Categorys = new ArrayList<SettingCategory>();
		}

		Categorys.clear();
		SettingCategory[] tmp = SettingCategory.values();
		for (SettingCategory item : tmp) {
			if (item != SettingCategory.Button) {
				Categorys.add(item);
			}

		}

		Iterator<SettingCategory> iteratorCat = Categorys.iterator();
		if (iteratorCat != null && iteratorCat.hasNext()) {

			ArrayList<SettingBase<?>> SortedSettingList = new ArrayList<SettingBase<?>>();

			for (Iterator<SettingBase<?>> it = Config.settings.iterator(); it.hasNext();) {
				SettingBase<?> setting = it.next();
				SortedSettingList.add(setting);
			}

			do {
				int position = 0;

				SettingCategory cat = iteratorCat.next();

				// add Cat einträge

				VerticalLayout lay = new VerticalLayout();
				int entryCount = 0;

				// int layoutHeight = 0;
				for (Iterator<SettingBase<?>> it = SortedSettingList.iterator(); it.hasNext();) {
					SettingBase<?> settingItem = it.next();
					if (settingItem.getCategory().name().equals(cat.name())) {
						// item nur zur Liste Hinzufügen, wenn der
						// SettingModus
						// dies auch zu lässt.
						//								if (settingItem.getModus() != SettingModus.develop || GlobalCore.isDevelop())
						//								{

						if ((settingItem.getModus() == SettingModus.Normal) && (settingItem.getModus() != SettingModus.Never)) {

							final Component view = getView(settingItem, position++);

							if (view == null)
								continue;

							lay.addComponent(view);
							entryCount++;
							Config.settings.indexOf(settingItem);

						}
						//								}
					}
				}

				if (entryCount > 0) {

					addControlToLinearLayout(lay, 100);

				}

			} while (iteratorCat.hasNext());

		}

	}

	private void addControlToLinearLayout(Component view, float itemMargin) {
		Settingscontent.addComponent(view);
	}

	private Component getView(SettingBase<?> SB, int BackgroundChanger) {
		if (SB instanceof SettingBool) {
			return getBoolView((SettingBool) SB, BackgroundChanger);
		} else if (SB instanceof SettingIntArray) {
			return getIntArrayView((SettingIntArray) SB, BackgroundChanger);
		} else if (SB instanceof SettingStringArray) {
			return getStringArrayView((SettingStringArray) SB, BackgroundChanger);
		} else if (SB instanceof SettingTime) {
			return getTimeView((SettingTime) SB, BackgroundChanger);
		} else if (SB instanceof SettingInt) {
			return getIntView((SettingInt) SB, BackgroundChanger);
		} else if (SB instanceof SettingDouble) {
			return getDblView((SettingDouble) SB, BackgroundChanger);
		} else if (SB instanceof SettingFloat) {
			return getFloatView((SettingFloat) SB, BackgroundChanger);
		} else if (SB instanceof SettingFolder) {
			return getFolderView((SettingFolder) SB, BackgroundChanger);
		} else if (SB instanceof SettingFile) {
			return getFileView((SettingFile) SB, BackgroundChanger);
		} else if (SB instanceof SettingEnum) {
			return getEnumView((SettingEnum<?>) SB, BackgroundChanger);
		} else if (SB instanceof SettingString) {
			return getStringView((SettingString) SB, BackgroundChanger);
		} else if (SB instanceof SettingsAudio) {
			return getAudioView((SettingsAudio) SB, BackgroundChanger);
		}

		return null;
	}

	private Component getAudioView(SettingsAudio sB, int backgroundChanger) {
		com.vaadin.ui.HorizontalLayout box = new HorizontalLayout();
		com.vaadin.ui.Label label = new com.vaadin.ui.Label();
		label.setCaption(sB.getName());
		box.addComponent(label);

		return box;
	}

	private Component getStringView(final SettingString sB, int backgroundChanger) {
		com.vaadin.ui.HorizontalLayout box = new HorizontalLayout();
		com.vaadin.ui.TextField input = new TextField(sB.getName(), String.valueOf(sB.getValue()));

		input.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = -634498493292006581L;

			@Override
			public void textChange(TextChangeEvent event) {
				sB.setValue(event.getText());
			}
		});

		box.addComponent(input);
		return box;
	}

	private Component getEnumView(SettingEnum<?> sB, int backgroundChanger) {
		com.vaadin.ui.HorizontalLayout box = new HorizontalLayout();
		com.vaadin.ui.Label label = new com.vaadin.ui.Label();
		label.setCaption(sB.getName());
		box.addComponent(label);

		return box;
	}

	private Component getFileView(SettingFile sB, int backgroundChanger) {
		com.vaadin.ui.HorizontalLayout box = new HorizontalLayout();
		com.vaadin.ui.Label label = new com.vaadin.ui.Label();
		label.setCaption(sB.getName());
		box.addComponent(label);

		return box;
	}

	private Component getFolderView(SettingFolder sB, int backgroundChanger) {
		com.vaadin.ui.HorizontalLayout box = new HorizontalLayout();
		com.vaadin.ui.Label label = new com.vaadin.ui.Label();
		label.setCaption(sB.getName());
		box.addComponent(label);

		return box;
	}

	private Component getFloatView(SettingFloat sB, int backgroundChanger) {
		com.vaadin.ui.HorizontalLayout box = new HorizontalLayout();
		com.vaadin.ui.Label label = new com.vaadin.ui.Label();
		label.setCaption(sB.getName());
		box.addComponent(label);

		return box;
	}

	private Component getDblView(SettingDouble sB, int backgroundChanger) {
		com.vaadin.ui.HorizontalLayout box = new HorizontalLayout();
		com.vaadin.ui.Label label = new com.vaadin.ui.Label();
		label.setCaption(sB.getName());
		box.addComponent(label);

		return box;
	}

	private Component getIntView(final SettingInt sB, int backgroundChanger) {
		com.vaadin.ui.HorizontalLayout box = new HorizontalLayout();
		com.vaadin.ui.TextField input = new TextField(sB.getName(), String.valueOf(sB.getValue()));

		input.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = -634498493292006581L;

			@Override
			public void textChange(TextChangeEvent event) {

				int newValue = Integer.parseInt(event.getText());
				sB.setValue(newValue);
			}
		});

		box.addComponent(input);
		return box;
	}

	private Component getTimeView(SettingTime sB, int backgroundChanger) {
		com.vaadin.ui.HorizontalLayout box = new HorizontalLayout();
		com.vaadin.ui.Label label = new com.vaadin.ui.Label();
		label.setCaption(sB.getName());
		box.addComponent(label);

		return box;
	}

	private Component getStringArrayView(SettingStringArray sB, int backgroundChanger) {
		com.vaadin.ui.HorizontalLayout box = new HorizontalLayout();
		com.vaadin.ui.Label label = new com.vaadin.ui.Label();
		label.setCaption(sB.getName());
		box.addComponent(label);

		return box;
	}

	private Component getIntArrayView(SettingIntArray sB, int backgroundChanger) {
		com.vaadin.ui.HorizontalLayout box = new HorizontalLayout();
		com.vaadin.ui.Label label = new com.vaadin.ui.Label();
		label.setCaption(sB.getName());
		box.addComponent(label);

		return box;
	}

	private Component getBoolView(SettingBool sB, int backgroundChanger) {

		com.vaadin.ui.HorizontalLayout box = new HorizontalLayout();
		com.vaadin.ui.Label label = new com.vaadin.ui.Label();
		label.setCaption(sB.getName());
		box.addComponent(label);

		return box;
	}

}
