package de.rwth.idsg.steve;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static de.rwth.idsg.steve.SteveConfiguration.*;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.01.2015
 */
public class Application {

    public static void main(String[] args) throws IOException {
        loadProperties();

        JettyServer jettyServer = new JettyServer();
        jettyServer.start();
    }

    private static void loadProperties() throws IOException {
        final String fileName = "main.properties";
        Properties prop = new Properties();

        InputStream is = Application.class.getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            throw new FileNotFoundException("Property file '" + fileName + "' is not found in classpath");
        } else {
            prop.load(is);
        }

        STEVE_VERSION = prop.getProperty("steve.version");

        DB.URL      = prop.getProperty("db.url");
        DB.USERNAME = prop.getProperty("db.user");
        DB.PASSWORD = prop.getProperty("db.password");

        Auth.USERNAME   = prop.getProperty("auth.user");
        Auth.PASSWORD   = prop.getProperty("auth.password");

        Jetty.SERVER_HOST           = prop.getProperty("server.host");
        Jetty.SERVER_PORT           = Integer.valueOf(prop.getProperty("server.port"));
        Jetty.SSL_ENABLED           = Boolean.valueOf(prop.getProperty("ssl.enabled"));
        Jetty.SSL_SERVER_PORT       = Integer.valueOf(prop.getProperty("ssl.server.port"));
        Jetty.KEY_STORE_PATH        = prop.getProperty("keystore.path");
        Jetty.KEY_STORE_PASSWORD    = prop.getProperty("keystore.password");
    }
}
