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
package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.ApplicationProfile;
import de.rwth.idsg.steve.SteveConfiguration;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@UtilityClass
public class SteveConfigurationReader {

    public static SteveConfiguration readSteveConfiguration(String name) {
        PropertiesFileLoader p = new PropertiesFileLoader(name);

        var profile = ApplicationProfile.fromName(p.getString("profile"));
        System.setProperty("spring.profiles.active", profile.name().toLowerCase(Locale.getDefault()));

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        var config = SteveConfiguration.builder()
                .paths(SteveConfiguration.Paths.builder()
                        .rootMapping("/")
                        .managerMapping("/manager")
                        .apiMapping("/api")
                        .soapMapping("/services")
                        .websocketMapping("/websocket")
                        .routerEndpointPath("/CentralSystemService")
                        .contextPath(sanitizeContextPath(
                                p.getOptionalString("context.path").orElse(null)))
                        .build())
                .timeZoneId("UTC")
                .steveVersion(p.getString("steve.version"))
                .gitDescribe(
                        useFallbackIfNotSet(p.getOptionalString("git.describe").orElse(null), null))
                .profile(profile)
                .jetty(SteveConfiguration.Jetty.builder()
                        .serverHost(p.getString("server.host"))
                        .gzipEnabled(p.getBoolean("server.gzip.enabled"))
                        .httpEnabled(p.getBoolean("http.enabled"))
                        .httpPort(p.getInt("http.port"))
                        .httpsEnabled(p.getBoolean("https.enabled"))
                        .httpsPort(p.getInt("https.port"))
                        .keyStorePath(p.getOptionalString("keystore.path").orElse(null))
                        .keyStorePassword(
                                p.getOptionalString("keystore.password").orElse(null))
                        .build())
                .db(SteveConfiguration.DB
                        .builder()
                        .jdbcUrl(p.getString("db.jdbc.url"))
                        .userName(p.getString("db.user"))
                        .password(p.getString("db.password"))
                        .sqlLogging(p.getBoolean("db.sql.logging"))
                        .schema(p.getOptionalString("db.schema").orElse(null))
                        .schemaSource(p.getOptionalString("db.schema-source").orElse("stevedb"))
                        .build())
                .auth(SteveConfiguration.Auth.builder()
                        .passwordEncoder(encoder)
                        .userName(p.getString("auth.user"))
                        .encodedPassword(encoder.encode(p.getString("auth.password")))
                        .build())
                .webApi(SteveConfiguration.WebApi.builder()
                        .headerKey(p.getOptionalString("webapi.key").orElse(null))
                        .headerValue(p.getOptionalString("webapi.value").orElse(null))
                        .build())
                .ocpp(SteveConfiguration.Ocpp.builder()
                        .autoRegisterUnknownStations(p.getOptionalBoolean("auto.register.unknown.stations")
                                .orElse(false))
                        .chargeBoxIdValidationRegex(p.getOptionalString("charge-box-id.validation.regex")
                                .orElse(null))
                        .wsSessionSelectStrategy(p.getString("ws.session.select.strategy"))
                        .build())
                .build();

        config.postConstruct();
        return config;
    }

    private static @Nullable String useFallbackIfNotSet(@Nullable String value, @Nullable String fallback) {
        if (value == null) {
            // if the property is optional, value will be null
            return fallback;
        } else if (value.startsWith("${")) {
            // property value variables start with "${" (if maven is not used, the value will not be set)
            return fallback;
        } else {
            return value;
        }
    }

    private static String sanitizeContextPath(@Nullable String s) {
        if (s == null || "/".equals(s)) {
            return "";

        } else if (s.startsWith("/")) {
            return s;

        } else {
            return "/" + s;
        }
    }
}
