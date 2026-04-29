/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.ocpp.task.RemoteStartTransactionTask;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.service.ChargePointService16_Client;
import de.rwth.idsg.steve.web.api.exception.BadRequestException;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionStatusResponse;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TransactionStatusRestController {

    private final ChargePointService16_Client v16Client;
    private final TaskStore taskStore;
    private final TransactionRepository transactionRepository;

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiControllerAdvice.ApiErrorResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = ApiControllerAdvice.ApiErrorResponse.class),
            @ApiResponse(code = 422, message = "Unprocessable Entity", response = ApiControllerAdvice.ApiErrorResponse.class),
            @ApiResponse(code = 404, message = "Not Found", response = ApiControllerAdvice.ApiErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ApiControllerAdvice.ApiErrorResponse.class)}
    )
    @PostMapping(value = "/start")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public int start(@RequestBody @Valid RemoteStartTransactionParams params) {
        log.debug("Create request: {}", params);

        int response = v16Client.remoteStartTransaction(params);

        log.debug("Create response: {}", response);
        log.debug("SENT PAYLOAD: {}", params);
        return response;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiControllerAdvice.ApiErrorResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = ApiControllerAdvice.ApiErrorResponse.class),
            @ApiResponse(code = 422, message = "Unprocessable Entity", response = ApiControllerAdvice.ApiErrorResponse.class),
            @ApiResponse(code = 404, message = "Not Found", response = ApiControllerAdvice.ApiErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ApiControllerAdvice.ApiErrorResponse.class)}
    )
    @PostMapping(value = "/stop")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public int stop(@RequestBody @Valid RemoteStopTransactionParams params) {
        log.debug("Create request: {}", params);

        int response = v16Client.remoteStopTransaction(params);

        log.debug("Create response: {}", response);
        log.debug("SENT PAYLOAD: {}", params);
        return response;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiControllerAdvice.ApiErrorResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = ApiControllerAdvice.ApiErrorResponse.class),
            @ApiResponse(code = 404, message = "Not Found", response = ApiControllerAdvice.ApiErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ApiControllerAdvice.ApiErrorResponse.class)}
    )
    @GetMapping(value = "/tasks/{taskId}")
    @ResponseBody
    public RemoteStartTransactionStatusResponse getStatus(@PathVariable("taskId") int taskId) {
        RemoteStartTransactionTask task = loadRemoteStartTask(taskId);

        RemoteStartTransactionParams params = task.getParams();
        List<ChargePointSelect> chargePoints = params.getChargePointSelectList();

        if (chargePoints == null || chargePoints.size() != 1) {
            throw new BadRequestException(String.format("Task %d does not reference exactly one charge point", taskId));
        }

        ChargePointSelect chargePoint = chargePoints.get(0);
        String chargeBoxId = chargePoint.getChargeBoxId();
        Integer connectorId = params.getConnectorId();
        String idTag = params.getIdTag();

        RequestResult requestResult = task.getResultMap().get(chargeBoxId);

        TransactionQueryForm query = new TransactionQueryForm();
        query.setChargeBoxId(chargeBoxId);
        query.setOcppIdTag(idTag);
        query.setType(TransactionQueryForm.QueryType.ACTIVE);
        query.setPeriodType(TransactionQueryForm.QueryPeriodType.ALL);

        List<Transaction> transactions = transactionRepository.getTransactions(query);

        if (connectorId != null) {
            transactions = transactions.stream()
                    .filter(tx -> tx.getConnectorId() == connectorId)
                    .collect(Collectors.toList());
        }

        List<Integer> transactionIds = transactions.stream()
                .map(Transaction::getId)
                .collect(Collectors.toList());

        Integer transactionId = transactionIds.isEmpty() ? null : transactionIds.get(0);

        return RemoteStartTransactionStatusResponse.builder()
                .taskId(taskId)
                .chargeBoxId(chargeBoxId)
                .connectorId(connectorId)
                .idTag(idTag)
                .finished(task.isFinished())
                .response(requestResult != null ? requestResult.getResponse() : null)
                .errorMessage(requestResult != null ? requestResult.getErrorMessage() : null)
                .transactionId(transactionId)
                .activeTransactionIds(transactionIds)
                .build();
    }

    private RemoteStartTransactionTask loadRemoteStartTask(int taskId) {
        CommunicationTask<?, ?> rawTask;
        try {
            rawTask = taskStore.get(taskId);
        } catch (SteveException e) {
            throw new SteveException.NotFound(e.getMessage());
        }

        if (!(rawTask instanceof RemoteStartTransactionTask)) {
            throw new BadRequestException(String.format("Task %d is not a remote start transaction", taskId));
        }
        return (RemoteStartTransactionTask) rawTask;
    }
}
