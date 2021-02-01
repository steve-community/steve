package net.parkl.ocpp.service;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.OcppChargingProcessRepository;
import net.parkl.ocpp.repositories.TransactionStartRepository;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.util.AsyncWaiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@Transactional
public class ChargingProcessService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChargingProcessService.class);

    private final OcppChargingProcessRepository chargingProcessRepo;

    private final ConnectorRepository connectorRepo;

    private final AdvancedChargeBoxConfiguration specialConfig;

    private final TransactionStartRepository transactionStartRepository;

    public ChargingProcessService(OcppChargingProcessRepository chargingProcessRepo,
                                  ConnectorRepository connectorRepo,
                                  AdvancedChargeBoxConfiguration specialConfig,
                                  TransactionStartRepository transactionStartRepository) {
        this.chargingProcessRepo = chargingProcessRepo;
        this.connectorRepo = connectorRepo;
        this.specialConfig = specialConfig;
        this.transactionStartRepository = transactionStartRepository;
    }

    public OcppChargingProcess findOpenChargingProcessWithoutTransaction(String chargeBoxId, int connectorId) {
        Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (c == null) {
            throw new IllegalStateException("Invalid charge box id/connector id: " + chargeBoxId + "/" + connectorId);
        }
        return chargingProcessRepo.findByConnectorAndTransactionIsNullAndEndDateIsNull(c);
    }

    public OcppChargingProcess findOpenChargingProcess(String chargeBoxId, int connectorId) {
        Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (c == null) {
            throw new IllegalStateException("Invalid charge box id/connector id: " + chargeBoxId + "/" + connectorId);
        }
        return chargingProcessRepo.findByConnectorAndEndDateIsNull(c);
    }

    public OcppChargingProcess createChargingProcess(String chargeBoxId,
                                                     int connectorId,
                                                     String idTag,
                                                     String licensePlate,
                                                     Float limitKwh,
                                                     Integer limitMinute) {
        Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (c == null) {
            throw new IllegalStateException("Invalid charge box id/connector id: " + chargeBoxId + "/" + connectorId);
        }
        LOGGER.info("Creating OcppChargingProcess on {}/{} with id tag {} for: {}...", chargeBoxId, connectorId,
                idTag, licensePlate);
        OcppChargingProcess existing = chargingProcessRepo.findByConnectorAndTransactionIsNullAndEndDateIsNull(c);
        if (existing != null) {
            throw new IllegalStateException("Connector occupied: " + c.getConnectorId());
        }

        OcppChargingProcess p = new OcppChargingProcess();
        p.setOcppChargingProcessId(UUID.randomUUID().toString());
        p.setConnector(c);
        p.setLicensePlate(licensePlate);
        p.setOcppTag(idTag);
        p.setLimitKwh(limitKwh);
        p.setLimitMinute(limitMinute);
        return chargingProcessRepo.save(p);
    }

    public OcppChargingProcess findOcppChargingProcess(String processId) {
        return chargingProcessRepo.findById(processId).orElse(null);
    }

    public OcppChargingProcess stopChargingProcess(String processId) {
        OcppChargingProcess cp = chargingProcessRepo.findById(processId).
                orElseThrow(() -> new IllegalStateException("Invalid OcppChargingProcess id: " + processId));

        if (cp.getEndDate() != null) {
            throw new IllegalStateException("OcppChargingProcess already ended: " + processId);
        }

        cp.setEndDate(new Date());
        return chargingProcessRepo.save(cp);
    }


    public OcppChargingProcess stopRequested(String processId) {
        OcppChargingProcess cp = chargingProcessRepo.findById(processId).
                orElseThrow(() -> new IllegalStateException("Invalid OcppChargingProcess id: " + processId));

        if (cp.getStopRequestDate() != null) {
            LOGGER.warn("OcppChargingProcess stop already requested: " + processId);
            return cp;
        }

        cp.setStopRequestDate(new Date());
        return chargingProcessRepo.save(cp);
    }


    public OcppChargingProcess stopRequestCancelled(String processId) {
        OcppChargingProcess cp = chargingProcessRepo.findById(processId).
                orElseThrow(() -> new IllegalStateException("Invalid OcppChargingProcess id: " + processId));

        cp.setStopRequestDate(null);
        return chargingProcessRepo.save(cp);
    }

    public List<OcppChargingProcess> getActiveProcessesByChargeBox(String chargeBoxId) {
        return chargingProcessRepo.findActiveByChargeBoxId(chargeBoxId);
    }

    public List<OcppChargingProcess> findOpenChargingProcessesWithoutTransaction() {
        return chargingProcessRepo.findAllByTransactionIsNullAndEndDateIsNull();
    }


    public List<OcppChargingProcess> findOpenChargingProcessesWithLimitKwh() {
        return chargingProcessRepo.findAllByTransactionIsNotNullAndLimitKwhIsNotNullAndEndDateIsNull();
    }

    public List<OcppChargingProcess> findOpenChargingProcessesWithLimitMinute() {
        return chargingProcessRepo.findAllByTransactionIsNotNullAndLimitMinuteIsNotNullAndEndDateIsNull();
    }

    public OcppChargingProcess findOpenProcessForRfidTag(String rfidTag, int connectorId, String chargeBoxId) {
        Connector connector = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (connector == null) {
            throw new IllegalStateException("Invalid charge box id/connector id: " + chargeBoxId + "/" + connectorId);
        }
        return chargingProcessRepo.findByOcppTagAndConnectorAndEndDateIsNull(rfidTag, connector);
    }

    public OcppChargingProcess findByTransactionId(int transactionId) {
        TransactionStart transaction = transactionStartRepository
                .findById(transactionId).orElseThrow(() -> new IllegalStateException("Invalid transaction id"));
        return chargingProcessRepo.findByTransaction(transaction);
    }

    public OcppChargingProcess fetchChargingProcess(int connectorId, String chargeBoxId, int timeout) {
        if (specialConfig.waitingForChargingProcessEnabled(chargeBoxId)) {
            OcppChargingProcess chargingProcess =
                    waitingForChargingProcessOnConnector(chargeBoxId,
                            connectorId,
                            timeout);
            if (chargingProcess == null) {
                log.error("Charging process not found without transaction: {}/{}", chargeBoxId, connectorId);
                throw new IllegalStateException("Charging process not found without transaction: " + connectorId);
            }
            return chargingProcess;
        }
        return findOpenChargingProcessWithoutTransaction(chargeBoxId, connectorId);
    }

    private OcppChargingProcess waitingForChargingProcessOnConnector(String chargeBoxId, int connectorId, int timeout) {
        Connector conn = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (conn == null) {
            throw new IllegalArgumentException("Connector not found: " + chargeBoxId + "/" + connectorId);
        }
        AsyncWaiter<OcppChargingProcess> waiter = new AsyncWaiter<>(timeout);
        waiter.setDelayMs(0);
        waiter.setIntervalMs(200);
        return waiter.waitFor(() -> chargingProcessRepo.findByConnectorAndTransactionIsNullAndEndDateIsNull(conn));
    }

}
