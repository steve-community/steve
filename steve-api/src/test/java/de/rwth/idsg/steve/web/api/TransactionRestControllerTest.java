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

import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.json.JsonContent;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.09.2022
 */
@ExtendWith(MockitoExtension.class)
public class TransactionRestControllerTest extends AbstractControllerTest {

    @Mock
    private TransactionRepository transactionRepository;

    private MockMvcTester mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = buildMockMvc(MockMvcBuilders.standaloneSetup(new TransactionsRestController(transactionRepository)));
    }

    @Test
    @DisplayName("Test with empty results, expected 200")
    public void test1() {
        // given
        List<Transaction> results = Collections.emptyList();

        // when
        when(transactionRepository.getTransactions(any())).thenReturn(results);

        // then
        assertThat(mockMvc.perform(get("/api/v1/transactions")))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$", path -> assertThat(path).asArray().isEmpty());
    }

    @Test
    @DisplayName("Test with one result, expected 200")
    public void test2() {
        // given
        List<Transaction> results = List.of(Transaction.builder().id(234).build());

        // when
        when(transactionRepository.getTransactions(any())).thenReturn(results);

        // then
        assertThat(mockMvc.perform(get("/api/v1/transactions")))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$", path -> assertThat(path).asArray().hasSize(1))
                .hasPathSatisfying(
                        "$[0].id", path -> assertThat(path).asNumber().isEqualTo(234));
    }

    @Test
    @DisplayName("Downstream bean throws exception, expected 500")
    public void test3() {
        // when
        when(transactionRepository.getTransactions(any())).thenThrow(new RuntimeException("failed"));

        // then
        assertThat(mockMvc.perform(get("/api/v1/transactions")))
                .hasStatus5xxServerError()
                .bodyJson()
                .satisfies(errorJsonMatchers());
    }

    @Test
    @DisplayName("Hidden param used, expected 400")
    public void test4() {
        assertThat(mockMvc.perform(get("/api/v1/transactions").param("returnCSV", "true")))
                .hasStatus4xxClientError()
                .bodyJson()
                .satisfies(errorJsonMatchers());
    }

    @Test
    @DisplayName("Typo in param makes validation fail, expected 400")
    public void test5() {
        assertThat(mockMvc.perform(get("/api/v1/transactions").param("periodType", "TODAYZZZ")))
                .hasStatus4xxClientError()
                .bodyJson()
                .satisfies(errorJsonMatchers());
    }

    @Test
    @DisplayName("Param requires other params which are not set, expected 400")
    public void test6() {
        assertThat(mockMvc.perform(get("/api/v1/transactions").param("periodType", "FROM_TO")))
                .hasStatus4xxClientError()
                .bodyJson()
                .satisfies(errorJsonMatchers());
    }

    @Test
    @DisplayName("to is before from when using FROM_TO, expected 400")
    public void test7() {
        assertThat(mockMvc.perform(get("/api/v1/transactions")
                        .param("periodType", "FROM_TO")
                        .param("from", "2022-10-01T00:00")
                        .param("to", "2022-09-01T00:00")))
                .hasStatus4xxClientError()
                .bodyJson()
                .satisfies(errorJsonMatchers());
    }

    @Test
    @DisplayName("Sets all valid params, expected 200")
    public void test8() {
        var start = Instant.parse("2022-10-01T00:00:00Z");
        var stop = start.plus(2, ChronoUnit.HOURS);

        // given
        Transaction transaction = Transaction.builder()
                .startTimestamp(start)
                .stopTimestamp(stop)
                .id(1)
                .chargeBoxId("cb-2")
                .ocppIdTag("id-3")
                .build();

        // when
        when(transactionRepository.getTransactions(any())).thenReturn(List.of(transaction));

        // then
        assertThat(mockMvc.perform(get("/api/v1/transactions")
                        .param("transactionPk", String.valueOf(transaction.getId()))
                        .param("type", "ACTIVE")
                        .param("periodType", "FROM_TO")
                        .param("chargeBoxId", transaction.getChargeBoxId())
                        .param("ocppIdTag", transaction.getOcppIdTag())
                        .param("from", "2022-10-01T00:00:00Z")
                        .param("to", "2022-10-08T00:00:00Z")))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$", path -> assertThat(path).asArray().hasSize(1))
                .hasPathSatisfying(
                        "$[0].id", path -> assertThat(path).asNumber().isEqualTo(1))
                .hasPathSatisfying(
                        "$[0].chargeBoxId", path -> assertThat(path).asString().isEqualTo("cb-2"))
                .hasPathSatisfying(
                        "$[0].ocppIdTag", path -> assertThat(path).asString().isEqualTo("id-3"))
                .hasPathSatisfying(
                        "$[0].startTimestamp",
                        path -> assertThat(path).asString().isEqualTo("2022-10-01T00:00:00Z"))
                .doesNotHavePath("$[0].startTimestampFormatted")
                .hasPathSatisfying(
                        "$[0].stopTimestamp",
                        path -> assertThat(path).asString().isEqualTo("2022-10-01T02:00:00Z"))
                .doesNotHavePath("$[0].stopTimestampFormatted");
    }

    @Test
    @DisplayName("from and to have are not conform with ISO")
    public void test9() {
        assertThat(mockMvc.perform(get("/api/v1/transactions")
                        .param("periodType", "FROM_TO")
                        .param("from", "2022-10-01 00:00")
                        .param("to", "2023-10-01 00:00")))
                .hasStatus4xxClientError()
                .bodyJson()
                .satisfies(errorJsonMatchers());
    }

    @Test
    @DisplayName("GET all: Query param 'type' is translated correctly, while others are defaulted")
    public void test10() {
        // given
        ArgumentCaptor<TransactionQueryForm.TransactionQueryFormForApi> formToCapture =
                ArgumentCaptor.forClass(TransactionQueryForm.TransactionQueryFormForApi.class);

        // when
        when(transactionRepository.getTransactions(any())).thenReturn(Collections.emptyList());

        // then
        assertThat(mockMvc.perform(get("/api/v1/transactions").param("type", "ACTIVE")))
                .hasStatusOk();

        verify(transactionRepository).getTransactions(formToCapture.capture());
        TransactionQueryForm.TransactionQueryFormForApi capturedForm = formToCapture.getValue();

        assertThat(capturedForm.getType()).isEqualTo(TransactionQueryForm.QueryType.ACTIVE);
        assertThat(capturedForm.getPeriodType()).isEqualTo(TransactionQueryForm.QueryPeriodType.ALL);
    }

    @Test
    @DisplayName("GET all: Query param 'periodType' is translated correctly, while others are defaulted")
    public void test11() {
        // given
        ArgumentCaptor<TransactionQueryForm.TransactionQueryFormForApi> formToCapture =
                ArgumentCaptor.forClass(TransactionQueryForm.TransactionQueryFormForApi.class);

        // when
        when(transactionRepository.getTransactions(any())).thenReturn(Collections.emptyList());

        // then
        assertThat(mockMvc.perform(get("/api/v1/transactions").param("periodType", "LAST_30")))
                .hasStatusOk();

        verify(transactionRepository).getTransactions(formToCapture.capture());
        TransactionQueryForm.TransactionQueryFormForApi capturedForm = formToCapture.getValue();

        assertThat(capturedForm.getType()).isEqualTo(TransactionQueryForm.QueryType.ALL);
        assertThat(capturedForm.getPeriodType()).isEqualTo(TransactionQueryForm.QueryPeriodType.LAST_30);
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
