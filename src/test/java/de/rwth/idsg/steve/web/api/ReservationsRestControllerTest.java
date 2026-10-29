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
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author huangkongsheng
 * @since 19.10.2026
 */
@ExtendWith(MockitoExtension.class)
public class ReservationsRestControllerTest extends AbstractControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private ReservationRepository reservationRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReservationsRestController(reservationRepository))
            .setControllerAdvice(new ApiControllerAdvice())
            .setMessageConverters(new JacksonJsonHttpMessageConverter(objectMapper))
            .alwaysExpect(content().contentType(CONTENT_TYPE))
            .build();
    }

    @Test
    @DisplayName("GET all: Test with empty results, expected 200")
    public void test1() throws Exception {
        // when
        when(reservationRepository.getReservations(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/reservations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET all: Test with one result, expected 200")
    public void test2() throws Exception {
        // given
        Reservation result = Reservation.builder()
            .id(42)
            .ocppTagPk(7)
            .chargeBoxPk(96)
            .connectorId(1)
            .ocppIdTag("TEST-001")
            .chargeBoxId("CP001")
            .expiryDatetime("2026-07-11T18:00:00Z")
            .status("Active")
            .build();

        // when
        when(reservationRepository.getReservations(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(get("/api/v1/reservations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value("42"))
            .andExpect(jsonPath("$[0].chargeBoxId").value("CP001"))
            .andExpect(jsonPath("$[0].ocppIdTag").value("TEST-001"))
            .andExpect(jsonPath("$[0].status").value("Active"));
    }

    @Test
    @DisplayName("GET all: Downstream bean throws exception, expected 500")
    public void test3() throws Exception {
        // when
        when(reservationRepository.getReservations(any())).thenThrow(new RuntimeException("failed"));

        // then
        mockMvc.perform(get("/api/v1/reservations"))
            .andExpect(status().isInternalServerError())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: Wrong path variable format makes validation fail, expected 400")
    public void test4() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/not-an-integer"))
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: Entity not found, expected 404")
    public void test5() throws Exception {
        // when
        when(reservationRepository.getReservations(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/reservations/42"))
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: Entity found, and the path variable is used as the filter, expected 200")
    public void test6() throws Exception {
        // given
        Reservation result = Reservation.builder().id(42).chargeBoxId("CP001").build();

        // when
        when(reservationRepository.getReservations(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(get("/api/v1/reservations/42"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("42"));

        ArgumentCaptor<ReservationQueryForm> captor = ArgumentCaptor.forClass(ReservationQueryForm.class);
        org.mockito.Mockito.verify(reservationRepository).getReservations(captor.capture());
        assertEquals(List.of(42), captor.getValue().getReservationId());
    }

    @Test
    @DisplayName("GET one: Lookup by primary key ignores the active-period default, expected 200")
    public void test7() throws Exception {
        // given
        Reservation result = Reservation.builder().id(42).chargeBoxId("CP001").build();

        // when
        when(reservationRepository.getReservations(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(get("/api/v1/reservations/42"))
            .andExpect(status().isOk());

        ArgumentCaptor<ReservationQueryForm> captor = ArgumentCaptor.forClass(ReservationQueryForm.class);
        org.mockito.Mockito.verify(reservationRepository).getReservations(captor.capture());
        assertEquals(ReservationQueryForm.QueryPeriodType.ALL, captor.getValue().getPeriodType());
    }

    @Test
    @DisplayName("GET all: Blank chargeBoxId filter is rejected, expected 400")
    public void test8() throws Exception {
        // @Valid must activate the form constraints; a blank id would otherwise
        // reach the repository IN predicates
        mockMvc.perform(get("/api/v1/reservations")
                .param("chargeBoxId", " "))
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET all: Negative reservationId filter is rejected, expected 400")
    public void test9() throws Exception {
        mockMvc.perform(get("/api/v1/reservations")
                .param("reservationId", "-1"))
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET all: Connector filter is passed to the repository, expected 200")
    public void test10() throws Exception {
        // when
        when(reservationRepository.getReservations(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/reservations")
                .param("connectorId", "2"))
            .andExpect(status().isOk());

        ArgumentCaptor<ReservationQueryForm> captor = ArgumentCaptor.forClass(ReservationQueryForm.class);
        org.mockito.Mockito.verify(reservationRepository).getReservations(captor.capture());
        assertEquals(2, captor.getValue().getConnectorId());
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
