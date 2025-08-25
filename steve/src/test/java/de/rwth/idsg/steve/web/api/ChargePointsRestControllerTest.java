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
package de.rwth.idsg.steve.web.api;

import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.service.ChargePointsService;
import de.rwth.idsg.steve.web.api.exception.NotFoundException;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ChargePointsRestControllerTest extends AbstractControllerTest {

    @Mock
    private ChargePointsService chargePointsService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = buildMockMvc(MockMvcBuilders.standaloneSetup(new ChargePointsRestController(chargePointsService)));
    }

    @Test
    @DisplayName("GET all: Test with empty results, expected 200")
    public void testGet_withEmptyList() throws Exception {
        when(chargePointsService.getOverview(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/chargeboxes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET all: Test with one result, expected 200")
    public void testGet_withOneResult() throws Exception {
        List<ChargePoint.Overview> results = List.of(ChargePoint.Overview.builder().chargeBoxPk(1).build());
        when(chargePointsService.getOverview(any())).thenReturn(results);

        mockMvc.perform(get("/api/v1/chargeboxes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].chargeBoxPk").value("1"));
    }

    @Test
    @DisplayName("GET one: Entity not found, expected 404")
    public void testGetOne_notFound() throws Exception {
        when(chargePointsService.getDetails(anyInt())).thenThrow(new NotFoundException(""));

        mockMvc.perform(get("/api/v1/chargeboxes/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET one: One entity found, expected 200")
    public void testGetOne_found() throws Exception {
        ChargePoint.Details result = createDetails(1, null);
        when(chargePointsService.getDetails(1)).thenReturn(result);

        mockMvc.perform(get("/api/v1/chargeboxes/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.chargeBoxPk").value("1"));
    }

    private static ChargePoint.Details createDetails(Integer pk, String chargeBoxId) {
        ChargeBoxRecord cbr = new ChargeBoxRecord();
        cbr.setChargeBoxPk(pk);
        cbr.setChargeBoxId(chargeBoxId);
        return new ChargePoint.Details(cbr, null);
    }

    @Test
    @DisplayName("POST: Entity created, expected 201")
    public void testPost() throws Exception {
        ChargePointForm form = new ChargePointForm();
        form.setChargeBoxId("test-cb");
        form.setRegistrationStatus("updated");
        form.setInsertConnectorStatusAfterTransactionMsg(true);

        ChargePoint.Details result = createDetails(1, "test-cb");

        when(chargePointsService.addChargePoint(any(ChargePointForm.class))).thenReturn(1);
        when(chargePointsService.getDetails(1)).thenReturn(result);

        mockMvc.perform(post("/api/v1/chargeboxes")
                .content(objectMapper.writeValueAsString(form))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.chargeBoxPk").value("1"))
            .andExpect(jsonPath("$.chargeBoxId").value("test-cb"));
    }

    @Test
    @DisplayName("PUT: Entity updated, expected 200")
    public void testPut() throws Exception {
        ChargePointForm form = new ChargePointForm();
        form.setChargeBoxId("test-cb-updated");
        form.setRegistrationStatus("updated");
        form.setInsertConnectorStatusAfterTransactionMsg(true);

        ChargePoint.Details result = createDetails(1, "test-cb-updated");

        when(chargePointsService.getDetails(1)).thenReturn(result);

        mockMvc.perform(put("/api/v1/chargeboxes/1")
                .content(objectMapper.writeValueAsString(form))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());

        verify(chargePointsService).updateChargePoint(any(ChargePointForm.class));
    }

    @Test
    @DisplayName("DELETE: Entity deleted, expected 200")
    public void testDelete() throws Exception {
        var rec = new ChargeBoxRecord();
        rec.setChargeBoxPk(1);
        var result = new ChargePoint.Details(rec, null);
        when(chargePointsService.getDetails(1)).thenReturn(result);

        mockMvc.perform(delete("/api/v1/chargeboxes/1"))
            .andExpect(status().isOk());

        verify(chargePointsService).deleteChargePoint(1);
    }
}
