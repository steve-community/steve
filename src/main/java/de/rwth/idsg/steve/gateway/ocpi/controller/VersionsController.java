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
package de.rwth.idsg.steve.gateway.ocpi.controller;

import de.rwth.idsg.steve.gateway.ocpi.model.Endpoint;
import de.rwth.idsg.steve.gateway.ocpi.model.Version;
import de.rwth.idsg.steve.gateway.ocpi.model.VersionDetail;
import de.rwth.idsg.steve.gateway.ocpi.model.OcpiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * OCPI v2.2 Versions REST Controller
 * Provides version information and endpoint discovery
 *
 * @author Steve Community
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequestMapping(value = "/ocpi", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VersionsController {

    @GetMapping("/versions")
    public OcpiResponse<List<Version>> getVersions() {
        log.debug("Get versions request");

        List<Version> versions = Arrays.asList(
            Version.builder()
                .version("2.2")
                .url("/ocpi/2.2")
                .build()
        );

        return OcpiResponse.success(versions);
    }

    @GetMapping("/2.2")
    public OcpiResponse<VersionDetail> getVersionDetail() {
        log.debug("Get version 2.2 detail request");

        List<Endpoint> endpoints = Arrays.asList(
            Endpoint.builder()
                .identifier("credentials")
                .role("CPO")
                .url("/ocpi/2.2/credentials")
                .build(),
            Endpoint.builder()
                .identifier("locations")
                .role("CPO")
                .url("/ocpi/cpo/2.2/locations")
                .build(),
            Endpoint.builder()
                .identifier("sessions")
                .role("CPO")
                .url("/ocpi/cpo/2.2/sessions")
                .build(),
            Endpoint.builder()
                .identifier("cdrs")
                .role("CPO")
                .url("/ocpi/cpo/2.2/cdrs")
                .build(),
            Endpoint.builder()
                .identifier("tokens")
                .role("EMSP")
                .url("/ocpi/emsp/2.2/tokens")
                .build()
        );

        VersionDetail versionDetail = VersionDetail.builder()
            .version("2.2")
            .endpoints(endpoints)
            .build();

        return OcpiResponse.success(versionDetail);
    }
}