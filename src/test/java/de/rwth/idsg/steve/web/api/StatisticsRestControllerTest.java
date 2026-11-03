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
package de.rwth.idsg.steve.web.api;

import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.utils.ConnectorStatusFilter.Strategy;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StatisticsRestControllerTest extends AbstractControllerTest {

    @Mock
    private ChargePointService chargePointService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new StatisticsRestController(chargePointService))
            .setControllerAdvice(new ApiControllerAdvice())
            .setMessageConverters(new JacksonJsonHttpMessageConverter(objectMapper))
            .build();
    }

    @Test
    public void connectorZeroStrategy_default() throws Exception {
        assertEquals(Strategy.ReplaceWithStation, performAndCapture("/api/v1/statistics/status").getConnectorZeroStrategy());
    }

    @Test
    public void connectorZeroStrategy_eachValue() throws Exception {
        for (Strategy strategy : Strategy.values()) {
            var form = performAndCapture("/api/v1/statistics/status?connectorZeroStrategy=" + strategy.name());
            assertEquals(strategy, form.getConnectorZeroStrategy());
        }
    }

    @Test
    public void connectorZeroStrategy_invalid() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/status?connectorZeroStrategy=Nope"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(chargePointService);
    }

    private ConnectorStatusForm performAndCapture(String url) throws Exception {
        when(chargePointService.getChargePointConnectorStatus(any())).thenReturn(Collections.emptyList());
        mockMvc.perform(get(url)).andExpect(status().isOk());

        var captor = ArgumentCaptor.forClass(ConnectorStatusForm.class);
        verify(chargePointService, atLeastOnce()).getChargePointConnectorStatus(captor.capture());
        return captor.getValue();
    }
}
