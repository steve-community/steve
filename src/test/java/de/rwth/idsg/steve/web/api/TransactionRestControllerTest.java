package de.rwth.idsg.steve.web.api;

import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.09.2022
 */
@ExtendWith(MockitoExtension.class)
public class TransactionRestControllerTest {

    @Mock
    private TransactionRepository transactionRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TransactionsRestController(transactionRepository))
            .setControllerAdvice(new ApiControllerAdvice())
            .setMessageConverters(new MappingJackson2HttpMessageConverter())
            .alwaysExpect(content().contentType("application/json;charset=UTF-8"))
            .build();
    }

    @Test
    @DisplayName("Test with empty results, expected 200")
    public void test1() throws Exception {
        // given
        List<Transaction> results = Collections.emptyList();

        // when
        when(transactionRepository.getTransactions(any())).thenReturn(results);

        // then
        mockMvc.perform(get("/api/v1/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Test with one result, expected 200")
    public void test2() throws Exception {
        // given
        List<Transaction> results = List.of(Transaction.builder().id(234).build());

        // when
        when(transactionRepository.getTransactions(any())).thenReturn(results);

        // then
        mockMvc.perform(get("/api/v1/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value("234"));
    }


    @Test
    @DisplayName("Downstream bean throws exception, expected 500")
    public void test3() throws Exception {
        // when
        when(transactionRepository.getTransactions(any())).thenThrow(new RuntimeException("failed"));

        // then
        mockMvc.perform(get("/api/v1/transactions"))
            .andExpect(status().isInternalServerError())
            .andExpectAll(errorJsonMatchers());
    }


    @Test
    @DisplayName("Hidden param used, expected 400")
    public void test4() throws Exception {
        mockMvc.perform(get("/api/v1/transactions")
                .param("returnCSV", "true")
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }


    @Test
    @DisplayName("Typo in param makes validation fail, expected 400")
    public void test5() throws Exception {
        mockMvc.perform(get("/api/v1/transactions")
                .param("periodType", "TODAYZZZ")
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("Param requires other params which are not set, expected 400")
    public void test6() throws Exception {
        mockMvc.perform(get("/api/v1/transactions")
                .param("periodType", "FROM_TO")
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("to is before from when using FROM_TO, expected 400")
    public void test7() throws Exception {
        mockMvc.perform(get("/api/v1/transactions")
                .param("periodType", "FROM_TO")
                .param("from", "2022-10-01 00:00")
                .param("to", "2022-09-01 00:00")
            )
            .andExpect(status().isBadRequest())
            .andExpectAll(errorJsonMatchers());
    }

    @Test
    @DisplayName("Sets all valid params, expected 200")
    public void test8() throws Exception {
        // given
        Transaction transaction = Transaction
            .builder()
            .id(1)
            .chargeBoxId("cb-2")
            .ocppIdTag("id-3")
            .build();

        // when
        when(transactionRepository.getTransactions(any())).thenReturn(List.of(transaction));

        // then
        mockMvc.perform(get("/api/v1/transactions")
                .param("transactionPk", String.valueOf(transaction.getId()))
                .param("type", "ACTIVE")
                .param("periodType", "FROM_TO")
                .param("chargeBoxId", transaction.getChargeBoxId())
                .param("ocppIdTag", transaction.getOcppIdTag())
                .param("from", "2022-10-01 00:00")
                .param("to", "2022-10-08 00:00")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].chargeBoxId").value("cb-2"))
            .andExpect(jsonPath("$[0].ocppIdTag").value("id-3"));
    }


    private static ResultMatcher[] errorJsonMatchers() {
        return new ResultMatcher[] {
            jsonPath("$.timestamp").exists(),
            jsonPath("$.status").exists(),
            jsonPath("$.error").exists(),
            jsonPath("$.message").exists(),
            jsonPath("$.path").exists()
        };
    }
}