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

import de.rwth.idsg.steve.gateway.adapter.OcppToOcpiAdapter;
import de.rwth.idsg.steve.gateway.ocpi.model.CDR;
import de.rwth.idsg.steve.gateway.ocpi.model.OcpiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * OCPI v2.2 CDRs (Charge Detail Records) REST Controller
 * Provides access to charge detail records for CPO
 *
 * @author Steve Community
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequestMapping(value = "/ocpi/cpo/2.2/cdrs", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CDRsController {

    private final OcppToOcpiAdapter ocppToOcpiAdapter;

    @GetMapping
    public OcpiResponse<List<CDR>> getCDRs(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit) {

        log.debug("Get CDRs request - dateFrom: {}, dateTo: {}, offset: {}, limit: {}",
                  dateFrom, dateTo, offset, limit);

        try {
            List<CDR> cdrs = ocppToOcpiAdapter.getCDRs(dateFrom, dateTo, offset, limit);
            return OcpiResponse.success(cdrs);
        } catch (Exception e) {
            log.error("Error retrieving CDRs", e);
            return OcpiResponse.error(2000, "Unable to retrieve CDRs");
        }
    }

    @GetMapping("/{cdrId}")
    public OcpiResponse<CDR> getCDR(@PathVariable String cdrId) {
        log.debug("Get CDR request for cdrId: {}", cdrId);

        try {
            CDR cdr = ocppToOcpiAdapter.getCDR(cdrId);
            if (cdr != null) {
                return OcpiResponse.success(cdr);
            } else {
                return OcpiResponse.error(2003, "CDR not found");
            }
        } catch (Exception e) {
            log.error("Error retrieving CDR: {}", cdrId, e);
            return OcpiResponse.error(2000, "Unable to retrieve CDR");
        }
    }
}