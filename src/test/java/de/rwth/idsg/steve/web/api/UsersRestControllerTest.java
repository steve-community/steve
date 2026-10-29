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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.service.UserService;
import de.rwth.idsg.steve.web.dto.UserForm;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author huangkongsheng
 * @since 19.10.2026
 */
@ExtendWith(MockitoExtension.class)
public class UsersRestControllerTest extends AbstractControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UsersRestController(userService))
            .setControllerAdvice(new ApiControllerAdvice())
            .setMessageConverters(new JacksonJsonHttpMessageConverter(objectMapper))
            .alwaysExpect(content().contentType(CONTENT_TYPE))
            .build();
    }

    @Test
    @DisplayName("GET all: Test with empty results, expected 200")
    public void test1() throws Exception {
        // given
        List<User.Overview> results = Collections.emptyList();

        // when
        when(userService.getOverview(any())).thenReturn(results);

        // then
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET all: Test with one result containing OCPP tags, expected 200")
    public void test2() throws Exception {
        // given
        User.Overview result = User.Overview.builder()
            .userPk(42)
            .name("Test User")
            .email("test@example.org")
            .ocppTagEntries(List.of(new User.OcppTagEntry(7, "TEST-001")))
            .build();

        // when
        when(userService.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].userPk").value("42"))
            .andExpect(jsonPath("$[0].name").value("Test User"))
            .andExpect(jsonPath("$[0].ocppTagEntries", hasSize(1)))
            .andExpect(jsonPath("$[0].ocppTagEntries[0].ocppTagPk").value("7"))
            .andExpect(jsonPath("$[0].ocppTagEntries[0].idTag").value("TEST-001"));
    }

    @Test
    @DisplayName("GET all: Downstream bean throws exception, expected 500")
    public void test3() throws Exception {
        // when
        when(userService.getOverview(any())).thenThrow(new RuntimeException("failed"));

        // then
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isInternalServerError())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: Wrong path variable format makes validation fail, expected 400")
    public void test4() throws Exception {
        mockMvc.perform(get("/api/v1/users/not-an-integer"))
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: Entity not found, expected 404")
    public void test5() throws Exception {
        // when
        when(userService.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(get("/api/v1/users/12"))
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: One entity found, expected 200")
    public void test6() throws Exception {
        // given
        User.Overview result = User.Overview.builder().userPk(12).name("Test User").build();

        // when
        when(userService.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(get("/api/v1/users/12"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userPk").value("12"));
    }

    @Test
    @DisplayName("POST: Invalid e-mail makes validation fail, expected 400")
    public void test7() throws Exception {
        // given
        UserForm form = new UserForm();
        form.setFirstName("First");
        form.setEmail("not-an-email");

        // when and then
        mockMvc.perform(
                post("/api/v1/users")
                    .content(objectMapper.writeValueAsString(form))
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("POST: Entity created, expected 201")
    public void test8() throws Exception {
        // given
        int userPk = 123;

        UserForm form = new UserForm();
        form.setFirstName("First");
        form.setLastName("Last");

        User.Overview result = User.Overview.builder()
            .userPk(userPk)
            .name("First Last")
            .build();

        // when
        // UserForm has no value equals; the request is a different instance, so
        // match any form and verify the fields on the captured argument below.
        when(userService.add(any(UserForm.class))).thenReturn(userPk);
        when(userService.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(
                post("/api/v1/users")
                    .content(objectMapper.writeValueAsString(form))
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userPk").value("123"))
            .andExpect(jsonPath("$.name").value("First Last"));

        ArgumentCaptor<UserForm> captor = ArgumentCaptor.forClass(UserForm.class);
        verify(userService).add(captor.capture());
        assertEquals("First", captor.getValue().getFirstName());
        assertEquals("Last", captor.getValue().getLastName());
    }

    @Test
    @DisplayName("PUT: Unknown user is rejected before any write, expected 404")
    public void test13() throws Exception {
        // given
        UserForm form = new UserForm();
        form.setFirstName("First");

        // when
        when(userService.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(
                put("/api/v1/users/999")
                    .content(objectMapper.writeValueAsString(form))
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());

        verify(userService, times(0)).update(any());
    }

    @Test
    @DisplayName("POST: Downstream bean throws exception, expected 500")
    public void test9() throws Exception {
        // given
        UserForm form = new UserForm();
        form.setFirstName("First");

        // when
        when(userService.add(any())).thenThrow(new SteveException("failed"));

        // then
        mockMvc.perform(
                post("/api/v1/users")
                    .content(objectMapper.writeValueAsString(form))
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isInternalServerError())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("PUT: Entity updated, expected 200")
    public void test10() throws Exception {
        // given
        int userPk = 123;

        UserForm form = new UserForm();
        form.setFirstName("Updated");

        User.Overview result = User.Overview.builder()
            .userPk(userPk)
            .name("Updated Last")
            .build();

        // when
        when(userService.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(
                put("/api/v1/users/" + userPk)
                    .content(objectMapper.writeValueAsString(form))
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isOk());

        verify(userService).update(any());
    }

    @Test
    @DisplayName("DELETE: Entity deleted, expected 200")
    public void test11() throws Exception {
        // given
        int userPk = 123;

        User.Overview result = User.Overview.builder().userPk(userPk).name("Test User").build();

        // when
        when(userService.getOverview(any())).thenReturn(List.of(result));

        // then
        mockMvc.perform(delete("/api/v1/users/" + userPk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userPk").value("123"));

        verify(userService).delete(eq(userPk));
    }

    @Test
    @DisplayName("DELETE: Entity not found, expected 404")
    public void test12() throws Exception {
        // given
        int userPk = 123;

        // when
        when(userService.getOverview(any())).thenReturn(Collections.emptyList());

        // then
        mockMvc.perform(delete("/api/v1/users/" + userPk))
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());

        verify(userService, times(0)).delete(userPk);
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

    @Test
    @DisplayName("GET all: Explicitly empty ocppTagFilter does not fail, expected 200")
    public void test14() throws Exception {
        // The binder turns "ocppTagFilter=" into null; the repository falls back
        // to the default filter instead of NPE-ing on it.
        when(userService.getOverview(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users")
                .param("ocppTagFilter", ""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST: Malformed json, expected 400")
    public void test15() throws Exception {
        mockMvc.perform(
                post("/api/v1/users")
                    .content("{not-json")
                    .contentType(CONTENT_TYPE)
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }
}
