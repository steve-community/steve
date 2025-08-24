package de.rwth.idsg.steve;

import de.rwth.idsg.steve.utils.PropertiesFileLoader;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@UtilityClass
public class SteveConfigurationReader {

    public static SteveConfiguration readSteveConfiguration(String name) {
        PropertiesFileLoader p = new PropertiesFileLoader(name);

        var profile = ApplicationProfile.fromName(p.getString("profile"));
        System.setProperty("spring.profiles.active", profile.name().toLowerCase());

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        var config = SteveConfiguration.builder()
                .contextPath(sanitizeContextPath(p.getOptionalString("context.path").orElse(null)))
                .steveVersion(p.getString("steve.version"))
                .gitDescribe(useFallbackIfNotSet(p.getOptionalString("git.describe").orElse(null), null))
                .profile(profile)
                .jetty(SteveConfiguration.Jetty.builder()
                        .serverHost(p.getString("server.host"))
                        .gzipEnabled(p.getBoolean("server.gzip.enabled"))
                        .httpEnabled(p.getBoolean("http.enabled"))
                        .httpPort(p.getInt("http.port"))
                        .httpsEnabled(p.getBoolean("https.enabled"))
                        .httpsPort(p.getInt("https.port"))
                        .keyStorePath(p.getOptionalString("keystore.path").orElse(null))
                        .keyStorePassword(p.getOptionalString("keystore.password").orElse(null))
                        .build())
                .db(SteveConfiguration.DB.builder()
                        .jdbcUrl(p.getString("db.jdbc.url"))
                        .userName(p.getString("db.user"))
                        .password(p.getString("db.password"))
                        .sqlLogging(p.getBoolean("db.sql.logging"))
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
                        .autoRegisterUnknownStations(p.getOptionalBoolean("auto.register.unknown.stations").orElse(false))
                        .chargeBoxIdValidationRegex(p.getOptionalString("charge-box-id.validation.regex").orElse(null))
                        .wsSessionSelectStrategy(p.getString("ws.session.select.strategy"))
                        .build())
                .build();

        config.postConstruct();
        return config;
    }

    private static String useFallbackIfNotSet(String value, String fallback) {
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

    private static String sanitizeContextPath(String s) {
        if (s == null || "/".equals(s)) {
            return "";

        } else if (s.startsWith("/")) {
            return s;

        } else {
            return "/" + s;
        }
    }
}
