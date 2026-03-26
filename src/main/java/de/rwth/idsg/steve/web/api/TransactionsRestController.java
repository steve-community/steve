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
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.service.TransactionReportService;
import de.rwth.idsg.steve.service.TransactionService;
import de.rwth.idsg.steve.web.api.ApiControllerAdvice.ApiErrorResponse;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import de.rwth.idsg.steve.web.dto.TransactionReportRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.09.2022
 */
@Tag(name = "transaction-controller",
    description = """
        Operations related to querying transactions.
        A transaction represents a charging session at a charge box (i.e. charging station. The notions 'charge box' and 'charging station' are being used interchangeably).
        """
)
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TransactionsRestController {

    private final TransactionService transactionService;
    private final TransactionReportService transactionReportService;

    @Operation(description = """
        Returns a list of transactions based on the query parameters.
        The query parameters can be used to filter the transactions.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "")
    public List<Transaction> get(@Valid @ParameterObject TransactionQueryForm.TransactionQueryFormForApi params) {
        log.debug("Read request for query: {}", params);

        if (params.isReturnCSV()) {
            throw new SteveException.BadRequest("returnCSV=true is not supported for API calls");
        }

        var response = transactionService.getTransactions(params);
        log.debug("Read response for query: {}", response);
        return response;
    }

    @Operation(description = """
        Returns the details of a single transaction based on the transactionPk.
        The details are the intermediate values of the transaction.
        """)
    @GetMapping(value = "/{transactionPk}")
    public TransactionDetails getTransactionDetails(@PathVariable("transactionPk") int transactionPk) {
        return transactionService.getDetails(transactionPk);
    }

    @Operation(description = """
        Manually changes this transaction from 'active' to 'stopped'.
        """)
    @PatchMapping(value = "/{transactionPk}/stop")
    public void stopTransaction(@PathVariable("transactionPk") int transactionPk) {
        transactionService.stop(transactionPk);
    }

    @Operation(description = """
        Returns charging consumption data as CSV between the given 'from' and 'to' timestamps.
        All transactions that stopped between the given timestamps are included.
        The exported data can be used for reports or can be imported into 3rd party payment/billing systems.
        """)
    @GetMapping(value = "/reports/csv", produces = "text/csv")
    public ResponseEntity<String> getTransactionReportCsv(@Valid @ParameterObject TransactionReportRequest dto) {
        String data = transactionReportService.getTransactionReportCsv(dto.getFrom(), dto.getTo());

        String fileName = "transaction_report.csv";
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=\"%s\"".formatted(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/csv");
        headers.add(headerKey, headerValue);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
