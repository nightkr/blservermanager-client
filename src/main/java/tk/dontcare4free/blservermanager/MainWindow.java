package tk.dontcare4free.blservermanager;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPopupMenu;
import javax.swing.BoxLayout;

import java.awt.Container;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.AWTException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.ConnectException;

import org.jdesktop.swinghelper.tray.JXTrayIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWindow extends JFrame {
	private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

	JXTrayIcon trayIcon;
	JTextField adminKeyField;

	Client wsc;

	public MainWindow() {
		super("Blockland Server Manager");

		if (SystemTray.isSupported()) {
			try {
				JPopupMenu trayPopupMenu = new JPopupMenu();
				trayPopupMenu.add("Show").addActionListener(traymenu_item_show_clicked);
				trayPopupMenu.add("Exit").addActionListener(traymenu_item_exit_clicked);

				trayIcon = new JXTrayIcon(Helpers.createImage("trayIcon.png", "tray icon"));
				trayIcon.setJPopupMenu(trayPopupMenu);
				trayIcon.addActionListener(traymenu_clicked);

				SystemTray.getSystemTray().add(trayIcon);

				setDefaultCloseOperation(HIDE_ON_CLOSE); // Run in background, so hide on close
			} catch(AWTException e) {
				trayIcon = null;
			} catch(Exception e) {
				logger.error("Error initializing the tray icon", e);
				trayIcon = null;
			}
		}
		if (trayIcon == null) {
			setDefaultCloseOperation(EXIT_ON_CLOSE); // Unable to bring back once hidden, so exit on close
			setVisible(true);
		}

		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		Container keyContainer = new Container();
		keyContainer.setLayout(new BoxLayout(keyContainer, BoxLayout.X_AXIS));
		add(keyContainer);
		keyContainer.add(new JLabel("Administration key"));
		adminKeyField = new JTextField(25);
		adminKeyField.setEditable(false);
		disconnected();
		keyContainer.add(adminKeyField);

		pack();

		wsc = new Client(this);
		wsc.connect();
	}

	public void connected(String key) {
		adminKeyField.setText(key);
		adminKeyField.setEnabled(true);
	}

	public void disconnected() {
		adminKeyField.setText("Not connected");
		adminKeyField.setEnabled(false);
	}

	ActionListener traymenu_clicked = new ActionListener() { public void actionPerformed(ActionEvent e) {
		setVisible(true);
	}};
	ActionListener traymenu_item_show_clicked = new ActionListener() { public void actionPerformed(ActionEvent e) {
		setVisible(true);
	}};
	ActionListener traymenu_item_exit_clicked = new ActionListener() { public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}};
}
