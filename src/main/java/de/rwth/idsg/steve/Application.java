package de.rwth.idsg.steve;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.TimeZone;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.01.2015
 */
@Slf4j
public class Application {

    public static void main(String[] args) throws Exception {

        // For Hibernate validator
        System.setProperty("org.jboss.logging.provider", "slf4j");

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateTimeZone.setDefault(DateTimeZone.UTC);
        log.info("Date/time zone of the application is set to UTC. Current date/time: {}", DateTime.now());

        SteveConfiguration sc = SteveConfiguration.CONFIG;

        log.info("Loaded the properties. Starting with the '{}' profile", sc.getProfile());

        if (sc.getProfile().isProd()) {
            new SteveProdStarter().start();
        } else {
            new SteveDevStarter().start();
        }
    }
}
