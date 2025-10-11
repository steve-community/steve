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
package de.rwth.idsg.steve.gateway.oicp.controller;

import de.rwth.idsg.steve.gateway.oicp.adapter.OcppToOicpAdapter;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAcknowledgment;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizationStart;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizationStart.AuthorizationStatusEnum;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizationStop;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizeRemoteStart;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizeRemoteStop;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizeStart;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizeStop;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingChargeDetailRecord;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingChargeDetailRecords;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingGetChargeDetailRecords;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static de.rwth.idsg.steve.gateway.oicp.OicpResponse.errorCode;
import static de.rwth.idsg.steve.gateway.oicp.OicpResponse.toResponse;

/**
 * OICP v2.3 Authorization REST Controller
 * Handles authorization start and stop requests
 *
 * @author Steve Community
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class AuthorizationController implements ERoamingAuthorizationApi {

    private final OcppToOicpAdapter ocppToOicpAdapter;

    @Override
    public ResponseEntity<ERoamingAcknowledgment> eRoamingAuthorizeRemoteStartV21(
            String providerID, ERoamingAuthorizeRemoteStart eroamingAuthorizeRemoteStart) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResponseEntity<ERoamingAcknowledgment> eRoamingAuthorizeRemoteStopV21(
            String externalID, ERoamingAuthorizeRemoteStop eroamingAuthorizeRemoteStop) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResponseEntity<ERoamingAuthorizationStart> eRoamingAuthorizeStartV21(
            String operatorID, ERoamingAuthorizeStart eroamingAuthorizeStart) {
        log.debug("Authorization start request: {}", eroamingAuthorizeStart);

        try {
            var response = ocppToOicpAdapter.authorizeStart(eroamingAuthorizeStart);
            return toResponse(response);
        } catch (Exception e) {
            log.error("Error during authorization start", e);
            return toResponse(ERoamingAuthorizationStart.builder()
                    .sessionID(eroamingAuthorizeStart.getSessionID())
                    .statusCode(errorCode("6000", "Authorization failed"))
                    .authorizationStatus(AuthorizationStatusEnum.NOT_AUTHORIZED)
                    .build());
        }
    }

    @Override
    public ResponseEntity<ERoamingAuthorizationStop> eRoamingAuthorizeStopV21(
            String operatorID, ERoamingAuthorizeStop request) {
        log.debug("Authorization stop request: {}", request);

        try {
            return toResponse(ocppToOicpAdapter.authorizeStop(request));
        } catch (Exception e) {
            log.error("Error during authorization stop", e);
            return toResponse(ERoamingAuthorizationStop.builder()
                    .sessionID(request.getSessionID())
                    .statusCode(errorCode("6000", "Authorization stop failed"))
                    .authorizationStatus(ERoamingAuthorizationStop.AuthorizationStatusEnum.NOT_AUTHORIZED)
                    .build());
        }
    }

    @Override
    public ResponseEntity<ERoamingAcknowledgment> eRoamingChargeDetailRecordV22(
            String operatorID, ERoamingChargeDetailRecord cdr) {
        log.debug("Charge detail record received: {}", cdr);

        try {
            return toResponse(ocppToOicpAdapter.processChargeDetailRecord(cdr));
        } catch (Exception e) {
            log.error("Error processing charge detail record", e);
            return toResponse(ERoamingAcknowledgment.builder()
                    .sessionID(cdr.getSessionID())
                    .statusCode(errorCode("4000", "Unable to process charge detail record"))
                    .result(false)
                    .build());
        }
    }

    @Override
    public ResponseEntity<ERoamingChargeDetailRecords> eRoamingGetChargeDetailRecordsV22(
            String providerID, ERoamingGetChargeDetailRecords eroamingGetChargeDetailRecords) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
