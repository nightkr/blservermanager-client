package tk.dontcare4free.blservermanager;

import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tootallnate.websocket.WebSocketClient;

public class Client extends WebSocketClient {
	public Client(MainWindow mainWindow) {
		super(Helpers.ServerURI);

		this.mainWindow = mainWindow;
	}

	private static final class ProcessStdoutRedirector extends Thread {
		Process process;
		Client client;

		ProcessStdoutRedirector(Process process, Client client) {
			this.process = process;
			this.client = client;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(process.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null)
					client.send("console_line:" + line);
				client.send("server_status:offline");
			} catch(IOException e) {
			}
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	private static final ProcessBuilder blocklandProcessBuilder = new ProcessBuilder(Helpers.configuration.getString("blfolder") + File.separator + "Blockland" + (System.getProperty("os.name").startsWith("Windows") ? ".exe" : ""), "-dedicated");

	static {
		blocklandProcessBuilder.redirectErrorStream(true);
	}

	private Process blocklandProcess;

	MainWindow mainWindow;

	public void send(String text) throws IOException {
		text = "~m~" + text.length() + "~m~" + text;

		logger.debug("Sending \"{}\"", text);
		super.send(text);
	}

	public void onOpen() {
	}
	public void onMessage(String rawMessage) {
		logger.debug("Recieved {}", rawMessage);

		rawMessage = rawMessage.split("~", 5)[4];
		if (rawMessage.substring(0, 3).equals("~h~")) {
			try {
				send(rawMessage);
			} catch (IOException e) {
				logger.warn("Unable to send {}", rawMessage, e);
			}
		} else {
			String[] commandAndMessage = rawMessage.split(":", 2);
			String command = commandAndMessage[0];
			String message = commandAndMessage.length == 2 ? commandAndMessage[1] : "";

			try {
				if (command.equals("key")) {
					mainWindow.connected(message);
				}
				if (command.equals("newclient")) {
					send("server_status:" + (blocklandProcess == null ? "offline" : "online"));
				}
				if (command.equals("start_server")) {
					if (blocklandProcess == null) {
						eval("");
						blocklandProcess = blocklandProcessBuilder.start();
						new ProcessStdoutRedirector(blocklandProcess, this).start();
					}
					send("server_status:online");
				}
				if (command.equals("stop_server")) {
					if (blocklandProcess != null) {
						eval("quit();");
						blocklandProcess = null;
					}
					send("server_status:offline");
				}
				if (command.equals("eval")) {
					if (blocklandProcess != null) {
						eval(message);
					}
				}
			} catch(IOException e) {
				logger.warn("Unable to send {}", rawMessage, e);
			}
		}
	}
	public void onClose() {
		mainWindow.disconnected();
	}

	public void eval(String command) {
		try {
			File evalfile = new File(Helpers.configuration.getString("blfolder") + File.separator + "config" + File.separator + "evalfile.cs");
			FileWriter writer = new FileWriter(evalfile);
			writer.write(command + "\n");
			writer.close();
		} catch(IOException e) {
			logger.warn("Unable to eval {}", command, e);
			try {
				send("console_line:* Unable to eval \"" + command + "\"");
			} catch(IOException e2) {
				logger.warn("Unable to send error message to clients", e2);
			}
		}
	}
}
