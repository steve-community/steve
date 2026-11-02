/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStore;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStoreHolder;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.GenericRepository;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.utils.ConnectorStatusFilter.Strategy;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChargePointServiceConnectorStatusTest {

    private static final DateTime DT = DateTime.parse("2025-12-10T12:00:00.000Z");

    @Mock private ApplicationEventPublisher applicationEventPublisher;
    @Mock private ChargePointRepository chargePointRepository;
    @Mock private GenericRepository genericRepository;
    @Mock private SteveProperties steveProperties;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SessionContextStoreHolder sessionContextStoreHolder;
    @Mock private SessionContextStore sessionContextStore;

    private ChargePointService service;

    // connector 0 is more recent and says Faulted, connector 1 is older and says Available
    private final ConnectorStatus cs0 = ConnectorStatus.builder().statusTimestamp(DT.plusHours(1)).chargeBoxId("foo").status("Faulted").connectorId(0).build();
    private final ConnectorStatus cs1 = ConnectorStatus.builder().statusTimestamp(DT).chargeBoxId("foo").status("Available").connectorId(1).build();

    @BeforeEach
    public void setup() {
        service = new ChargePointService(applicationEventPublisher, chargePointRepository, genericRepository,
            steveProperties, passwordEncoder, sessionContextStoreHolder);
        when(chargePointRepository.getChargePointConnectorStatus(any())).thenReturn(List.of(cs0, cs1));
        when(sessionContextStoreHolder.getOrCreate(any())).thenReturn(sessionContextStore);
        when(sessionContextStore.getChargeBoxIdList()).thenReturn(List.of());
    }

    @Test
    public void defaultStrategy_isReplaceWithStation() {
        Assertions.assertEquals(List.of(cs0), service.getChargePointConnectorStatus(form(null)));
    }

    @Test
    public void nullStrategy_fallsBackToReplaceWithStation() {
        var form = form(null);
        form.setConnectorZeroStrategy(null);
        Assertions.assertEquals(List.of(cs0), service.getChargePointConnectorStatus(form));
    }

    @Test
    public void inheritFromStation() {
        var result = service.getChargePointConnectorStatus(form(Strategy.InheritFromStation));
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.get(0).getConnectorId());
        Assertions.assertEquals("Faulted", result.get(0).getStatus());
    }

    @Test
    public void asReported() {
        Assertions.assertEquals(List.of(cs0, cs1), service.getChargePointConnectorStatus(form(Strategy.AsReported)));
    }

    private static ConnectorStatusForm form(Strategy strategy) {
        var form = new ConnectorStatusForm();
        form.setChargeBoxId("foo");
        if (strategy != null) {
            form.setConnectorZeroStrategy(strategy);
        }
        return form;
    }
}
