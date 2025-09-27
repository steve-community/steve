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
package de.rwth.idsg.steve.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@ComponentScan(basePackages = "de.rwth.idsg.steve.gateway")
public class GatewayConfiguration {

    private final GatewayProperties gatewayProperties;

    public void init() {
        log.info("Gateway layer initialized");
        if (gatewayProperties.getOcpi().isEnabled()) {
            log.info("OCPI {} support enabled - Party: {}/{}",
                gatewayProperties.getOcpi().getVersion(),
                gatewayProperties.getOcpi().getCountryCode(),
                gatewayProperties.getOcpi().getPartyId());
        }
        if (gatewayProperties.getOicp().isEnabled()) {
            log.info("OICP {} support enabled - Provider: {}",
                gatewayProperties.getOicp().getVersion(),
                gatewayProperties.getOicp().getProviderId());
        }
    }
}