package CB_ServerTray;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;







/*
 * HelloWorldSwing.java requires no other files. 
 */
import javax.swing.*;

import cb_server.CacheboxServer;

public class CBServerTray {
	private static Process p = null;
	
	public CBServerTray() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("HelloWorldSwing");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add the ubiquitous "Hello World" label.
		JLabel label = new JLabel("Hello World");
		frame.getContentPane().add(label);

		//Display the window.
		frame.pack();
		frame.setVisible(false);

		if (SystemTray.isSupported()) {
			final PopupMenu popup = new PopupMenu();
			final TrayIcon trayIcon = new TrayIcon(createImage("images/bulb.gif", "tray icon"));
			final SystemTray tray = SystemTray.getSystemTray();

			// Create a pop-up menu components
			MenuItem aboutItem = new MenuItem("About");
			CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
			CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
			Menu displayMenu = new Menu("Display");
			MenuItem errorItem = new MenuItem("Error");
			MenuItem warningItem = new MenuItem("Warning");
			MenuItem startItem = new MenuItem("Start CBServer");
			MenuItem stopItem = new MenuItem("Stop CBServer");
			MenuItem exitItem = new MenuItem("Exit");

			//Add components to pop-up menu
			popup.add(aboutItem);
			popup.addSeparator();
			popup.add(cb1);
			popup.add(cb2);
			popup.addSeparator();
			popup.add(displayMenu);
			displayMenu.add(errorItem);
			displayMenu.add(warningItem);
			displayMenu.add(startItem);
			displayMenu.add(stopItem);
			popup.add(exitItem);

			trayIcon.setPopupMenu(popup);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.out.println("TrayIcon could not be added.");
			}

			exitItem.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {

				}
			});
			
			startItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
//					try {
//						String line;
//						
//						p = Runtime.getRuntime().exec("java -jar d:/wincachebox/cbserver.jar",  new String[] { "-Xmx2048m"}, new File("d:/wincachebox"));
//						BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
//						while ((line = input.readLine()) != null) {
//							System.out.println(line);
//						}
//						input.close();
//						
//					} catch (Exception err) {
//						err.printStackTrace();
//					}
					try {
						Thread t = new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									CacheboxServer.main(new String[] { });
									System.out.println("Server Stopped");
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						t.start();
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			
			stopItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (p != null) {
						p.destroy();
						p = null;
					}
				}
			});
		}
	}

	//Obtain the image URL
	protected static Image createImage(String path, String description) {
		URL imageURL = CBServerTray.class.getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}

	public static void main(String[] args) {
		try {
			CacheboxServer.main(args);
			System.out.println("Server Stopped");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			


			}
		});
	}

}
