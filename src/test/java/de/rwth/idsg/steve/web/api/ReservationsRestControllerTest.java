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

import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.service.OcppOperationsService;
import de.rwth.idsg.steve.web.dto.RestCallback;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ReserveNowParams;
import ocpp.cp._2015._10.CancelReservationStatus;
import ocpp.cp._2015._10.ReservationStatus;
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

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.08.2026
 */
@ExtendWith(MockitoExtension.class)
public class ReservationsRestControllerTest extends AbstractControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private OcppOperationsService operationsService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReservationsRestController(reservationRepository, operationsService))
            .setControllerAdvice(new ApiControllerAdvice())
            .setMessageConverters(new JacksonJsonHttpMessageConverter(objectMapper))
            .alwaysExpect(content().contentType(CONTENT_TYPE))
            .build();
    }

    @Test
    @DisplayName("GET all: Test with empty results, expected 200")
    public void test1() throws Exception {
        when(reservationRepository.getReservations(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/reservations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET all: Test with one result, expected 200")
    public void test2() throws Exception {
        Reservation res = Reservation.builder()
            .id(42)
            .chargeBoxId("CP001")
            .connectorId(1)
            .ocppIdTag("TAG-001")
            .status("ACCEPTED")
            .build();

        when(reservationRepository.getReservations(any())).thenReturn(List.of(res));

        mockMvc.perform(get("/api/v1/reservations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(42))
            .andExpect(jsonPath("$[0].chargeBoxId").value("CP001"))
            .andExpect(jsonPath("$[0].connectorId").value(1))
            .andExpect(jsonPath("$[0].ocppIdTag").value("TAG-001"))
            .andExpect(jsonPath("$[0].status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("GET one: Entity found, expected 200")
    public void test3() throws Exception {
        Reservation res = Reservation.builder()
            .id(42)
            .chargeBoxId("CP001")
            .connectorId(1)
            .ocppIdTag("TAG-001")
            .status("ACCEPTED")
            .build();

        when(reservationRepository.getReservations(any())).thenReturn(List.of(res));

        mockMvc.perform(get("/api/v1/reservations/42"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.chargeBoxId").value("CP001"));
    }

    @Test
    @DisplayName("GET one: Entity not found, expected 404")
    public void test4() throws Exception {
        when(reservationRepository.getReservations(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/reservations/999"))
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("POST reserve-now: Valid params, expected 200")
    public void test5() throws Exception {
        ReserveNowParams params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of("CP001"));
        params.setConnectorId(1);
        params.setIdTag("TAG-001");
        params.setExpiry(DateTime.now().plusHours(2));

        RestCallback<ReservationStatus> callback = new RestCallback<>(Duration.ofSeconds(1), new CountDownLatch(0));
        callback.setTaskId(101);
        when(operationsService.reserveNow(any())).thenReturn(callback);

        mockMvc.perform(
                post("/api/v1/reservations/reserve-now")
                    .content(objectMapper.writeValueAsString(params))
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value(101));
    }

    @Test
    @DisplayName("POST reserve-now: Expiry in past, expected 400")
    public void test6() throws Exception {
        ReserveNowParams params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of("CP001"));
        params.setConnectorId(1);
        params.setIdTag("TAG-001");
        params.setExpiry(DateTime.now().minusHours(2));

        mockMvc.perform(
                post("/api/v1/reservations/reserve-now")
                    .content(objectMapper.writeValueAsString(params))
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());

        verifyNoInteractions(operationsService);
    }

    @Test
    @DisplayName("POST cancel: Valid params, expected 200")
    public void test7() throws Exception {
        CancelReservationParams params = new CancelReservationParams();
        params.setChargeBoxIdList(List.of("CP001"));
        params.setReservationId(42);

        RestCallback<CancelReservationStatus> callback = new RestCallback<>(Duration.ofSeconds(1), new CountDownLatch(0));
        callback.setTaskId(102);
        when(operationsService.cancelReservation(any())).thenReturn(callback);

        mockMvc.perform(
                post("/api/v1/reservations/cancel")
                    .content(objectMapper.writeValueAsString(params))
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value(102));
    }

    @Test
    @DisplayName("POST cancel: Missing reservationId, expected 400")
    public void test8() throws Exception {
        CancelReservationParams params = new CancelReservationParams();
        params.setChargeBoxIdList(List.of("CP001"));
        params.setReservationId(null);

        mockMvc.perform(
                post("/api/v1/reservations/cancel")
                    .content(objectMapper.writeValueAsString(params))
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());

        verifyNoInteractions(operationsService);
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
