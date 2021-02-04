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

    private final AdvancedChargeBoxConfiguration advancedConfig;

    private final TransactionStartRepository transactionStartRepository;

    public ChargingProcessService(OcppChargingProcessRepository chargingProcessRepo,
                                  ConnectorRepository connectorRepo,
                                  AdvancedChargeBoxConfiguration advancedConfig,
                                  TransactionStartRepository transactionStartRepository) {
        this.chargingProcessRepo = chargingProcessRepo;
        this.connectorRepo = connectorRepo;
        this.advancedConfig = advancedConfig;
        this.transactionStartRepository = transactionStartRepository;
    }

    public OcppChargingProcess findOpenChargingProcessWithoutTransaction(String chargeBoxId, int connectorId) {
        Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (c == null) {
            throw new IllegalStateException("Invalid charge box id/connector id: " + chargeBoxId + "/" + connectorId);
        }
        return chargingProcessRepo.findByConnectorAndTransactionStartIsNullAndEndDateIsNull(c);
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
        OcppChargingProcess existing = chargingProcessRepo.findByConnectorAndTransactionStartIsNullAndEndDateIsNull(c);
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


    public void stopRequestCancelled(String processId) {
        OcppChargingProcess cp = chargingProcessRepo.findById(processId).
                orElseThrow(() -> new IllegalStateException("Invalid OcppChargingProcess id: " + processId));

        cp.setStopRequestDate(null);
        chargingProcessRepo.save(cp);
    }

    public List<OcppChargingProcess> getActiveProcessesByChargeBox(String chargeBoxId) {
        return chargingProcessRepo.findActiveByChargeBoxId(chargeBoxId);
    }

    public List<OcppChargingProcess> findOpenChargingProcessesWithoutTransaction() {
        return chargingProcessRepo.findAllByTransactionStartIsNullAndEndDateIsNull();
    }


    public List<OcppChargingProcess> findOpenChargingProcessesWithLimitKwh() {
        return chargingProcessRepo.findAllByTransactionStartIsNotNullAndLimitKwhIsNotNullAndEndDateIsNull();
    }

    public List<OcppChargingProcess> findOpenChargingProcessesWithLimitMinute() {
        return chargingProcessRepo.findAllByTransactionStartIsNotNullAndLimitMinuteIsNotNullAndEndDateIsNull();
    }

    public OcppChargingProcess findByOcppTagAndConnectorAndEndDateIsNullAndTransactionIsNotNull (String rfidTag,
                                                                                                 int connectorId,
                                                                                                 String chargeBoxId) {

        Connector connector = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (connector == null) {
            throw new IllegalStateException("Invalid charge box id/connector id: " + chargeBoxId + "/" + connectorId);
        }
        return chargingProcessRepo.findByOcppTagAndConnectorAndEndDateIsNullAndTransactionStartIsNotNull(rfidTag, connector);
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
        return chargingProcessRepo.findByTransactionStart(transaction);
    }

    public OcppChargingProcess save(OcppChargingProcess process) {
        return chargingProcessRepo.save(process);
    }

    public OcppChargingProcess fetchChargingProcess(int connectorId, String chargeBoxId, AsyncWaiter<OcppChargingProcess> waiter) {
        if (advancedConfig.waitingForChargingProcessEnabled(chargeBoxId)) {
            OcppChargingProcess chargingProcess =
                    waitingForChargingProcessOnConnector(chargeBoxId,
                            connectorId,
                            waiter);
            if (chargingProcess == null) {
                log.error("Charging process not found without transaction: {}/{}", chargeBoxId, connectorId);
                throw new IllegalStateException("Charging process not found without transaction: " + connectorId);
            }
            return chargingProcess;
        }
        return findOpenChargingProcessWithoutTransaction(chargeBoxId, connectorId);
    }

    private OcppChargingProcess waitingForChargingProcessOnConnector(String chargeBoxId, int connectorId, AsyncWaiter<OcppChargingProcess> waiter) {
        Connector conn = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (conn == null) {
            throw new IllegalArgumentException("Connector not found: " + chargeBoxId + "/" + connectorId);
        }
        waiter.setDelayMs(0);
        waiter.setIntervalMs(200);
        return waiter.waitFor(() -> chargingProcessRepo.findByConnectorAndTransactionStartIsNullAndEndDateIsNull(conn));
    }

}
