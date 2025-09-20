/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.config.SteveProperties;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.TimeZone;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.09.2025
 */
@Slf4j
@SpringBootApplication(exclude = {JooqAutoConfiguration.class})
public class SteveApplication {

    static {
        // For Hibernate validator
        System.setProperty("org.jboss.logging.provider", "slf4j");

        TimeZone.setDefault(TimeZone.getTimeZone(SteveProperties.TIME_ZONE_ID));
        DateTimeZone.setDefault(DateTimeZone.forID(SteveProperties.TIME_ZONE_ID));
        log.info("Date/time zone of the application is set to {}. Current date/time: {}", SteveProperties.TIME_ZONE_ID, DateTime.now());
    }

    public static void main(String[] args) throws Exception {
        start(args);
    }

    public static ConfigurableApplicationContext start(String... args) throws Exception {
        return SpringApplication.run(SteveApplication.class, args);
    }

}
