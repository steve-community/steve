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

import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.web.dto.ChargingProfileForm;
import de.rwth.idsg.steve.web.dto.ChargingProfileQueryForm;
import ocpp.cp._2015._10.ChargingProfileKindType;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingRateUnitType;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
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
public class ChargingProfilesRestControllerTest extends AbstractControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private ChargingProfileRepository chargingProfileRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ChargingProfilesRestController(chargingProfileRepository))
            .setControllerAdvice(new ApiControllerAdvice())
            .setMessageConverters(new JacksonJsonHttpMessageConverter(objectMapper))
            .alwaysExpect(content().contentType(CONTENT_TYPE))
            .build();
    }

    /**
     * A form that passes @Valid, mirroring how the existing API tests serialize
     * the form object instead of hand-writing JSON (so the enum wire format
     * stays correct).
     */
    private static ChargingProfileForm aValidForm() {
        ChargingProfileForm form = new ChargingProfileForm();
        form.setDescription("Night charging");
        form.setStackLevel(1);
        form.setChargingProfilePurpose(ChargingProfilePurposeType.TX_PROFILE);
        form.setChargingProfileKind(ChargingProfileKindType.ABSOLUTE);
        form.setChargingRateUnit(ChargingRateUnitType.A);

        ChargingProfileForm.SchedulePeriod period = new ChargingProfileForm.SchedulePeriod();
        period.setStartPeriodInSeconds(0);
        period.setPowerLimit(new BigDecimal("16"));
        period.setNumberPhases(3);
        form.setSchedulePeriods(List.of(period));
        return form;
    }

    @Test
    @DisplayName("GET all: Test with empty results, expected 200")
    public void test1() throws Exception {
        // when
        when(chargingProfileRepository.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/chargingProfiles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET all: Test with one result, expected 200")
    public void test2() throws Exception {
        // given
        ChargingProfile.Overview result = ChargingProfile.Overview.builder()
            .chargingProfilePk(42)
            .stackLevel(1)
            .description("Night charging")
            .profilePurpose("TxProfile")
            .profileKind("Absolute")
            .build();

        // when
        when(chargingProfileRepository.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(get("/api/v1/chargingProfiles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].chargingProfilePk").value("42"))
            .andExpect(jsonPath("$[0].stackLevel").value("1"))
            .andExpect(jsonPath("$[0].profilePurpose").value("TxProfile"));
    }

    @Test
    @DisplayName("GET all: with station/connector filters, expected 200")
    public void test10() throws Exception {
        // given
        ArgumentCaptor<ChargingProfileQueryForm> captor = ArgumentCaptor.forClass(ChargingProfileQueryForm.class);

        // when
        when(chargingProfileRepository.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/chargingProfiles")
                .param("chargeBoxId", "CP001")
                .param("connectorId", "2")
                .param("active", "true"))
            .andExpect(status().isOk());

        verify(chargingProfileRepository).getOverview(captor.capture());
        assertEquals("CP001", captor.getValue().getChargeBoxId());
        assertEquals(2, captor.getValue().getConnectorId());
        assertTrue(captor.getValue().getActive());
    }

    @Test
    @DisplayName("GET all: OCPP value form of enum filter is accepted, expected 200")
    public void test11() throws Exception {
        // resources return OCPP values ("TxProfile"), so filtering by them must work
        ArgumentCaptor<ChargingProfileQueryForm> captor = ArgumentCaptor.forClass(ChargingProfileQueryForm.class);

        when(chargingProfileRepository.getOverview(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/chargingProfiles")
                .param("profilePurpose", "TxProfile"))
            .andExpect(status().isOk());

        verify(chargingProfileRepository).getOverview(captor.capture());
        assertEquals(ChargingProfilePurposeType.TX_PROFILE, captor.getValue().getProfilePurpose());
    }

    @Test
    @DisplayName("POST: OCPP value form of enums in body is accepted, expected 201")
    public void test12() throws Exception {
        // given
        int chargingProfilePk = 123;

        ChargingProfile.Overview result = ChargingProfile.Overview.builder()
            .chargingProfilePk(chargingProfilePk)
            .description("Night charging")
            .build();

        ArgumentCaptor<ChargingProfileForm> captor = ArgumentCaptor.forClass(ChargingProfileForm.class);

        // when
        when(chargingProfileRepository.add(any(ChargingProfileForm.class))).thenReturn(chargingProfilePk);
        when(chargingProfileRepository.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(
                post("/api/v1/chargingProfiles")
                    .content("""
                        {"description": "Night charging", "stackLevel": 1,
                         "chargingProfilePurpose": "TxProfile", "chargingProfileKind": "Absolute",
                         "chargingRateUnit": "A",
                         "schedulePeriods": [{"startPeriodInSeconds": 0, "powerLimit": 16, "numberPhases": 3}]}
                        """)
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isCreated());

        verify(chargingProfileRepository).add(captor.capture());
        assertEquals(ChargingProfilePurposeType.TX_PROFILE, captor.getValue().getChargingProfilePurpose());
    }

    @Test
    @DisplayName("POST: Malformed json, expected 400")
    public void test13() throws Exception {
        mockMvc.perform(
                post("/api/v1/chargingProfiles")
                    .content("{not-json")
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: Entity not found, expected 404")
    public void test3() throws Exception {
        // when
        when(chargingProfileRepository.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/chargingProfiles/42"))
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: One entity found, expected 200")
    public void test4() throws Exception {
        // given
        ChargingProfile.Overview result = ChargingProfile.Overview.builder()
            .chargingProfilePk(42)
            .description("Night charging")
            .validFrom(DateTime.parse("2026-07-11T16:00:00.000Z"))
            .build();

        // when
        when(chargingProfileRepository.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(get("/api/v1/chargingProfiles/42"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.chargingProfilePk").value("42"))
            .andExpect(jsonPath("$.validFrom").value("2026-07-11T16:00:00.000Z"));
    }

    @Test
    @DisplayName("POST: Entity created, expected 201")
    public void test5() throws Exception {
        // given
        int chargingProfilePk = 123;

        ChargingProfile.Overview result = ChargingProfile.Overview.builder()
            .chargingProfilePk(chargingProfilePk)
            .description("Night charging")
            .build();

        // when
        when(chargingProfileRepository.add(any(ChargingProfileForm.class))).thenReturn(chargingProfilePk);
        when(chargingProfileRepository.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(
                post("/api/v1/chargingProfiles")
                    .content(objectMapper.writeValueAsString(aValidForm()))
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.chargingProfilePk").value("123"))
            .andExpect(jsonPath("$.description").value("Night charging"));
    }

    @Test
    @DisplayName("POST: Downstream bean throws exception, expected 500")
    public void test6() throws Exception {
        // when
        when(chargingProfileRepository.add(any(ChargingProfileForm.class))).thenThrow(new RuntimeException("failed"));

        // then
        mockMvc.perform(
                post("/api/v1/chargingProfiles")
                    .content(objectMapper.writeValueAsString(aValidForm()))
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isInternalServerError())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("PUT: Entity updated, expected 200")
    public void test7() throws Exception {
        // given
        int chargingProfilePk = 123;

        ChargingProfile.Overview result = ChargingProfile.Overview.builder()
            .chargingProfilePk(chargingProfilePk)
            .description("Updated")
            .build();

        // when
        when(chargingProfileRepository.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(
                put("/api/v1/chargingProfiles/" + chargingProfilePk)
                    .content(objectMapper.writeValueAsString(aValidForm()))
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isOk());

        verify(chargingProfileRepository).update(any(ChargingProfileForm.class));
    }

    @Test
    @DisplayName("DELETE: Entity deleted, expected 200")
    public void test8() throws Exception {
        // given
        int chargingProfilePk = 123;

        ChargingProfile.Overview result = ChargingProfile.Overview.builder()
            .chargingProfilePk(chargingProfilePk)
            .description("Night charging")
            .build();

        // when
        when(chargingProfileRepository.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(delete("/api/v1/chargingProfiles/" + chargingProfilePk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.chargingProfilePk").value("123"));

        verify(chargingProfileRepository).delete(eq(chargingProfilePk));
    }

    @Test
    @DisplayName("DELETE: Entity not found, expected 404")
    public void test9() throws Exception {
        // when
        when(chargingProfileRepository.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(delete("/api/v1/chargingProfiles/123"))
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());

        verify(chargingProfileRepository, times(0)).delete(123);
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
