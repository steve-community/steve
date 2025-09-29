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
import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.service.ChargePointsService;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(MockitoExtension.class)
public class ChargePointsRestControllerTest extends AbstractControllerTest {

    @Configuration
    static class ValidationConfig {
        @Bean
        public LocalValidatorFactoryBean validator(ApplicationContext applicationContext) {
            var factory = new LocalValidatorFactoryBean();
            factory.setApplicationContext(applicationContext);
            return factory;
        }
    }

    @Mock
    private ChargePointsService chargePointsService;

    private MockMvcTester mockMvc;

    @BeforeEach
    public void setUp() {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(ValidationConfig.class);
        ctx.register(SteveProperties.class);
        ctx.refresh();

        var validator = ctx.getBean(Validator.class);

        mockMvc = buildMockMvc(MockMvcBuilders.standaloneSetup(new ChargePointsRestController(chargePointsService))
                .setValidator(validator));
    }

    @Test
    @DisplayName("GET all: Test with empty results, expected 200")
    public void testGet_withEmptyList() {
        when(chargePointsService.getOverview(any())).thenReturn(Collections.emptyList());

        assertThat(mockMvc.perform(get("/api/v1/chargeboxes")))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$", path -> assertThat(path).asArray().isEmpty());
    }

    @Test
    @DisplayName("GET all: Test with one result, expected 200")
    public void testGet_withOneResult() {
        List<ChargePoint.Overview> results =
                List.of(ChargePoint.Overview.builder().chargeBoxPk(1).build());
        when(chargePointsService.getOverview(any())).thenReturn(results);

        assertThat(mockMvc.perform(get("/api/v1/chargeboxes")))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$", path -> assertThat(path).asArray().hasSize(1))
                .hasPathSatisfying(
                        "$[0].chargeBoxPk", path -> assertThat(path).asNumber().isEqualTo(1));
    }

    @Test
    @DisplayName("GET one: Entity not found, expected 404")
    public void testGetOne_notFound() {
        when(chargePointsService.getDetails(anyInt())).thenThrow(new SteveException.NotFound(""));

        assertThat(mockMvc.perform(get("/api/v1/chargeboxes/1"))).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET one: One entity found, expected 200")
    public void testGetOne_found() {
        var result = createDetails(1, null);
        when(chargePointsService.getDetails(1)).thenReturn(result);

        assertThat(mockMvc.perform(get("/api/v1/chargeboxes/1")))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying(
                        "$.chargeBoxPk", path -> assertThat(path).asNumber().isEqualTo(1));
    }

    private static ChargePoint.Details createDetails(Integer pk, String chargeBoxId) {
        return ChargePoint.Details.builder()
                .chargeBoxPk(pk)
                .chargeBoxId(chargeBoxId)
                .build();
    }

    @Test
    @DisplayName("POST: Entity created, expected 201")
    public void testPost() throws Exception {
        var form = new ChargePointForm();
        form.setChargeBoxId("test-cb");
        form.setRegistrationStatus("updated");
        form.setInsertConnectorStatusAfterTransactionMsg(true);

        var result = createDetails(1, "test-cb");

        when(chargePointsService.addChargePoint(any(ChargePointForm.class))).thenReturn(1);
        when(chargePointsService.getDetails(1)).thenReturn(result);

        assertThat(mockMvc.perform(post("/api/v1/chargeboxes")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .hasPathSatisfying(
                        "$.chargeBoxPk", path -> assertThat(path).asNumber().isEqualTo(1))
                .hasPathSatisfying(
                        "$.chargeBoxId", path -> assertThat(path).asString().isEqualTo("test-cb"));
    }

    @Test
    @DisplayName("PUT: Entity updated, expected 200")
    public void testPut() throws Exception {
        var form = new ChargePointForm();
        form.setChargeBoxId("test-cb-updated");
        form.setRegistrationStatus("updated");
        form.setInsertConnectorStatusAfterTransactionMsg(true);

        var result = createDetails(1, "test-cb-updated");

        when(chargePointsService.getDetails(1)).thenReturn(result);

        assertThat(mockMvc.perform(put("/api/v1/chargeboxes/1")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)))
                .hasStatusOk();

        verify(chargePointsService).updateChargePoint(any(ChargePointForm.class));
    }

    @Test
    @DisplayName("DELETE: Entity deleted, expected 200")
    public void testDelete() {
        var result = createDetails(1, "test-cb");
        when(chargePointsService.getDetails(1)).thenReturn(result);

        assertThat(mockMvc.perform(delete("/api/v1/chargeboxes/1"))).hasStatusOk();

        verify(chargePointsService).deleteChargePoint(1);
    }
}
