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

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.TaskOverview;

import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.service.ChargePointService12_Client;
import de.rwth.idsg.steve.service.ChargePointService15_Client;
import de.rwth.idsg.steve.service.ChargePointService16_Client;

import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;

import de.rwth.idsg.steve.web.dto.ocpp.UnlockConnectorParams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.rwth.idsg.steve.web.api.ApiControllerAdvice.ApiErrorResponse;
import de.rwth.idsg.steve.web.api.dto.ApiChargePointList;
import de.rwth.idsg.steve.web.api.dto.ApiChargePointStart;
import de.rwth.idsg.steve.web.api.exception.BadRequestException;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;
import static java.util.Objects.isNull;



/**
 * @author fnkbsi
 * @since 18.10.2023
 */

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/remote", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RemoteStartStopRestController {

    @Autowired private ChargePointHelperService chargePointHelperService;
    @Autowired private ChargePointRepository chargePointRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private TaskStore taskStore;

    @Autowired
    @Qualifier("ChargePointService12_Client")
    private ChargePointService12_Client client12;

    @Autowired
    @Qualifier("ChargePointService15_Client")
    private ChargePointService15_Client client15;

    @Autowired
    @Qualifier("ChargePointService16_Client")
    private ChargePointService16_Client client16;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

     private String getOcppProtocol(String chargeBoxId) {
        ChargePointQueryForm form = new ChargePointQueryForm();
        form.setChargeBoxId(chargeBoxId);
        return chargePointRepository.getOverview(form).get(0).getOcppProtocol().toUpperCase();
     }

     private Integer remoteStart(String chargeBoxId, RemoteStartTransactionParams transactionParams) {
        String ocppProtocol = getOcppProtocol(chargeBoxId);
        Integer taskId;
        taskId = switch (ocppProtocol) {
            case "OCPP1.6J", "OCPP1.6S" -> client16.remoteStartTransaction(transactionParams, "SteveWebApi");
            case "OCPP1.5J", "OCPP1.5S", "OCPP1.5" -> client15.remoteStartTransaction(transactionParams, "SteveWebApi");
            case "OCPP1.2" -> client12.remoteStartTransaction(transactionParams, "SteveWebApi");
            default -> client12.remoteStartTransaction(transactionParams, "SteveWebApi");
        };
        return taskId;
    }

     private Integer remoteStop(String chargeBoxId, RemoteStopTransactionParams transactionParams) {
        String ocppProtocol = getOcppProtocol(chargeBoxId);
        Integer taskId;
        taskId = switch (ocppProtocol) {
            case "OCPP1.6J", "OCPP1.6S" -> client16.remoteStopTransaction(transactionParams, "SteveWebApi");
            case "OCPP1.5J", "OCPP1.5S", "OCPP1.5" -> client15.remoteStopTransaction(transactionParams, "SteveWebApi");
            case "OCPP1.2" -> client12.remoteStopTransaction(transactionParams, "SteveWebApi");
            default -> client12.remoteStopTransaction(transactionParams, "SteveWebApi");
        };
        return taskId;
    }

    private Integer remoteUnlock(String chargeBoxId, UnlockConnectorParams transactionParams) {
        String ocppProtocol = getOcppProtocol(chargeBoxId);
        Integer taskId;
        taskId = switch (ocppProtocol) {
            case "OCPP1.6J", "OCPP1.6S" -> client16.unlockConnector(transactionParams, "SteveWebApi");
            case "OCPP1.5J", "OCPP1.5S", "OCPP1.5" -> client15.unlockConnector(transactionParams, "SteveWebApi");
            case "OCPP1.2" -> client12.unlockConnector(transactionParams, "SteveWebApi");
            default -> client12.unlockConnector(transactionParams, "SteveWebApi");
        };
        return taskId;
    }

    private ApiChargePointList getChargePoints() {
        List<ChargePointSelect> chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_12);
        chargePoints.addAll(chargePointHelperService.getChargePoints(OcppVersion.V_15));
        chargePoints.addAll(chargePointHelperService.getChargePoints(OcppVersion.V_16));
        ApiChargePointList lsCp = new ApiChargePointList();
        try {
            for (ChargePointSelect chargeBox : chargePoints) {
                List<Integer> conList = chargePointRepository.getNonZeroConnectorIds(chargeBox.getChargeBoxId());
                if (!conList.isEmpty()) {
                    lsCp.addCP(chargeBox.getChargeBoxId(), conList);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return lsCp;
    }

    private Boolean activeTaskOnChargeBox(String chargeBoxId) {
        Boolean retValue = false;
        List<TaskOverview> taskList = taskStore.getOverview();
        for (TaskOverview taOverview : taskList) {
            CommunicationTask task = taskStore.get(taOverview.getTaskId());
            if (!task.isFinished()) {
                if (!isNull(task.getResultMap().get(chargeBoxId))) {
                    retValue = true;
                    break;
                }
            }
        }
        return retValue;
    }

    // -------------------------------------------------------------------------
    // Http methods (GET)
    // -------------------------------------------------------------------------

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "")
    @ResponseBody
    public ApiChargePointList getBase() {
        return getChargePoints();
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "start")
    @ResponseBody
    public ApiChargePointList getRemoteStartTx() {
        return getChargePoints();
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "stop")
    @ResponseBody
    public ApiChargePointList getRemoteStopTx() {
        return getChargePoints();
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @GetMapping(value = "unlock")
    @ResponseBody
    public ApiChargePointList getUnlockCon() {
        return getChargePoints();
    }

    // -------------------------------------------------------------------------
    // Http methods (POST)
    // the methods return the taskID, check the sucess with the TaskRestController
    // -------------------------------------------------------------------------

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @PostMapping(value = "start")
    @ResponseBody
    public Integer postRemoteStartTx(@Valid ApiChargePointStart params) {
        // only one active task per charge box over api; to be discussed!
        if (activeTaskOnChargeBox(params.getChargeBoxId())) {
            String errMsg = String.format("Active task found on ChargeBox %s!",
                    params.getChargeBoxId()
            );
            throw new BadRequestException(errMsg);
        }

        // Check for acctive transactions on the connector, If a active transaction is found, don't send RemoteStart.
        Integer transactionId = transactionRepository.getActiveTransactionId(params.getChargeBoxId(),
                params.getConnectorId());
        if (!isNull(transactionId)) {
            String errMsg = String.format("Active transaction found for connector %s at ChargeBox %s!",
                    params.getConnectorId(),
                    params.getChargeBoxId()
            );
            throw new BadRequestException(errMsg);
        }
        // Check if OCPP-Tag is allowed to use the connector? To be discussed and t.b.d.!

        RemoteStartTransactionParams transactionParams = new RemoteStartTransactionParams();
        transactionParams.setChargePointSelectList(chargePointRepository.getChargePointSelect(params.getChargeBoxId()));
        transactionParams.setConnectorId(params.getConnectorId());
        transactionParams.setIdTag(params.getOcppTag());
        return remoteStart(params.getChargeBoxId(), transactionParams);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @PostMapping(value = "stop")
    @ResponseBody
    public Integer postRemoteStopTx(@Valid ApiChargePointStart params) {
        // only one active task per charge box over api; to be discussed!
        if (activeTaskOnChargeBox(params.getChargeBoxId())) {
            String errMsg = String.format("Active task found on ChargeBox %s!",
                    params.getChargeBoxId()
            );
            throw new BadRequestException(errMsg);
        }

        RemoteStopTransactionParams transactionParams = new RemoteStopTransactionParams();
        // set the ChargPointSelectionList, maybe check nessesary that length is one
        transactionParams.setChargePointSelectList(chargePointRepository.getChargePointSelect(params.getChargeBoxId()));

        // Get the transactionId of the active transaction on the connector.
        // If no transaction active don't send RemoteStop
        Integer transactionId = transactionRepository.getActiveTransactionId(params.getChargeBoxId(),
                params.getConnectorId());
        if (isNull(transactionId)) {
            String errMsg = String.format("No active transaction found for connector %s at ChargeBox %s!",
                    params.getConnectorId(),
                    params.getChargeBoxId()
            );
            throw new BadRequestException(errMsg);
        }

        // check the user is allowed to stop this transaction (actual only the one who started it!)
        String ocppTag = transactionRepository.getTransaction(transactionId).getOcppIdTag();
        if (!params.getOcppTag().contentEquals(ocppTag)) {
             throw new BadRequestException("The transaction was authorised with another OCPP Tag!");
        }
        transactionParams.setTransactionId(transactionId);

        return remoteStop(params.getChargeBoxId(), transactionParams);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))})}
    )
    @PostMapping(value = "unlock")
    @ResponseBody
    public Integer postUnlockCon(@Valid ApiChargePointStart params) {
        // only one active task per charge box over api; to be discussed!
        if (activeTaskOnChargeBox(params.getChargeBoxId())) {
            String errMsg = String.format("Active task found on ChargeBox %s!",
                    params.getChargeBoxId()
            );
            throw new BadRequestException(errMsg);
        }

        UnlockConnectorParams transactionParams = new UnlockConnectorParams();
        transactionParams.setChargePointSelectList(chargePointRepository.getChargePointSelect(params.getChargeBoxId()));

        /* If a active transaction is found, don't unlock the connection. */
        Integer transactionId = transactionRepository.getActiveTransactionId(params.getChargeBoxId(),
                params.getConnectorId());
        if (!isNull(transactionId)) {
            String errMsg = String.format("Active transaction found for connector %s at ChargeBox %s!",
                    params.getConnectorId(),
                    params.getChargeBoxId()
            );
            throw new BadRequestException(errMsg);
        }

        transactionParams.setConnectorId(params.getConnectorId());
        return remoteUnlock(params.getChargeBoxId(), transactionParams);
    }
}
