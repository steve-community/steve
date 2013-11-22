package de.rwth.idsg.steve.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesFileManager {

	private static final Logger LOG = LoggerFactory.getLogger(PropertiesFileManager.class);
	private static final File catalinaBase = new File(System.getProperty("catalina.base")).getAbsoluteFile();
	private static final File stevePropFile = new File(catalinaBase, "conf" + File.separator + "steve.properties");

	/**
	 * Reads the properties from file.
	 */
	public static void getFile() {
		try {
			if (stevePropFile.exists()) {
				FileInputStream input = new FileInputStream(stevePropFile);
				Properties prop = new Properties();
				prop.load(input);
				input.close();

				Constants.HEARTBEAT_INTERVAL = Integer.parseInt(prop.getProperty("hearbeatInterval"));
				Constants.HOURS_TO_EXPIRE = Integer.parseInt(prop.getProperty("hoursToExpire"));
			} else {
				writeFile();
			}
		} catch (FileNotFoundException e) {
			LOG.error("Exception happened", e);
		} catch (IOException e) {
			LOG.error("Exception happened", e);
		}
	}

	/**
	 * Writes properties to file.
	 * If the file is not created yet, it first creates one.
	 */
	public static void writeFile() {
		
		if (!stevePropFile.exists()){
			try {
				stevePropFile.createNewFile();
			} catch (IOException e) {
				LOG.error("Exception happened", e);
			}
		}

		Properties prop = new Properties();
		prop.setProperty("hearbeatInterval", String.valueOf(Constants.HEARTBEAT_INTERVAL));
		prop.setProperty("hoursToExpire", String.valueOf(Constants.HOURS_TO_EXPIRE));

		try {
			prop.store(new FileOutputStream(stevePropFile), null);
		} catch (FileNotFoundException e) {
			LOG.error("Exception happened", e);
		} catch (IOException e) {
			LOG.error("Exception happened", e);
		}
	}
}
