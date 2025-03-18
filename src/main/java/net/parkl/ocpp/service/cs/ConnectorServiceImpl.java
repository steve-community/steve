/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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
package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.TransactionStatusUpdate;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.ConnectorLastStatus;
import net.parkl.ocpp.entities.ConnectorStatus;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.repositories.ConnectorLastStatusRepository;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.ConnectorStatusRepository;
import net.parkl.ocpp.repositories.OcppChargingProcessRepository;
import net.parkl.ocpp.service.OcppConstants;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.service.middleware.OcppChargingMiddleware;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@Slf4j
public class ConnectorServiceImpl implements ConnectorService {
    @Autowired
    private OcppChargingProcessRepository chargingProcessRepo;
    @Autowired
    private ConnectorRepository connectorRepo;
    @Autowired
    private ConnectorStatusRepository connectorStatusRepo;
    @Autowired
    private ConnectorLastStatusRepository connectorLastStatusRepo;
    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor executor;
    @Autowired
    private OcppChargingMiddleware chargingMiddleware;
    @Autowired
    private AdvancedChargeBoxConfiguration advancedChargeBoxConfiguration;

    @Override
    @Transactional
    public void insertConnectorStatus(InsertConnectorStatusParams p) {
        Connector connector = createConnectorIfNotExists(p.getChargeBoxId(), p.getConnectorId());

        ConnectorStatus s = new ConnectorStatus();
        s.setConnector(connector);
        if (p.getTimestamp() != null) {
            s.setStatusTimestamp(p.getTimestamp().toDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        s.setStatus(p.getStatus());
        s.setErrorCode(p.getErrorCode());
        s.setErrorInfo(p.getErrorInfo());
        s.setVendorId(p.getVendorId());
        s.setVendorErrorCode(p.getVendorErrorCode());

        connectorStatusRepo.save(s);

        ConnectorLastStatus ls = new ConnectorLastStatus();
        ls.setConnectorPk(connector.getConnectorPk());
        if (p.getTimestamp() != null) {
            ls.setStatusTimestamp(p.getTimestamp().toDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        } else {
            ls.setStatusTimestamp(LocalDateTime.now());
        }
        ls.setStatus(p.getStatus());
        ls.setErrorCode(p.getErrorCode());
        ls.setErrorInfo(p.getErrorInfo());
        ls.setVendorId(p.getVendorId());
        ls.setVendorErrorCode(p.getVendorErrorCode());
        ls.setChargeBoxId(p.getChargeBoxId());
        ls.setConnectorId(p.getConnectorId());

        connectorLastStatusRepo.save(ls);

        OcppChargingProcess savedOcppChargingProcess = handleChargingProcess(connector, s);

        log.debug("Stored a new connector status for {}/{}.", p.getChargeBoxId(), p.getConnectorId());

        if (savedOcppChargingProcess != null) {
            final OcppChargingProcess pr = savedOcppChargingProcess;
            executor.execute(() -> {
                log.info("Notifying Parkl about closing charging process with id: {}...", pr.getOcppChargingProcessId());
                if (pr.getErrorCode() != null) {
                    String errorCode = ChargePointErrorCode.fromValue(pr.getErrorCode()).toString();
                    chargingMiddleware.stopChargingExternal(
                            pr,
                            errorCode);
                } else {
                    chargingMiddleware.stopChargingExternal(
                            pr,
                            OcppConstants.REASON_VEHICLE_NOT_CONNECTED);
                }
            });
        }
    }

    private OcppChargingProcess handleChargingProcess(Connector connector, ConnectorStatus s) {
        OcppChargingProcess process = chargingProcessRepo.findByConnectorAndEndDateIsNull(connector);

        if (process == null) {
            return null;
        }

        if (s.getStatus().equals(ChargePointStatus.AVAILABLE.value())) {
            if (advancedChargeBoxConfiguration
                    .ignoreConnectorAvailableUntilStopTransaction(process.getConnector().getChargeBoxId())) {
                log.info("Ignore connector available state until StopTransaction event is turned on for chargeBoxId: {}",
                        process.getConnector().getChargeBoxId());
                return null;
            }
            log.info("Ending charging process on available connector status: {}", process.getOcppChargingProcessId());
            process.setEndDate(LocalDateTime.now());
            return chargingProcessRepo.save(process);
        } else if (s.getStatus().equals(ChargePointStatus.FAULTED.value())
                || s.getStatus().equals(ChargePointStatus.UNAVAILABLE.value())) {
            if (s.getErrorCode() != null && !s.getErrorCode().equals(ChargePointErrorCode.NO_ERROR.value())) {
                log.info("Saving connector status error to charging process: {} [error={}]...", process.getOcppChargingProcessId(),
                        s.getErrorCode());
                process.setErrorCode(s.getErrorCode());
                return chargingProcessRepo.save(process);
            }
        }
        return null;
    }

    @Override
    public Connector createConnectorIfNotExists(String chargeBoxId, int connectorId) {
        Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (c == null) {
            c = new Connector();
            c.setChargeBoxId(chargeBoxId);
            c.setConnectorId(connectorId);
            c = connectorRepo.save(c);
        }
        return c;
    }

    @Override
    public Optional<Connector> findById(int connectorId) {
        return connectorRepo.findById(connectorId);
    }

    @Override
    @Transactional
    public void createConnectorStatus(Connector connector, DateTime startTimestamp, TransactionStatusUpdate statusUpdate) {
        ConnectorStatus s = new ConnectorStatus();
        s.setConnector(connector);
        if (startTimestamp != null) {
            s.setStatusTimestamp(startTimestamp.toDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        s.setStatus(statusUpdate.getStatus());
        s.setErrorCode(statusUpdate.getErrorCode());

        connectorStatusRepo.save(s);

        ConnectorLastStatus ls = new ConnectorLastStatus();
        ls.setConnectorPk(connector.getConnectorPk());
        if (startTimestamp != null) {
            ls.setStatusTimestamp(startTimestamp.toDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        } else {
            ls.setStatusTimestamp(LocalDateTime.now());
        }
        ls.setStatus(statusUpdate.getStatus());
        ls.setErrorCode(statusUpdate.getErrorCode());
        ls.setChargeBoxId(connector.getChargeBoxId());
        ls.setConnectorId(connector.getConnectorId());

        connectorLastStatusRepo.save(ls);
    }
}
