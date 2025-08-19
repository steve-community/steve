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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.OcppTag;
import de.rwth.idsg.steve.service.OcppTagsService;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.09.2022
 */
@ExtendWith(MockitoExtension.class)
public class OcppTagsRestControllerTest extends AbstractControllerTest {

    @Mock
    private OcppTagsService ocppTagsService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = buildMockMvc(MockMvcBuilders.standaloneSetup(new OcppTagsRestController(ocppTagsService)));
    }

    @Test
    @DisplayName("GET all: Test with empty results, expected 200")
    public void test1() throws Exception {
        // given
        List<OcppTag.OcppTagOverview> results = Collections.emptyList();

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(results);

        // then
        mockMvc.perform(get("/api/v1/ocppTags"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET all: Test with one result, expected 200")
    public void test2() throws Exception {
        // given
        List<OcppTag.OcppTagOverview> results = List.of(OcppTag.OcppTagOverview.builder().ocppTagPk(96).build());

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(results);

        // then
        mockMvc.perform(get("/api/v1/ocppTags"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].ocppTagPk").value("96"));
    }

    @Test
    @DisplayName("GET all: Downstream bean throws exception, expected 500")
    public void test3() throws Exception {
        // when
        when(ocppTagsService.getOverview(any())).thenThrow(new RuntimeException("failed"));

        // then
        mockMvc.perform(get("/api/v1/ocppTags"))
            .andExpect(status().isInternalServerError())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET all: Typo in param makes validation fail, expected 400")
    public void test4() throws Exception {
        mockMvc.perform(get("/api/v1/ocppTags")
                .param("blocked", "FALSE1")
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET all: Sets all valid params, expected 200")
    public void test5() throws Exception {
        // given
        DateTime someDate = DateTime.parse("2020-10-01T00:00:00.000Z");
        OcppTag.OcppTagOverview result = OcppTag.OcppTagOverview.builder()
            .ocppTagPk(121)
            .idTag("id-1")
            .parentOcppTagPk(454)
            .parentIdTag("parent-id-1")
            .inTransaction(false)
            .blocked(true)
            .expiryDate(someDate)
            .expiryDateFormatted(DateTimeUtils.humanize(someDate))
            .maxActiveTransactionCount(4)
            .activeTransactionCount(0L)
            .note("some note")
            .build();

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(get("/api/v1/ocppTags")
                .param("ocppTagPk", String.valueOf(result.getOcppTagPk()))
                .param("idTag", result.getIdTag())
                .param("parentIdTag", result.getParentIdTag())
                .param("expired", "FALSE")
                .param("inTransaction", "ALL")
                .param("blocked", "TRUE")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].ocppTagPk").value("121"))
            .andExpect(jsonPath("$[0].idTag").value("id-1"))
            .andExpect(jsonPath("$[0].parentOcppTagPk").value("454"))
            .andExpect(jsonPath("$[0].parentIdTag").value("parent-id-1"))
            .andExpect(jsonPath("$[0].inTransaction").value("false"))
            .andExpect(jsonPath("$[0].blocked").value("true"))
            .andExpect(jsonPath("$[0].expiryDate").value("2020-10-01T00:00:00.000Z"))
            .andExpect(jsonPath("$[0].expiryDateFormatted").doesNotExist())
            .andExpect(jsonPath("$[0].maxActiveTransactionCount").value("4"))
            .andExpect(jsonPath("$[0].activeTransactionCount").value("0"))
            .andExpect(jsonPath("$[0].note").value("some note"));
    }

    @Test
    @DisplayName("GET one: Wrong path variable format makes validation fail, expected 400")
    public void test6() throws Exception {
        mockMvc.perform(get("/api/v1/ocppTags/not-an-integer"))
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: Entity not found, expected 404")
    public void test7() throws Exception {
        // when
        when(ocppTagsService.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/ocppTags/12"))
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: One entity found, expected 200")
    public void test8() throws Exception {
        // given
        OcppTag.OcppTagOverview result = OcppTag.OcppTagOverview.builder().ocppTagPk(12).build();

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(get("/api/v1/ocppTags/12"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ocppTagPk").value("12"));
    }

    @Test
    @DisplayName("POST: Missing idTag makes validation fail, expected 400")
    public void test9() throws Exception {
        // given
        OcppTagForm form = new OcppTagForm();
        form.setIdTag(null);

        // when and then
        mockMvc.perform(
                post("/api/v1/ocppTags")
                    .content(objectMapper.writeValueAsString(form))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());

        verifyNoInteractions(ocppTagsService);
    }

    @Test
    @DisplayName("POST: Expiry in past makes validation fail, expected 400")
    public void test10() throws Exception {
        // given
        OcppTagForm form = new OcppTagForm();
        form.setIdTag("id-123");
        form.setExpiryDate(LocalDateTime.parse("1990-10-01T00:00"));

        // when and then
        mockMvc.perform(
                post("/api/v1/ocppTags")
                    .content(objectMapper.writeValueAsString(form))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());

        verifyNoInteractions(ocppTagsService);
    }

    @Test
    @DisplayName("POST: Entity created, expected 201")
    public void test11() throws Exception {
        // given
        int ocppTagPk = 123;

        OcppTagForm form = new OcppTagForm();
        form.setIdTag("id-123");

        OcppTag.OcppTagOverview result = OcppTag.OcppTagOverview.builder()
            .ocppTagPk(ocppTagPk)
            .idTag(form.getIdTag())
            .build();

        // when
        when(ocppTagsService.addOcppTag(eq(form))).thenReturn(ocppTagPk);
        when(ocppTagsService.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(
                post("/api/v1/ocppTags")
                    .content(objectMapper.writeValueAsString(form))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.ocppTagPk").value("123"))
            .andExpect(jsonPath("$.idTag").value("id-123"));
    }

    @Test
    @DisplayName("PUT: Entity updated, expected 200")
    public void test12() throws Exception {
        // given
        int ocppTagPk = 123;

        OcppTagForm form = new OcppTagForm();
        form.setOcppTagPk(ocppTagPk);
        form.setIdTag("id-123");
        form.setNote("note-1");

        OcppTag.OcppTagOverview result = OcppTag.OcppTagOverview.builder()
            .ocppTagPk(ocppTagPk)
            .idTag(form.getIdTag())
            .note(form.getNote())
            .build();

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(
                put("/api/v1/ocppTags/" + ocppTagPk)
                    .content(objectMapper.writeValueAsString(form))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isOk());

        verify(ocppTagsService).updateOcppTag(eq(form));
    }

    @Test
    @DisplayName("PUT: Missing idTag makes validation fail, expected 400")
    public void test13() throws Exception {
        // given
        int ocppTagPk = 123;

        OcppTagForm form = new OcppTagForm();
        form.setOcppTagPk(ocppTagPk);
        form.setNote("note-1");

        // then
        mockMvc.perform(
                put("/api/v1/ocppTags/" + ocppTagPk)
                    .content(objectMapper.writeValueAsString(form))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isBadRequest());

        verifyNoInteractions(ocppTagsService);
    }

    @Test
    @DisplayName("PUT: Downstream bean throws exception, expected 500")
    public void test14() throws Exception {
        // given
        int ocppTagPk = 123;

        OcppTagForm form = new OcppTagForm();
        form.setIdTag("id-123");
        form.setOcppTagPk(ocppTagPk);
        form.setNote("note-1");

        // when
        doThrow(new SteveException("failed")).when(ocppTagsService).updateOcppTag(any());

        // then
        mockMvc.perform(
                put("/api/v1/ocppTags/" + ocppTagPk)
                    .content(objectMapper.writeValueAsString(form))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isInternalServerError())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("DELETE: Entity deleted, expected 200")
    public void test15() throws Exception {
        // given
        int ocppTagPk = 123;

        OcppTag.OcppTagOverview result = OcppTag.OcppTagOverview.builder()
            .ocppTagPk(ocppTagPk)
            .idTag("id-123")
            .note("note-2")
            .build();

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(delete("/api/v1/ocppTags/" + ocppTagPk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ocppTagPk").value("123"));

        verify(ocppTagsService).deleteOcppTag(ocppTagPk);
    }

    @Test
    @DisplayName("DELETE: Downstream bean throws exception, expected 500")
    public void test16() throws Exception {
        // given
        int ocppTagPk = 123;

        OcppTag.OcppTagOverview result = OcppTag.OcppTagOverview.builder()
            .ocppTagPk(ocppTagPk)
            .idTag("id-123")
            .note("note-2")
            .build();

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(List.of(result));
        doThrow(new SteveException("failed")).when(ocppTagsService).deleteOcppTag(ocppTagPk);

        // then
        mockMvc.perform(delete("/api/v1/ocppTags/" + ocppTagPk))
            .andExpect(status().isInternalServerError())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("DELETE: Entity not found, expected 404")
    public void test17() throws Exception {
        // given
        int ocppTagPk = 123;

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(List.of());

        // then
        mockMvc.perform(delete("/api/v1/ocppTags/" + ocppTagPk))
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());

        verify(ocppTagsService, times(0)).deleteOcppTag(anyInt());
    }

    @Test
    @DisplayName("POST: Cannot create because entity exists already, returns 422")
    public void test18() throws Exception {
        // given
        int ocppTagPk = 123;

        OcppTagForm form = new OcppTagForm();
        form.setIdTag("id-123");

        // when
        when(ocppTagsService.addOcppTag(eq(form))).thenThrow(new SteveException.AlreadyExists("A user with idTag '%s' already exists.", ocppTagPk));

        // then
        mockMvc.perform(
                post("/api/v1/ocppTags")
                    .content(objectMapper.writeValueAsString(form))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpectAll(errorJsonMatchers());

        verify(ocppTagsService, times(0)).removeUnknown(anyList());
        verify(ocppTagsService, times(0)).getOverview(any(OcppTagQueryForm.OcppTagQueryFormForApi.class));
    }

    @Test
    @DisplayName("GET all: Query param 'expired' is translated correctly, while others are defaulted")
    public void test19() throws Exception {
        // given
        ArgumentCaptor<OcppTagQueryForm.OcppTagQueryFormForApi> formToCapture = ArgumentCaptor.forClass(OcppTagQueryForm.OcppTagQueryFormForApi.class);

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/ocppTags")
                .param("expired", "FALSE"))
            .andExpect(status().isOk());

        verify(ocppTagsService).getOverview(formToCapture.capture());
        OcppTagQueryForm.OcppTagQueryFormForApi capturedForm = formToCapture.getValue();

        assertEquals(OcppTagQueryForm.BooleanType.FALSE, capturedForm.getExpired());
        assertEquals(OcppTagQueryForm.BooleanType.ALL, capturedForm.getInTransaction());
        assertEquals(OcppTagQueryForm.BooleanType.ALL, capturedForm.getBlocked());
    }

    @Test
    @DisplayName("GET all: Query param 'inTransaction' is translated correctly, while others are defaulted")
    public void test20() throws Exception {
        // given
        ArgumentCaptor<OcppTagQueryForm.OcppTagQueryFormForApi> formToCapture = ArgumentCaptor.forClass(OcppTagQueryForm.OcppTagQueryFormForApi.class);

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/ocppTags")
                .param("inTransaction", "TRUE"))
            .andExpect(status().isOk());

        verify(ocppTagsService).getOverview(formToCapture.capture());
        OcppTagQueryForm.OcppTagQueryFormForApi capturedForm = formToCapture.getValue();

        assertEquals(OcppTagQueryForm.BooleanType.ALL, capturedForm.getExpired());
        assertEquals(OcppTagQueryForm.BooleanType.TRUE, capturedForm.getInTransaction());
        assertEquals(OcppTagQueryForm.BooleanType.ALL, capturedForm.getBlocked());
    }

    @Test
    @DisplayName("GET all: Query param 'inTransaction' is translated correctly, while others are defaulted")
    public void test21() throws Exception {
        // given
        ArgumentCaptor<OcppTagQueryForm.OcppTagQueryFormForApi> formToCapture = ArgumentCaptor.forClass(OcppTagQueryForm.OcppTagQueryFormForApi.class);

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/ocppTags")
                .param("blocked", "FALSE"))
            .andExpect(status().isOk());

        verify(ocppTagsService).getOverview(formToCapture.capture());
        OcppTagQueryForm.OcppTagQueryFormForApi capturedForm = formToCapture.getValue();

        assertEquals(OcppTagQueryForm.BooleanType.ALL, capturedForm.getExpired());
        assertEquals(OcppTagQueryForm.BooleanType.ALL, capturedForm.getInTransaction());
        assertEquals(OcppTagQueryForm.BooleanType.FALSE, capturedForm.getBlocked());
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
