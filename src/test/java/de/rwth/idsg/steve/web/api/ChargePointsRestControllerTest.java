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

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppSecurityProfile;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointRegistration;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.utils.JsonUtils;
import de.rwth.idsg.steve.web.dto.ChargePointFormForUpdate;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author huangkongsheng
 * @since 19.10.2026
 */
@ExtendWith(MockitoExtension.class)
public class ChargePointsRestControllerTest extends AbstractControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private ChargePointService chargePointService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ChargePointsRestController(chargePointService))
            .setControllerAdvice(new ApiControllerAdvice())
            .setMessageConverters(new JacksonJsonHttpMessageConverter(objectMapper))
            .setValidator(validatorWithChargePointService(chargePointService))
            .alwaysExpect(content().contentType(CONTENT_TYPE))
            .build();
    }

    @Test
    @DisplayName("GET all: Test with empty results, expected 200")
    public void test1() throws Exception {
        // when
        when(chargePointService.getOverview(any(), any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/chargeBoxes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET all: Test with one result, expected 200")
    public void test2() throws Exception {
        // given
        ChargePoint.Overview result = ChargePoint.Overview.builder()
            .chargeBoxPk(96)
            .chargeBoxId("CP001")
            .ocppProtocol("OCPP_16_JSON")
            .build();

        // when
        when(chargePointService.getOverview(any(), any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(get("/api/v1/chargeBoxes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].chargeBoxPk").value("96"))
            .andExpect(jsonPath("$[0].chargeBoxId").value("CP001"));
    }

    @Test
    @DisplayName("GET one: Entity not found, expected 404")
    public void test3() throws Exception {
        // when
        when(chargePointService.getOverview(any(), any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/chargeBoxes/12"))
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: One entity found, expected 200")
    public void test4() throws Exception {
        // given
        ChargePoint.Overview result = ChargePoint.Overview.builder().chargeBoxPk(12).chargeBoxId("CP001").build();

        // when
        when(chargePointService.getOverview(any(), any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(get("/api/v1/chargeBoxes/12"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.chargeBoxPk").value("12"));
    }

    @Test
    @DisplayName("POST: Entity created, expected 201")
    public void test5() throws Exception {
        // given
        int chargeBoxPk = 123;

        ChargePoint.Overview result = ChargePoint.Overview.builder()
            .chargeBoxPk(chargeBoxPk)
            .chargeBoxId("CP-NEW")
            .build();

        // when
        when(chargePointService.getOverview(any(), any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(
                post("/api/v1/chargeBoxes")
                    .content("""
                        {"chargeBoxId": "CP-NEW", "registrationStatus": "Accepted",
                         "insertConnectorStatusAfterTransactionMsg": true}
                        """)
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.chargeBoxPk").value("123"))
            .andExpect(jsonPath("$.chargeBoxId").value("CP-NEW"));
    }

    @Test
    @DisplayName("PUT: Entity updated, expected 200")
    public void test6() throws Exception {
        // given
        int chargeBoxPk = 123;

        ChargePoint.Overview result = ChargePoint.Overview.builder()
            .chargeBoxPk(chargeBoxPk)
            .chargeBoxId("CP001")
            .build();

        // when
        when(chargePointService.getOverview(any(), any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(
                put("/api/v1/chargeBoxes/" + chargeBoxPk)
                    .content("""
                        {"chargeBoxId": "CP001", "registrationStatus": "Accepted",
                         "insertConnectorStatusAfterTransactionMsg": true}
                        """)
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isOk());

        verify(chargePointService).updateChargePoint(any(ChargePointFormForUpdate.class));
    }

    @Test
    @DisplayName("DELETE: Entity deleted, expected 200")
    public void test7() throws Exception {
        // given
        int chargeBoxPk = 123;

        ChargePoint.Overview result = ChargePoint.Overview.builder()
            .chargeBoxPk(chargeBoxPk)
            .chargeBoxId("CP001")
            .build();

        // when
        when(chargePointService.getOverview(any(), any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(delete("/api/v1/chargeBoxes/" + chargeBoxPk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.chargeBoxPk").value("123"));

        verify(chargePointService).deleteChargePoint(eq(chargeBoxPk));
    }

    @Test
    @DisplayName("DELETE: Entity not found, expected 404")
    public void test8() throws Exception {
        // when
        when(chargePointService.getOverview(any(), any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(delete("/api/v1/chargeBoxes/123"))
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());

        verify(chargePointService, times(0)).deleteChargePoint(123);
    }

    @Test
    @DisplayName("GET connectors: Connected station with two connectors, expected 200")
    public void test9() throws Exception {
        // given
        ChargePoint.Overview overview = ChargePoint.Overview.builder()
            .chargeBoxPk(96)
            .chargeBoxId("CP001")
            .build();

        List<ConnectorStatus> statuses = List.of(
            ConnectorStatus.builder()
                .chargeBoxPk(96)
                .chargeBoxId("CP001")
                .connectorId(1)
                .ocppProtocol(OcppProtocol.V_16_JSON)
                .status("Charging")
                .errorCode("NoError")
                .statusTimestamp(DateTime.parse("2026-10-19T08:00:00.000Z"))
                .jsonAndDisconnected(false)
                .build(),
            ConnectorStatus.builder()
                .chargeBoxPk(96)
                .chargeBoxId("CP001")
                .connectorId(2)
                .ocppProtocol(OcppProtocol.V_16_JSON)
                .status("Available")
                .errorCode("NoError")
                .statusTimestamp(DateTime.parse("2026-10-19T08:00:00.000Z"))
                .jsonAndDisconnected(false)
                .build()
        );

        when(chargePointService.getOverview(any(), any())).thenReturn(List.of(overview));
        when(chargePointService.getChargePointConnectorStatus(any(ConnectorStatusForm.class))).thenReturn(statuses);

        // then
        mockMvc.perform(get("/api/v1/chargeBoxes/96/connectors"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].connectorId").value("1"))
            .andExpect(jsonPath("$[0].status").value("Charging"))
            .andExpect(jsonPath("$[0].transportConnected").value("true"))
            .andExpect(jsonPath("$[1].connectorId").value("2"));
    }

    @Test
    @DisplayName("GET connection: Disconnected JSON station, expected 200")
    public void test10() throws Exception {
        // given
        ChargePoint.Overview overview = ChargePoint.Overview.builder()
            .chargeBoxPk(96)
            .chargeBoxId("CP001")
            .ocppProtocol("OCPP16J")
            .lastHeartbeatTimestampDT(DateTime.parse("2026-10-19T08:00:00.000Z"))
            .build();

        List<ConnectorStatus> statuses = List.of(
            ConnectorStatus.builder()
                .chargeBoxPk(96)
                .chargeBoxId("CP001")
                .connectorId(1)
                .ocppProtocol(OcppProtocol.V_16_JSON)
                .status("Available")
                .errorCode("NoError")
                .statusTimestamp(DateTime.parse("2026-10-19T08:00:00.000Z"))
                .jsonAndDisconnected(true)
                .build()
        );

        when(chargePointService.getOverview(any(), any())).thenReturn(List.of(overview));
        when(chargePointService.getChargePointConnectorStatus(any(ConnectorStatusForm.class))).thenReturn(statuses);

        // then
        mockMvc.perform(get("/api/v1/chargeBoxes/96/connection"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.chargeBoxId").value("CP001"))
            .andExpect(jsonPath("$.connected").value("false"))
            .andExpect(jsonPath("$.ocppProtocol").value("OCPP16J"))
            .andExpect(jsonPath("$.lastHeartbeatTimestamp").value("2026-10-19T08:00:00.000Z"))
            .andExpect(jsonPath("$.connectors", hasSize(1)));
    }

    @Test
    @DisplayName("GET connection: Station without connector statuses, expected 200")
    public void test11() throws Exception {
        // given
        ChargePoint.Overview overview = ChargePoint.Overview.builder()
            .chargeBoxPk(96)
            .chargeBoxId("CP001")
            .ocppProtocol("OCPP16J")
            .lastHeartbeatTimestampDT(DateTime.parse("2026-10-19T08:00:00.000Z"))
            .build();

        when(chargePointService.getOverview(any(), any())).thenReturn(List.of(overview));
        when(chargePointService.getChargePointConnectorStatus(any(ConnectorStatusForm.class)))
            .thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/chargeBoxes/96/connection"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.chargeBoxId").value("CP001"))
            .andExpect(jsonPath("$.connected").value("false"))
            .andExpect(jsonPath("$.connectors", hasSize(0)));
    }

    @Test
    @DisplayName("POST: Duplicate chargeBoxId, expected 422")
    public void test12() throws Exception {
        // given
        var registration = new ChargePointRegistration(96, "CP-DUP", "Accepted",
            OcppSecurityProfile.Profile_0, null, JsonUtils.toObjectNode(null), null);
        when(chargePointService.getRegistrationDirect("CP-DUP")).thenReturn(Optional.of(registration));

        // then
        mockMvc.perform(
                post("/api/v1/chargeBoxes")
                    .content("""
                        {"chargeBoxId": "CP-DUP", "registrationStatus": "Accepted",
                         "insertConnectorStatusAfterTransactionMsg": true}
                        """)
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpectAll(errorJsonMatchers());

        verify(chargePointService, times(0)).addChargePoint(any());
    }

    @Test
    @DisplayName("PUT: Body chargeBoxId does not match path, expected 400")
    public void test13() throws Exception {
        // given
        ChargePoint.Overview result = ChargePoint.Overview.builder()
            .chargeBoxPk(123)
            .chargeBoxId("CP001")
            .build();

        when(chargePointService.getOverview(any(), any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(
                put("/api/v1/chargeBoxes/123")
                    .content("""
                        {"chargeBoxId": "CP-OTHER", "registrationStatus": "Accepted",
                         "insertConnectorStatusAfterTransactionMsg": true}
                        """)
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());

        verify(chargePointService, times(0)).updateChargePoint(any());
    }

    @Test
    @DisplayName("GET one connector: Existing connector, expected 200")
    public void test14() throws Exception {
        // given
        ChargePoint.Overview overview = ChargePoint.Overview.builder()
            .chargeBoxPk(96)
            .chargeBoxId("CP001")
            .build();

        List<ConnectorStatus> statuses = List.of(
            ConnectorStatus.builder()
                .chargeBoxPk(96)
                .chargeBoxId("CP001")
                .connectorId(1)
                .ocppProtocol(OcppProtocol.V_16_JSON)
                .status("Charging")
                .errorCode("NoError")
                .statusTimestamp(DateTime.parse("2026-10-19T08:00:00.000Z"))
                .jsonAndDisconnected(false)
                .build()
        );

        when(chargePointService.getOverview(any(), any())).thenReturn(List.of(overview));
        when(chargePointService.getChargePointConnectorStatus(any(ConnectorStatusForm.class))).thenReturn(statuses);

        // then
        mockMvc.perform(get("/api/v1/chargeBoxes/96/connectors/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.connectorId").value("1"))
            .andExpect(jsonPath("$.status").value("Charging"))
            .andExpect(jsonPath("$.statusTimestamp").value("2026-10-19T08:00:00.000Z"));
    }

    @Test
    @DisplayName("GET one connector: Unknown connector, expected 404")
    public void test15() throws Exception {
        // given
        ChargePoint.Overview overview = ChargePoint.Overview.builder()
            .chargeBoxPk(96)
            .chargeBoxId("CP001")
            .build();

        when(chargePointService.getOverview(any(), any())).thenReturn(List.of(overview));
        when(chargePointService.getChargePointConnectorStatus(any(ConnectorStatusForm.class)))
            .thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/chargeBoxes/96/connectors/7"))
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET connectors: Connector zero is not a physical connector, expected 200")
    public void test16() throws Exception {
        // given
        ChargePoint.Overview overview = ChargePoint.Overview.builder()
            .chargeBoxPk(96)
            .chargeBoxId("CP001")
            .build();

        List<ConnectorStatus> statuses = List.of(
            ConnectorStatus.builder()
                .chargeBoxPk(96)
                .chargeBoxId("CP001")
                .connectorId(0)
                .ocppProtocol(OcppProtocol.V_16_JSON)
                .status("Available")
                .errorCode("NoError")
                .statusTimestamp(DateTime.parse("2026-10-19T08:00:00.000Z"))
                .jsonAndDisconnected(false)
                .build(),
            ConnectorStatus.builder()
                .chargeBoxPk(96)
                .chargeBoxId("CP001")
                .connectorId(1)
                .ocppProtocol(OcppProtocol.V_16_JSON)
                .status("Charging")
                .errorCode("NoError")
                .statusTimestamp(DateTime.parse("2026-10-19T08:00:00.000Z"))
                .jsonAndDisconnected(false)
                .build()
        );

        when(chargePointService.getOverview(any(), any())).thenReturn(List.of(overview));
        when(chargePointService.getChargePointConnectorStatus(any(ConnectorStatusForm.class))).thenReturn(statuses);

        // then
        mockMvc.perform(get("/api/v1/chargeBoxes/96/connectors"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].connectorId").value("1"));
    }

    private static ResultMatcher[] errorJsonMatchers() {
        return new ResultMatcher[]{
            jsonPath("$.timestamp").exists(),
            jsonPath("$.status").exists(),
            jsonPath("$.error").exists(),
            jsonPath("$.message").exists(),
            jsonPath("$.path").exists()
        };
    }
}
