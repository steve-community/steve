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
package de.rwth.idsg.steve.gateway.oicp;

import de.rwth.idsg.steve.gateway.adapter.OcppToOicpAdapter;
import de.rwth.idsg.steve.gateway.oicp.model.AuthorizationStart;
import de.rwth.idsg.steve.gateway.oicp.model.AuthorizationStartResponse;
import de.rwth.idsg.steve.gateway.oicp.model.AuthorizationStop;
import de.rwth.idsg.steve.gateway.oicp.model.AuthorizationStopResponse;
import de.rwth.idsg.steve.gateway.oicp.model.EVSEData;
import de.rwth.idsg.steve.gateway.oicp.model.EVSEDataRequest;
import de.rwth.idsg.steve.gateway.oicp.model.EVSEStatusRecord;
import de.rwth.idsg.steve.gateway.oicp.model.EVSEStatusRequest;
import de.rwth.idsg.steve.gateway.oicp.model.Identification;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OICP Adapter
 *
 * @author Steve Community
 */
@DisplayName("OICP Adapter Tests")
class OicpAdapterTest {

    @Mock
    private ChargePointRepository chargePointRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private OcppTagRepository ocppTagRepository;

    private OcppToOicpAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adapter = new OcppToOicpAdapter(
            chargePointRepository,
            transactionRepository,
            ocppTagRepository
        );
    }

    @Test
    @DisplayName("getEVSEData should return empty list when no charge points exist")
    void testGetEVSEDataEmpty() {
        when(chargePointRepository.getOverview(any(ChargePointQueryForm.class)))
            .thenReturn(new ArrayList<>());

        EVSEDataRequest request = new EVSEDataRequest();
        List<EVSEData> evseDataList = adapter.getEVSEData("DE*STE", request);

        assertNotNull(evseDataList);
        assertTrue(evseDataList.isEmpty());
        verify(chargePointRepository).getOverview(any(ChargePointQueryForm.class));
    }

    @Test
    @DisplayName("getEVSEStatus should return empty list when no connectors exist")
    void testGetEVSEStatusEmpty() {
        when(chargePointRepository.getChargePointConnectorStatus(any(ConnectorStatusForm.class)))
            .thenReturn(new ArrayList<>());

        EVSEStatusRequest request = new EVSEStatusRequest();
        List<EVSEStatusRecord> statusList = adapter.getEVSEStatus("DE*STE", request);

        assertNotNull(statusList);
        assertTrue(statusList.isEmpty());
        verify(chargePointRepository).getChargePointConnectorStatus(any(ConnectorStatusForm.class));
    }

    @Test
    @DisplayName("authorizeStart should return response")
    void testAuthorizeStart() {
        AuthorizationStart authStart = new AuthorizationStart();
        Identification identification = new Identification();
        identification.setRfidId("RFID123456");
        authStart.setIdentification(identification);
        authStart.setEvseId("DE*STE*E123456*1");

        AuthorizationStartResponse response = adapter.authorizeStart(authStart);

        assertNotNull(response);
        assertNotNull(response.getAuthorizationStatus());
    }

    @Test
    @DisplayName("authorizeStop should return response")
    void testAuthorizeStop() {
        AuthorizationStop authStop = new AuthorizationStop();
        Identification identification = new Identification();
        identification.setRfidId("RFID123456");
        authStop.setIdentification(identification);
        authStop.setEvseId("DE*STE*E123456*1");
        authStop.setSessionId("SESSION123");

        AuthorizationStopResponse response = adapter.authorizeStop(authStop);

        assertNotNull(response);
        assertNotNull(response.getAuthorizationStatus());
    }

    @Test
    @DisplayName("authorizeStart with null identification should throw exception")
    void testAuthorizeStartNullIdentification() {
        AuthorizationStart authStart = new AuthorizationStart();
        authStart.setEvseId("DE*STE*E123456*1");

        assertThrows(NullPointerException.class, () -> {
            adapter.authorizeStart(authStart);
        });
    }

    @Test
    @DisplayName("authorizeStop with null identification should throw exception")
    void testAuthorizeStopNullIdentification() {
        AuthorizationStop authStop = new AuthorizationStop();
        authStop.setEvseId("DE*STE*E123456*1");
        authStop.setSessionId("SESSION123");

        assertThrows(NullPointerException.class, () -> {
            adapter.authorizeStop(authStop);
        });
    }
}
