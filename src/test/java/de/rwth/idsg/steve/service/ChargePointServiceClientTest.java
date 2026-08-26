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
import de.rwth.idsg.steve.ocpp.ChargePointServiceInvokerImpl;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.CertificateRepository;
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.repository.EventRepository;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChargePointServiceClientTest {

    @Mock
    private ChargingProfileRepository chargingProfileRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private OcppTagService ocppTagService;
    @Mock
    private ChargePointService chargePointService;
    @Mock
    private CertificateRepository certificateRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SteveProperties steveProperties;
    @Mock
    private TaskExecutor taskExecutor;
    @Mock
    private TaskStore taskStore;
    @Mock
    private ChargePointServiceInvokerImpl invoker;

    @InjectMocks
    private ChargePointServiceClient client;

    @Test
    public void clearCache_storesTaskBeforeSchedulingIt() {
        var params = new MultipleChargePointSelect();
        params.setChargePointSelectList(List.of(new ChargePointSelect(OcppProtocol.V_16_JSON, "station")));
        when(taskStore.add(any(CommunicationTask.class))).thenReturn(42);

        int taskId = client.clearCache(params);

        var taskCaptor = ArgumentCaptor.forClass(CommunicationTask.class);
        InOrder order = inOrder(taskStore, taskExecutor);
        order.verify(taskStore).add(taskCaptor.capture());
        order.verify(taskExecutor).execute(any(Runnable.class));
        assertEquals(42, taskId);
    }
}
