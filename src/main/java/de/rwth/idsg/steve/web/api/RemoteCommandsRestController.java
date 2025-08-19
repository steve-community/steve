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

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.TransactionRepository;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.service.ChargePointServiceClient;

import de.rwth.idsg.steve.web.api.dto.ApiChargePointStop;
import de.rwth.idsg.steve.web.api.dto.ApiChargePointUnlock;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;

import de.rwth.idsg.steve.web.dto.ocpp.UnlockConnectorParams;

import de.rwth.idsg.steve.web.api.dto.ApiChargePointList;
import de.rwth.idsg.steve.web.api.dto.ApiChargePointStart;
import de.rwth.idsg.steve.web.api.exception.BadRequestException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author fnkbsi
 * @since 18.10.2023
 */
@Tag(name = "remote-commands")
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/remote", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RemoteCommandsRestController {

    private static final String CALLER = "SteveWebApi";

    private final ChargePointHelperService chargePointHelperService;
    private final ChargePointRepository chargePointRepository;
    private final TransactionRepository transactionRepository;
    private final TaskStore taskStore;
    private final ChargePointServiceClient client;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private ApiChargePointList getChargePoints() {
        var chargePoints = new ArrayList<ChargePointSelect>();
        chargePoints.addAll(chargePointHelperService.getChargePoints(OcppVersion.V_12));
        chargePoints.addAll(chargePointHelperService.getChargePoints(OcppVersion.V_15));
        chargePoints.addAll(chargePointHelperService.getChargePoints(OcppVersion.V_16));
        var lsCp = new ApiChargePointList();
        try {
            for (var chargeBox : chargePoints) {
                var conList = chargePointRepository.getNonZeroConnectorIds(chargeBox.getChargeBoxId());
                if (!conList.isEmpty()) {
                    lsCp.addCP(chargeBox.getChargeBoxId(), conList);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to build charge point list", e);
        }
        return lsCp;
    }

    private boolean activeTaskOnChargeBox(String chargeBoxId) {
        var retValue = false;
        var taskList = taskStore.getOverview();
        for (var taOverview : taskList) {
            var task = taskStore.get(taOverview.getTaskId());
            if (!task.isFinished()) {
                var resultMap = task.getResultMap();
                if (resultMap != null && resultMap.containsKey(chargeBoxId)) {
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

    @StandardApiResponses
    @GetMapping(value = "")
    public ApiChargePointList getBase() {
        return getChargePoints();
    }

    @StandardApiResponses
    @GetMapping(value = "start")
    public ApiChargePointList getRemoteStartTx() {
        return getChargePoints();
    }

    @StandardApiResponses
    @GetMapping(value = "stop")
    public ApiChargePointList getRemoteStopTx() {
        return getChargePoints();
    }

    @StandardApiResponses
    @GetMapping(value = "unlock")
    public ApiChargePointList getUnlockCon() {
        return getChargePoints();
    }

    // -------------------------------------------------------------------------
    // Http methods (POST)
    // the methods return the taskID, check the success with the TaskRestController
    // -------------------------------------------------------------------------

    @StandardApiResponses
    @PostMapping(value = "start")
    public Integer postRemoteStartTx(@Valid ApiChargePointStart params) {
        // only one active task per charge box over api; to be discussed!
        if (activeTaskOnChargeBox(params.getChargeBoxId())) {
            var errMsg = String.format("Active task found on ChargeBox %s!",
                    params.getChargeBoxId()
            );
            throw new BadRequestException(errMsg);
        }

        var transactionParams = new RemoteStartTransactionParams();

        // Check for active transactions on the connector, If a active transaction is found, don't send RemoteStart.
        if (params.getConnectorId() != null && params.getConnectorId() > 0) {
            var transactionId = transactionRepository.getActiveTransactionId(params.getChargeBoxId(),
                params.getConnectorId());
            if (transactionId.isPresent()) {
                var errMsg = String.format("Active transaction found for connector %s at ChargeBox %s!",
                    params.getConnectorId(),
                    params.getChargeBoxId()
                );
              throw new BadRequestException(errMsg);
            }
        }
        transactionParams.setConnectorId(params.getConnectorId());

        var chargePoint = chargePointRepository.getChargePointSelect(params.getChargeBoxId()).orElseThrow(
            () -> new BadRequestException(
                String.format("ChargeBox %s not found!", params.getChargeBoxId())
            )
        );
        transactionParams.setChargePointSelectList(Collections.singletonList(chargePoint));

        // Check if OCPP-Tag is allowed to use the connector? To be discussed and t.b.d.!
        transactionParams.setIdTag(params.getOcppTag());

        return client.remoteStartTransaction(transactionParams, CALLER);
    }

    @StandardApiResponses
    @PostMapping(value = "stop")
    public Integer postRemoteStopTx(@Valid ApiChargePointStop params) {
        // only one active task per charge box over api; to be discussed!
        if (activeTaskOnChargeBox(params.getChargeBoxId())) {
            var errMsg = String.format("Active task found on ChargeBox %s!",
                    params.getChargeBoxId()
            );
            throw new BadRequestException(errMsg);
        }

        var transactionParams = new RemoteStopTransactionParams();

        // set the ChargPointSelectionList, ensure the length is exactly one
        var chargePoint = chargePointRepository.getChargePointSelect(params.getChargeBoxId()).orElseThrow(
            () -> new BadRequestException(
                String.format("ChargeBox %s not found!", params.getChargeBoxId())
            )
        );
        transactionParams.setChargePointSelectList(Collections.singletonList(chargePoint));

        // Get the transactionId of the active transaction on the connector.
        // If no transaction active don't send RemoteStop
        var transactionId = transactionRepository.getActiveTransactionId(params.getChargeBoxId(),
                params.getConnectorId()).orElseThrow(
            () -> new BadRequestException(
                String.format("No active transaction found for connector %s at ChargeBox %s!",
                    params.getConnectorId(),
                    params.getChargeBoxId()
                )
            )
        );
        // check the user is allowed to stop this transaction (actual only the one who started it!)
        var ocppTag = transactionRepository.getTransaction(transactionId).orElseThrow(
            () -> new BadRequestException(
                String.format("Transaction %s not found!", transactionId)
            )
        ).getOcppIdTag();
        if (!params.getOcppTag().contentEquals(ocppTag)) {
             throw new BadRequestException("The transaction was authorised with another OCPP Tag!");
        }
        transactionParams.setTransactionId(transactionId);

        return client.remoteStopTransaction(transactionParams, CALLER);
    }

    @StandardApiResponses
    @PostMapping(value = "unlock")
    public Integer postUnlockCon(@Valid ApiChargePointUnlock params) {
        // only one active task per charge box over api; to be discussed!
        if (activeTaskOnChargeBox(params.getChargeBoxId())) {
            var errMsg = String.format("Active task found on ChargeBox %s!",
                    params.getChargeBoxId()
            );
            throw new BadRequestException(errMsg);
        }

        var transactionParams = new UnlockConnectorParams();
        var chargePoint = chargePointRepository.getChargePointSelect(params.getChargeBoxId()).orElseThrow(
            () -> new BadRequestException(
                String.format("ChargeBox %s not found!", params.getChargeBoxId())
            )
        );
        transactionParams.setChargePointSelectList(Collections.singletonList(chargePoint));

        /* If a active transaction is found, don't unlock the connection. */
        var transactionId = transactionRepository.getActiveTransactionId(params.getChargeBoxId(),
                params.getConnectorId());
        if (transactionId.isPresent()) {
            var errMsg = String.format("Active transaction found for connector %s at ChargeBox %s!",
                    params.getConnectorId(),
                    params.getChargeBoxId()
            );
            throw new BadRequestException(errMsg);
        }

        transactionParams.setConnectorId(params.getConnectorId());
        return client.unlockConnector(transactionParams, CALLER);
    }
}
