package de.rwth.idsg.steve.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Encapsulates java.util.Properties and adds type specific convenience methods
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 01.10.2015
 */
public class PropertiesFileLoader {

    private Properties prop;

    public PropertiesFileLoader(String fileName) {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new FileNotFoundException("Property file '" + fileName + "' is not found in classpath");
            }
            prop = new Properties();
            prop.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getString(String key) {
        return prop.getProperty(key);
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(prop.getProperty(key));
    }

    public int getInt(String key) {
        return Integer.parseInt(prop.getProperty(key));
    }
}
