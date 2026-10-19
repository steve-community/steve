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

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.repository.TaskStore;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.hasSize;
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
public class TasksRestControllerTest extends AbstractControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private TaskStore taskStore;

    @Mock
    private CommunicationTask communicationTask;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TasksRestController(taskStore))
            .setControllerAdvice(new ApiControllerAdvice())
            .setMessageConverters(new JacksonJsonHttpMessageConverter(objectMapper))
            .alwaysExpect(content().contentType(CONTENT_TYPE))
            .build();
    }

    @Test
    @DisplayName("GET one: Wrong path variable format makes validation fail, expected 400")
    public void test1() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/not-an-integer"))
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: Unknown task id, expected 404")
    public void test2() throws Exception {
        // when
        when(taskStore.get(123)).thenReturn(null);

        // then
        mockMvc.perform(get("/api/v1/tasks/123"))
            .andExpect(status().isNotFound())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET one: Running task with no responses yet, expected 200")
    public void test3() throws Exception {
        // given
        Map<String, RequestResult> resultMap = new HashMap<>();
        resultMap.put("CP001", result(null, null));
        resultMap.put("CP002", result(null, null));

        when(communicationTask.getOperationName()).thenReturn("ResetTask");
        when(communicationTask.getStartTimestamp()).thenReturn(DateTime.parse("2026-10-19T08:00:00.000Z"));
        when(communicationTask.getEndTimestamp()).thenReturn(null);
        when(communicationTask.getResultMap()).thenReturn(resultMap);
        when(communicationTask.getResponseCount()).thenReturn(new AtomicInteger(0));
        when(communicationTask.getErrorCount()).thenReturn(new AtomicInteger(0));
        when(communicationTask.getResultSize()).thenReturn(2);
        when(taskStore.get(123)).thenReturn(communicationTask);

        // then
        mockMvc.perform(get("/api/v1/tasks/123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value("123"))
            .andExpect(jsonPath("$.operation").value("ResetTask"))
            .andExpect(jsonPath("$.state").value("RUNNING"))
            .andExpect(jsonPath("$.taskFinished").value("false"))
            .andExpect(jsonPath("$.resultSize").value("2"))
            .andExpect(jsonPath("$.successResponses", hasSize(0)))
            .andExpect(jsonPath("$.errorResponses", hasSize(0)));
    }

    @Test
    @DisplayName("GET one: Fully successful task, expected 200")
    public void test4() throws Exception {
        // given
        Map<String, RequestResult> resultMap = new HashMap<>();
        resultMap.put("CP001", result("Accepted", null));
        resultMap.put("CP002", result("Accepted", null));

        when(communicationTask.getOperationName()).thenReturn("ResetTask");
        when(communicationTask.getStartTimestamp()).thenReturn(DateTime.parse("2026-10-19T08:00:00.000Z"));
        when(communicationTask.getEndTimestamp()).thenReturn(DateTime.parse("2026-10-19T08:00:03.000Z"));
        when(communicationTask.getResultMap()).thenReturn(resultMap);
        when(communicationTask.getResponseCount()).thenReturn(new AtomicInteger(2));
        when(communicationTask.getErrorCount()).thenReturn(new AtomicInteger(0));
        when(communicationTask.getResultSize()).thenReturn(2);
        when(taskStore.get(123)).thenReturn(communicationTask);

        // then
        mockMvc.perform(get("/api/v1/tasks/123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.state").value("COMPLETED"))
            .andExpect(jsonPath("$.taskFinished").value("true"))
            .andExpect(jsonPath("$.responseCount").value("2"))
            .andExpect(jsonPath("$.successResponses", hasSize(2)))
            .andExpect(jsonPath("$.successResponses[0].chargeBoxId").value("CP001"))
            .andExpect(jsonPath("$.successResponses[0].response").value("Accepted"))
            .andExpect(jsonPath("$.errorResponses", hasSize(0)));
    }

    @Test
    @DisplayName("GET one: Task with mixed success and errors, expected 200")
    public void test5() throws Exception {
        // given
        Map<String, RequestResult> resultMap = new HashMap<>();
        resultMap.put("CP001", result("Accepted", null));
        resultMap.put("CP002", result(null, "Unsupported parameter"));

        when(communicationTask.getOperationName()).thenReturn("ChangeConfiguration");
        when(communicationTask.getStartTimestamp()).thenReturn(DateTime.parse("2026-10-19T08:00:00.000Z"));
        when(communicationTask.getEndTimestamp()).thenReturn(DateTime.parse("2026-10-19T08:00:03.000Z"));
        when(communicationTask.getResultMap()).thenReturn(resultMap);
        when(communicationTask.getResponseCount()).thenReturn(new AtomicInteger(1));
        when(communicationTask.getErrorCount()).thenReturn(new AtomicInteger(1));
        when(communicationTask.getResultSize()).thenReturn(2);
        when(taskStore.get(123)).thenReturn(communicationTask);

        // then
        mockMvc.perform(get("/api/v1/tasks/123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.state").value("COMPLETED_WITH_ERRORS"))
            .andExpect(jsonPath("$.taskFinished").value("true"))
            .andExpect(jsonPath("$.successResponses", hasSize(1)))
            .andExpect(jsonPath("$.successResponses[0].chargeBoxId").value("CP001"))
            .andExpect(jsonPath("$.errorResponses", hasSize(1)))
            .andExpect(jsonPath("$.errorResponses[0].chargeBoxId").value("CP002"))
            .andExpect(jsonPath("$.errorResponses[0].response").value("Unsupported parameter"));
    }

    @Test
    @DisplayName("GET one: Task where all stations failed, expected 200")
    public void test6() throws Exception {
        // given
        Map<String, RequestResult> resultMap = new HashMap<>();
        resultMap.put("CP001", result(null, "Not implemented"));

        when(communicationTask.getOperationName()).thenReturn("GetConfiguration");
        when(communicationTask.getStartTimestamp()).thenReturn(DateTime.parse("2026-10-19T08:00:00.000Z"));
        when(communicationTask.getEndTimestamp()).thenReturn(DateTime.parse("2026-10-19T08:00:03.000Z"));
        when(communicationTask.getResultMap()).thenReturn(resultMap);
        when(communicationTask.getResponseCount()).thenReturn(new AtomicInteger(0));
        when(communicationTask.getErrorCount()).thenReturn(new AtomicInteger(1));
        when(communicationTask.getResultSize()).thenReturn(1);
        when(taskStore.get(123)).thenReturn(communicationTask);

        // then
        mockMvc.perform(get("/api/v1/tasks/123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.state").value("FAILED"))
            .andExpect(jsonPath("$.successResponses", hasSize(0)))
            .andExpect(jsonPath("$.errorResponses", hasSize(1)));
    }

    private static RequestResult result(String response, String errorMessage) {
        var r = new RequestResult();
        r.setResponse(response);
        r.setErrorMessage(errorMessage);
        return r;
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
