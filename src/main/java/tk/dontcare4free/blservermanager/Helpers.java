package tk.dontcare4free.blservermanager;

import javax.swing.ImageIcon;

import java.awt.Image;

import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;

import java.io.FileNotFoundException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

final class Helpers {
	private static final Logger logger = LoggerFactory.getLogger(Helpers.class);

	private Helpers(){}

	static URI ServerURI;

	static Configuration configuration;

	static {
		String ServerURIString = "ws://dontcare4free.tk:8000/blmanagerserverdesktop/websocket/";
		try {
			ServerURI = new URI(ServerURIString);
		} catch(URISyntaxException e) {
			logger.error("{} is an invalid URI", ServerURIString, e);
			System.exit(1);
		}
		String configurationFileName = "blservermanager.xml";
		try {
			configuration = new XMLConfiguration(configurationFileName);
		} catch(ConfigurationException e) {
			logger.error("Unable to load the configuration {}", configurationFileName, e);
		}
	}

	static Image createImage(String path, String description) throws FileNotFoundException {
		URL url = Helpers.class.getResource(path);
		if (url == null) {
			throw new FileNotFoundException(path);
		} else {
			return (new ImageIcon(url, description)).getImage();
		}
	}
}
