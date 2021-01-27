package de.rwth.idsg.steve;

import de.rwth.idsg.steve.config.WebEnvironment;
import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategy;
import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategyEnum;
import de.rwth.idsg.steve.utils.PropertiesFileLoader;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.beans.ConstructorProperties;

import javax.annotation.PostConstruct;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * SteVe configuration component modified for plugins
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @author Andor Toth
 * @since 19.08.2014
 */
@Component
public class SteveConfiguration {
    //CONFIG;

    // Root mapping for Spring
    //private final String springMapping = "/";
    // Web frontend
    //private final String springManagerMapping = "/ocpp/manager/*";
    // Mapping for CXF SOAP services
    //private final String cxfMapping = "/ocpp/services/*";
    // Dummy service path
    private static final String routerEndpointPath = WebEnvironment.getContextRoot()+"/CentralSystemService";
    // Time zone for the application and database connections
    private final String timeZoneId = "UTC";  // or ZoneId.systemDefault().getId();

    // -------------------------------------------------------------------------
    // main.properties
    // -------------------------------------------------------------------------

    private Ocpp ocpp;

    @Value("${ocpp.ws.session.select.strategy:ALWAYS_LAST}")
    private String wsSessionSelectStrategy;

    @Value("${auto.register.unknown.stations:false}")
    private boolean autoRegisterUnknownStations;

    @Value("${keystore.path:}")
    @Getter
    private String keystorePath;

    @Value("${keystore.password:}")
    @Getter
    private String keystorePassword;

    @PostConstruct
    public void init() {



        ocpp = Ocpp.builder()
                .autoRegisterUnknownStations(autoRegisterUnknownStations)
                .wsSessionSelectStrategy(
                        WsSessionSelectStrategyEnum.fromName(wsSessionSelectStrategy))
                .build();


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

    private String sanitizeContextPath(String s) {
        if (s == null || "/".equals(s)) {
            return "";

        } else if (s.startsWith("/")) {
            return s;

        } else {
            return "/" + s;
        }
    }


    // OCPP-related configuration
    @Builder
    @Getter
    public static class Ocpp {
        private final boolean autoRegisterUnknownStations;
        private final WsSessionSelectStrategy wsSessionSelectStrategy;


    }

    public Ocpp getOcpp() {
        return ocpp;
    }


    public static String getRouterEndpointPath() {
        return routerEndpointPath;
    }

    public WsSessionSelectStrategy getWsSessionSelectStrategy() {
        return getOcpp().getWsSessionSelectStrategy();
    }

    public boolean isAutoRegisterUnknownStations() {
        return getOcpp().isAutoRegisterUnknownStations();
    }
}
