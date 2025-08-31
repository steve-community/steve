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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonContent;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
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

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.09.2022
 */
@ExtendWith(MockitoExtension.class)
public class OcppTagsRestControllerTest extends AbstractControllerTest {

    @Mock
    private OcppTagsService ocppTagsService;

    private MockMvcTester mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = buildMockMvc(MockMvcBuilders.standaloneSetup(new OcppTagsRestController(ocppTagsService)));
    }

    @Test
    @DisplayName("GET all: Test with empty results, expected 200")
    public void test1() {
        // given
        List<OcppTag.OcppTagOverview> results = Collections.emptyList();

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(results);

        // then
        assertThat(mockMvc.perform(get("/api/v1/ocppTags")))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$")
                .asArray()
                .isEmpty();
    }

    @Test
    @DisplayName("GET all: Test with one result, expected 200")
    public void test2() {
        // given
        List<OcppTag.OcppTagOverview> results =
                List.of(OcppTag.OcppTagOverview.builder().ocppTagPk(96).build());

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(results);

        // then
        assertThat(mockMvc.perform(get("/api/v1/ocppTags")))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$", path -> assertThat(path).asArray().hasSize(1))
                .hasPathSatisfying(
                        "$[0].ocppTagPk", path -> assertThat(path).asNumber().isEqualTo(96));
    }

    @Test
    @DisplayName("GET all: Downstream bean throws exception, expected 500")
    public void test3() {
        // when
        when(ocppTagsService.getOverview(any())).thenThrow(new RuntimeException("failed"));

        // then
        assertThat(mockMvc.perform(get("/api/v1/ocppTags")))
                .hasStatus5xxServerError()
                .bodyJson()
                .satisfies(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET all: Typo in param makes validation fail, expected 400")
    public void test4() {
        assertThat(mockMvc.perform(get("/api/v1/ocppTags").param("blocked", "FALSE1")))
                .hasStatus4xxClientError()
                .bodyJson()
                .satisfies(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET all: Sets all valid params, expected 200")
    public void test5() {
        // given
        var someDate = Instant.parse("2020-10-01T00:00:00.000Z");
        OcppTag.OcppTagOverview result = OcppTag.OcppTagOverview.builder()
                .ocppTagPk(121)
                .idTag("id-1")
                .parentOcppTagPk(454)
                .parentIdTag("parent-id-1")
                .inTransaction(false)
                .blocked(true)
                .expiryDate(someDate)
                .expiryDateFormatted(DateTimeUtils.humanize(someDate, ZoneOffset.UTC))
                .maxActiveTransactionCount(4)
                .activeTransactionCount(0L)
                .note("some note")
                .build();

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(List.of(result));

        // then
        assertThat(mockMvc.perform(get("/api/v1/ocppTags")
                        .param("ocppTagPk", String.valueOf(result.getOcppTagPk()))
                        .param("idTag", result.getIdTag())
                        .param("parentIdTag", result.getParentIdTag())
                        .param("expired", "FALSE")
                        .param("inTransaction", "ALL")
                        .param("blocked", "TRUE")))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$", path -> assertThat(path).asArray().hasSize(1))
                .hasPathSatisfying(
                        "$[0].ocppTagPk", path -> assertThat(path).asNumber().isEqualTo(121))
                .hasPathSatisfying(
                        "$[0].idTag", path -> assertThat(path).asString().isEqualTo("id-1"))
                .hasPathSatisfying(
                        "$[0].parentOcppTagPk",
                        path -> assertThat(path).asNumber().isEqualTo(454))
                .hasPathSatisfying(
                        "$[0].parentIdTag", path -> assertThat(path).asString().isEqualTo("parent-id-1"))
                .hasPathSatisfying(
                        "$[0].inTransaction",
                        path -> assertThat(path).asBoolean().isFalse())
                .hasPathSatisfying(
                        "$[0].blocked", path -> assertThat(path).asBoolean().isTrue())
                .hasPathSatisfying(
                        "$[0].expiryDate", path -> assertThat(path).asString().isEqualTo("2020-10-01T00:00:00Z"))
                .doesNotHavePath("$[0].expiryDateFormatted")
                .hasPathSatisfying(
                        "$[0].maxActiveTransactionCount",
                        path -> assertThat(path).asNumber().isEqualTo(4))
                .hasPathSatisfying(
                        "$[0].activeTransactionCount",
                        path -> assertThat(path).asNumber().isEqualTo(0))
                .hasPathSatisfying(
                        "$[0].note", path -> assertThat(path).asString().isEqualTo("some note"));
    }

    @Test
    @DisplayName("GET one: Wrong path variable format makes validation fail, expected 400")
    public void test6() {
        assertThat(mockMvc.perform(get("/api/v1/ocppTags/not-an-integer")))
                .hasStatus4xxClientError()
                .bodyJson()
                .satisfies(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: Entity not found, expected 404")
    public void test7() {
        // when
        when(ocppTagsService.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        assertThat(mockMvc.perform(get("/api/v1/ocppTags/12")))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .satisfies(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: One entity found, expected 200")
    public void test8() {
        // given
        OcppTag.OcppTagOverview result =
                OcppTag.OcppTagOverview.builder().ocppTagPk(12).build();

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(List.of(result));

        // then
        assertThat(mockMvc.perform(get("/api/v1/ocppTags/12")))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying(
                        "$.ocppTagPk", path -> assertThat(path).asNumber().isEqualTo(12));
    }

    @Test
    @DisplayName("POST: Missing idTag makes validation fail, expected 400")
    public void test9() throws Exception {
        // given
        OcppTagForm form = new OcppTagForm();
        form.setIdTag(null);

        // when and then
        assertThat(mockMvc.perform(post("/api/v1/ocppTags")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)))
                .hasStatus4xxClientError()
                .bodyJson()
                .satisfies(errorJsonMatchers());

        verifyNoInteractions(ocppTagsService);
    }

    @Test
    @DisplayName("POST: Expiry in past makes validation fail, expected 400")
    public void test10() throws Exception {
        // given
        OcppTagForm form = new OcppTagForm();
        form.setIdTag("id-123");
        form.setExpiryDate(Instant.parse("1990-10-01T00:00:00Z"));

        // when and then
        assertThat(mockMvc.perform(post("/api/v1/ocppTags")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)))
                .hasStatus4xxClientError()
                .bodyJson()
                .satisfies(errorJsonMatchers());

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
        when(ocppTagsService.addOcppTag(form)).thenReturn(ocppTagPk);
        when(ocppTagsService.getOverview(any())).thenReturn(List.of(result));

        // then
        assertThat(mockMvc.perform(post("/api/v1/ocppTags")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .hasPathSatisfying(
                        "$.ocppTagPk", path -> assertThat(path).asNumber().isEqualTo(123))
                .hasPathSatisfying(
                        "$.idTag", path -> assertThat(path).asString().isEqualTo("id-123"));
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
        assertThat(mockMvc.perform(put("/api/v1/ocppTags/" + ocppTagPk)
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)))
                .hasStatusOk();

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
        assertThat(mockMvc.perform(put("/api/v1/ocppTags/" + ocppTagPk)
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)))
                .hasStatus4xxClientError();

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
        doThrow(new SteveException.InternalError("failed"))
                .when(ocppTagsService)
                .updateOcppTag(any());

        // then
        assertThat(mockMvc.perform(put("/api/v1/ocppTags/" + ocppTagPk)
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)))
                .hasStatus5xxServerError()
                .bodyJson()
                .satisfies(errorJsonMatchers());
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
        assertThat(mockMvc.perform(delete("/api/v1/ocppTags/" + ocppTagPk)))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying(
                        "$.ocppTagPk", path -> assertThat(path).asNumber().isEqualTo(123));

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
        doThrow(new SteveException.InternalError("failed"))
                .when(ocppTagsService)
                .deleteOcppTag(ocppTagPk);

        // then
        assertThat(mockMvc.perform(delete("/api/v1/ocppTags/" + ocppTagPk)))
                .hasStatus5xxServerError()
                .bodyJson()
                .satisfies(errorJsonMatchers());
    }

    @Test
    @DisplayName("DELETE: Entity not found, expected 404")
    public void test17() throws Exception {
        // given
        int ocppTagPk = 123;

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(List.of());

        // then
        assertThat(mockMvc.perform(delete("/api/v1/ocppTags/" + ocppTagPk)))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .satisfies(errorJsonMatchers());

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
        when(ocppTagsService.addOcppTag(form))
                .thenThrow(new SteveException.AlreadyExists(
                        "A user with idTag '%s' already exists.".formatted(ocppTagPk)));

        // then
        assertThat(mockMvc.perform(post("/api/v1/ocppTags")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)))
                .hasStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                .bodyJson()
                .satisfies(errorJsonMatchers());

        verify(ocppTagsService, times(0)).removeUnknown(anyList());
        verify(ocppTagsService, times(0)).getOverview(any(OcppTagQueryForm.OcppTagQueryFormForApi.class));
    }

    @Test
    @DisplayName("GET all: Query param 'expired' is translated correctly, while others are defaulted")
    public void test19() {
        // given
        ArgumentCaptor<OcppTagQueryForm.OcppTagQueryFormForApi> formToCapture =
                ArgumentCaptor.forClass(OcppTagQueryForm.OcppTagQueryFormForApi.class);

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        assertThat(mockMvc.perform(get("/api/v1/ocppTags").param("expired", "FALSE")))
                .hasStatusOk();

        verify(ocppTagsService).getOverview(formToCapture.capture());
        OcppTagQueryForm.OcppTagQueryFormForApi capturedForm = formToCapture.getValue();

        assertThat(capturedForm.getExpired()).isEqualTo(OcppTagQueryForm.BooleanType.FALSE);
        assertThat(capturedForm.getInTransaction()).isEqualTo(OcppTagQueryForm.BooleanType.ALL);
        assertThat(capturedForm.getBlocked()).isEqualTo(OcppTagQueryForm.BooleanType.ALL);
    }

    @Test
    @DisplayName("GET all: Query param 'inTransaction' is translated correctly, while others are defaulted")
    public void test20() {
        // given
        ArgumentCaptor<OcppTagQueryForm.OcppTagQueryFormForApi> formToCapture =
                ArgumentCaptor.forClass(OcppTagQueryForm.OcppTagQueryFormForApi.class);

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        assertThat(mockMvc.perform(get("/api/v1/ocppTags").param("inTransaction", "TRUE")))
                .hasStatusOk();

        verify(ocppTagsService).getOverview(formToCapture.capture());
        OcppTagQueryForm.OcppTagQueryFormForApi capturedForm = formToCapture.getValue();

        assertThat(capturedForm.getExpired()).isEqualTo(OcppTagQueryForm.BooleanType.ALL);
        assertThat(capturedForm.getInTransaction()).isEqualTo(OcppTagQueryForm.BooleanType.TRUE);
        assertThat(capturedForm.getBlocked()).isEqualTo(OcppTagQueryForm.BooleanType.ALL);
    }

    @Test
    @DisplayName("GET all: Query param 'blocked' is translated correctly, while others are defaulted")
    public void test21() {
        // given
        ArgumentCaptor<OcppTagQueryForm.OcppTagQueryFormForApi> formToCapture =
                ArgumentCaptor.forClass(OcppTagQueryForm.OcppTagQueryFormForApi.class);

        // when
        when(ocppTagsService.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        assertThat(mockMvc.perform(get("/api/v1/ocppTags").param("blocked", "FALSE")))
                .hasStatusOk();

        verify(ocppTagsService).getOverview(formToCapture.capture());
        OcppTagQueryForm.OcppTagQueryFormForApi capturedForm = formToCapture.getValue();

        assertThat(capturedForm.getExpired()).isEqualTo(OcppTagQueryForm.BooleanType.ALL);
        assertThat(capturedForm.getInTransaction()).isEqualTo(OcppTagQueryForm.BooleanType.ALL);
        assertThat(capturedForm.getBlocked()).isEqualTo(OcppTagQueryForm.BooleanType.FALSE);
    }

    private static Consumer<JsonContent> errorJsonMatchers() {
        return content -> content.assertThat()
                .hasPathSatisfying(
                        "$.timestamp", path -> assertThat(path).asString().isNotEmpty())
                .hasPathSatisfying("$.status", path -> assertThat(path).isNotNull())
                .hasPathSatisfying(
                        "$.error", path -> assertThat(path).asString().isNotEmpty())
                .hasPathSatisfying(
                        "$.message", path -> assertThat(path).asString().isNotEmpty())
                .hasPathSatisfying("$.path", path -> assertThat(path).asString().isNotEmpty());
    }
}
