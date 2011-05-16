package tk.dontcare4free.blservermanager;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.SwingUtilities;

public class Main {
	public static void main(String... args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

					for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
						if (info.getName().equals("Nimbus")) {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch(ClassNotFoundException e) {
				} catch(InstantiationException e) {
				} catch(IllegalAccessException e) {
				} catch(UnsupportedLookAndFeelException e) {
				}

				MainWindow mainWindow = new MainWindow();
			}
		});
	}
}
