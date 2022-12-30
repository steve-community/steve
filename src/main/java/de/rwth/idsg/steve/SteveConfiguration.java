/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2019 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @author Andor Toth
 * @since 19.08.2014
 */
@Component
public class SteveConfiguration {
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
