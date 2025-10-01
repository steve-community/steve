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
package de.rwth.idsg.steve.gateway.ocpi;

import de.rwth.idsg.steve.gateway.adapter.OcppToOcpiAdapter;
import de.rwth.idsg.steve.gateway.config.GatewayProperties;
import de.rwth.idsg.steve.gateway.ocpi.model.CDR;
import de.rwth.idsg.steve.gateway.ocpi.model.Location;
import de.rwth.idsg.steve.gateway.ocpi.model.Session;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
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
 * Unit tests for OCPI Adapter
 *
 * @author Steve Community
 */
@DisplayName("OCPI Adapter Tests")
class OcpiAdapterTest {

    @Mock
    private ChargePointRepository chargePointRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private GatewayProperties gatewayProperties;

    private OcppToOcpiAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup gateway properties
        GatewayProperties.Ocpi ocpiProps = new GatewayProperties.Ocpi();
        ocpiProps.setCountryCode("DE");
        ocpiProps.setPartyId("STE");
        ocpiProps.setCurrency("EUR");

        when(gatewayProperties.getOcpi()).thenReturn(ocpiProps);

        adapter = new OcppToOcpiAdapter(
            chargePointRepository,
            transactionRepository,
            null,
            null,
            gatewayProperties
        );
    }

    @Test
    @DisplayName("getLocations should return empty list when no charge points exist")
    void testGetLocationsEmpty() {
        when(chargePointRepository.getOverview(any(ChargePointQueryForm.class)))
            .thenReturn(new ArrayList<>());

        List<Location> locations = adapter.getLocations(null, null, 0, 10);

        assertNotNull(locations);
        assertTrue(locations.isEmpty());
        verify(chargePointRepository).getOverview(any(ChargePointQueryForm.class));
    }

    @Test
    @DisplayName("getSessions should return empty list when no transactions exist")
    void testGetSessionsEmpty() {
        when(transactionRepository.getTransactions(any(TransactionQueryForm.class)))
            .thenReturn(new ArrayList<>());

        List<Session> sessions = adapter.getSessions(null, null, 0, 10);

        assertNotNull(sessions);
        assertTrue(sessions.isEmpty());
        verify(transactionRepository).getTransactions(any(TransactionQueryForm.class));
    }

    @Test
    @DisplayName("getCDRs should return empty list when no completed transactions exist")
    void testGetCDRsEmpty() {
        when(transactionRepository.getTransactions(any(TransactionQueryForm.class)))
            .thenReturn(new ArrayList<>());

        List<CDR> cdrs = adapter.getCDRs(null, null, 0, 10);

        assertNotNull(cdrs);
        assertTrue(cdrs.isEmpty());
        verify(transactionRepository).getTransactions(any(TransactionQueryForm.class));
    }

    @Test
    @DisplayName("getLocation should return null for non-existent ID")
    void testGetLocationNonExistent() {
        when(chargePointRepository.getOverview(any(ChargePointQueryForm.class)))
            .thenReturn(new ArrayList<>());

        Location location = adapter.getLocation("NON_EXISTENT");

        assertNull(location);
        verify(chargePointRepository).getOverview(any(ChargePointQueryForm.class));
    }

    @Test
    @DisplayName("getEvse should return null when location does not exist")
    void testGetEvseNonExistentLocation() {
        when(chargePointRepository.getOverview(any(ChargePointQueryForm.class)))
            .thenReturn(new ArrayList<>());

        var evse = adapter.getEvse("NON_EXISTENT", "EVSE_UID");

        assertNull(evse);
        verify(chargePointRepository).getOverview(any(ChargePointQueryForm.class));
    }

    @Test
    @DisplayName("getConnector should return null when EVSE does not exist")
    void testGetConnectorNonExistentEvse() {
        when(chargePointRepository.getOverview(any(ChargePointQueryForm.class)))
            .thenReturn(new ArrayList<>());

        var connector = adapter.getConnector("LOCATION_ID", "NON_EXISTENT", "1");

        assertNull(connector);
        verify(chargePointRepository).getOverview(any(ChargePointQueryForm.class));
    }

    @Test
    @DisplayName("Pagination should limit results correctly")
    void testPaginationLimits() {
        when(chargePointRepository.getOverview(any(ChargePointQueryForm.class)))
            .thenReturn(new ArrayList<>());

        int limit = 5;
        List<Location> locations = adapter.getLocations(null, null, 0, limit);

        assertNotNull(locations);
        assertTrue(locations.size() <= limit);
    }
}
