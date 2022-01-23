/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.steve.ocpp.soap.ClientProvider;
import de.rwth.idsg.steve.ocpp.soap.ClientProviderWithCache;
import de.rwth.idsg.steve.ocpp.task.CancelReservationTask;
import de.rwth.idsg.steve.ocpp.task.ChangeAvailabilityTask;
import de.rwth.idsg.steve.ocpp.task.ChangeConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.ClearCacheTask;
import de.rwth.idsg.steve.ocpp.task.DataTransferTask;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.GetDiagnosticsTask;
import de.rwth.idsg.steve.ocpp.task.GetLocalListVersionTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStartTransactionTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStopTransactionTask;
import de.rwth.idsg.steve.ocpp.task.ReserveNowTask;
import de.rwth.idsg.steve.ocpp.task.ResetTask;
import de.rwth.idsg.steve.ocpp.task.SendLocalListTask;
import de.rwth.idsg.steve.ocpp.task.UnlockConnectorTask;
import de.rwth.idsg.steve.ocpp.task.UpdateFirmwareTask;
import de.rwth.idsg.steve.ocpp.ws.ChargePointServiceInvoker;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.pipeline.OutgoingCallPipeline;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import ocpp.cp._2012._06.ChargePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.03.2018
 */
@Service
public class ChargePointService15_InvokerImpl implements ChargePointService15_Invoker {

    private final ChargePointServiceInvoker wsHelper;
    private final ClientProviderWithCache<ChargePointService> soapHelper;

    @Autowired
    public ChargePointService15_InvokerImpl(OutgoingCallPipeline pipeline, Ocpp15WebSocketEndpoint endpoint, ClientProvider clientProvider) {
        this.wsHelper = new ChargePointServiceInvoker(pipeline, endpoint, Ocpp15TypeStore.INSTANCE);
        this.soapHelper = new ClientProviderWithCache<>(clientProvider);
    }

    @Override
    public void reset(ChargePointSelect cp, ResetTask task) {
        if (cp.isSoap()) {
            create(cp).resetAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void clearCache(ChargePointSelect cp, ClearCacheTask task) {
        if (cp.isSoap()) {
            create(cp).clearCacheAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsTask task) {
        if (cp.isSoap()) {
            create(cp).getDiagnosticsAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void updateFirmware(ChargePointSelect cp, UpdateFirmwareTask task) {
        if (cp.isSoap()) {
            create(cp).updateFirmwareAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void unlockConnector(ChargePointSelect cp, UnlockConnectorTask task) {
        if (cp.isSoap()) {
            create(cp).unlockConnectorAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));

        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityTask task) {
        if (cp.isSoap()) {
            create(cp).changeAvailabilityAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp, ChangeConfigurationTask task) {
        if (cp.isSoap()) {
            create(cp).changeConfigurationAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionTask task) {
        if (cp.isSoap()) {
            create(cp).remoteStartTransactionAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionTask task) {
        if (cp.isSoap()) {
            create(cp).remoteStopTransactionAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void dataTransfer(ChargePointSelect cp, DataTransferTask task) {
        if (cp.isSoap()) {
            create(cp).dataTransferAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void getConfiguration(ChargePointSelect cp, GetConfigurationTask task) {
        if (cp.isSoap()) {
            create(cp).getConfigurationAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionTask task) {
        if (cp.isSoap()) {
            create(cp).getLocalListVersionAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void sendLocalList(ChargePointSelect cp, SendLocalListTask task) {
        if (cp.isSoap()) {
            create(cp).sendLocalListAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void reserveNow(ChargePointSelect cp, ReserveNowTask task) {
        if (cp.isSoap()) {
            create(cp).reserveNowAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    @Override
    public void cancelReservation(ChargePointSelect cp, CancelReservationTask task) {
        if (cp.isSoap()) {
            create(cp).cancelReservationAsync(task.getOcpp15Request(), cp.getChargeBoxId(), task.getOcpp15Handler(cp.getChargeBoxId()));
        } else {
            runPipeline(cp, task);
        }
    }

    private void runPipeline(ChargePointSelect cp, CommunicationTask task) {
        wsHelper.runPipeline(cp, task);
    }

    private ChargePointService create(ChargePointSelect cp) {
        return soapHelper.createClient(ChargePointService.class, cp.getEndpointAddress());
    }
}
