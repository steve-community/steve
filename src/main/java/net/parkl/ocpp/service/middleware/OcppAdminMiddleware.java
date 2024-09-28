package net.parkl.ocpp.service.middleware;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.*;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.*;
import net.parkl.ocpp.module.esp.model.ESPActiveTransaction;
import net.parkl.ocpp.module.esp.model.ESPClosedTransaction;
import net.parkl.ocpp.module.esp.model.ESPClosedTransactions;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.OcppChargingProcessRepository;
import net.parkl.ocpp.repositories.TransactionRepository;
import net.parkl.ocpp.repositories.TransactionStopRepository;
import net.parkl.ocpp.service.cs.TransactionService;
import net.parkl.ocpp.util.ListTransform;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

@Service
@Slf4j
public class OcppAdminMiddleware extends AbstractOcppMiddleware {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private OcppChargingProcessRepository ocppChargingProcessRepository;

    public void unlockConnector(String chargeBoxId, String chargerId) {
        log.info("Unlock connector request for {}-{}...", chargeBoxId, chargerId);
        OcppChargeBox chargeBox = getOcppChargeBox(chargeBoxId);

        ChargePointSelect c = getChargePointSelect(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());

        UnlockConnectorParams params = new UnlockConnectorParams();
        params.setChargePointSelectList(singletonList(c));
        params.setConnectorId(Integer.parseInt(chargerId));
        int taskId = sendUnlockConnector(params, chargeBox.getOcppProtocol());

        RequestResult result = waitForResult(chargeBoxId, taskId);
        processGenericResult("Unlock connector", chargeBoxId, result);
    }

    private int sendUnlockConnector(UnlockConnectorParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_12_SOAP:
            case V_12_JSON:
                return client12.unlockConnector(params);
            case V_15_SOAP:
            case V_15_JSON:
                return client15.unlockConnector(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.unlockConnector(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    private int sendTriggerMessage(TriggerMessageParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_12_SOAP:
            case V_12_JSON:
            case V_15_SOAP:
            case V_15_JSON:
                throw new UnsupportedOperationException("Trigger message not supported for OCPP 1.2 and 1.5");
            case V_16_SOAP:
            case V_16_JSON:
                return client16.triggerMessage(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    public void resetChargeBox(String chargeBoxId, boolean soft) {
        log.info("Reset request for {} (soft={}}...", chargeBoxId, soft);
        OcppChargeBox chargeBox = getOcppChargeBox(chargeBoxId);

        ChargePointSelect c = getChargePointSelect(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());

        ResetParams params = new ResetParams();
        params.setChargePointSelectList(singletonList(c));
        params.setResetType(soft ? ResetType.SOFT : ResetType.HARD);
        int taskId = sendResetRequest(params, chargeBox.getOcppProtocol());

        RequestResult result = waitForResult(chargeBoxId, taskId);
        processGenericResult("Reset request", chargeBoxId, result);
    }

    private int sendResetRequest(ResetParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_12_SOAP:
            case V_12_JSON:
                return client12.reset(params);
            case V_15_SOAP:
            case V_15_JSON:
                return client15.reset(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.reset(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    public List<ESPActiveTransaction> getConnectorActiveTransactions(String chargeBoxId, String chargerId) {
        Connector connector = connectorRepository.findByChargeBoxIdAndConnectorId(chargeBoxId, Integer.parseInt(chargerId));
        if (connector == null) {
            log.error("Invalid connector id: {}-{}", chargeBoxId, chargerId);
            throw new IllegalArgumentException("Invalid connector ID: " + chargeBoxId + "-" + chargerId);
        }
        List<Transaction> transactions = transactionRepository.findByConnectorAndStopTimestampIsNullOrderByStartTimestampDesc(connector);

        List<Integer> transactionIds = new ArrayList<>();
        Map<Integer, OcppChargingProcess> processMap = new HashMap<>();
        transactions.forEach(t -> transactionIds.add(t.getTransactionPk()));

        if (!transactionIds.isEmpty()) {
            List<OcppChargingProcess> processes = ocppChargingProcessRepository.findByTransactionIdIn(transactionIds);
            processes.forEach(p -> processMap.put(p.getTransactionStart().getTransactionPk(), p));
        }

        return toActiveTransactions(transactions, processMap);
    }

    private List<ESPActiveTransaction> toActiveTransactions(List<Transaction> transactions, Map<Integer, OcppChargingProcess> processMap) {
        List<ESPActiveTransaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            OcppChargingProcess process = processMap.get(transaction.getTransactionPk());
            String processId = process != null ? process.getOcppChargingProcessId() : null;
            result.add(ESPActiveTransaction.builder()
                    .ocppChargingProcessId(processId)
                    .ocppTag(transaction.getOcppTag())
                    .startDate(transaction.getStartTimestamp())
                    .startValue(transaction.getStartValue())
                    .build());
        }
        return result;
    }

    public ESPClosedTransactions getConnectorClosedTransactions(String chargeBoxId, String chargerId, int page, int size) {
        Connector connector = connectorRepository.findByChargeBoxIdAndConnectorId(chargeBoxId, Integer.parseInt(chargerId));
        if (connector == null) {
            log.error("Invalid connector id: {}-{}", chargeBoxId, chargerId);
            throw new IllegalArgumentException("Invalid connector ID: " + chargeBoxId + "-" + chargerId);
        }

        Page<Transaction> transactions = transactionRepository.findByConnectorAndStopTimestampIsNotNullOrderByStartTimestampDesc(connector,
                PageRequest.of(page, size));

        List<Integer> transactionIds = new ArrayList<>();
        Map<Integer, OcppChargingProcess> processMap = new HashMap<>();
        transactions.getContent().forEach(t -> transactionIds.add(t.getTransactionPk()));

        if (!transactionIds.isEmpty()) {
            List<OcppChargingProcess> processes = ocppChargingProcessRepository.findByTransactionIdIn(transactionIds);
            processes.forEach(p -> processMap.put(p.getTransactionStart().getTransactionPk(), p));
        }

        return ESPClosedTransactions.builder()
                .totalPages(transactions.getTotalPages())
                .totalElements(transactions.getTotalElements())
                .transactions(toClosedTransactions(transactions.getContent(), processMap))
                .build();
    }

    private List<ESPClosedTransaction> toClosedTransactions(List<Transaction> transactions, Map<Integer,OcppChargingProcess> processMap) {
        List<ESPClosedTransaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            OcppChargingProcess process = processMap.get(transaction.getTransactionPk());
            String processId = process != null ? process.getOcppChargingProcessId() : null;
            result.add(ESPClosedTransaction.builder()
                    .ocppChargingProcessId(processId)
                    .ocppTag(transaction.getOcppTag())
                    .startDate(transaction.getStartTimestamp())
                    .startValue(transaction.getStartValue())
                    .endDate(transaction.getStopTimestamp())
                    .stopReason(transaction.getStopReason())
                    .stopValue(transaction.getStopValue())
                    .build());
        }
        return result;
    }

    public void triggerMessage(String chargeBoxId, String chargerId, TriggerMessageEnum message) {
        log.info("Trigger message request for {}-{}...", chargeBoxId, chargerId);
        ;
        OcppChargeBox chargeBox = getOcppChargeBox(chargeBoxId);

        ChargePointSelect c = getChargePointSelect(chargeBoxId, chargeBox.getOcppProtocol());

        TriggerMessageParams params = new TriggerMessageParams();
        params.setChargePointSelectList(singletonList(c));
        params.setConnectorId(Integer.parseInt(chargerId));
        params.setTriggerMessage(message);
        int taskId = sendTriggerMessage(params, chargeBox.getOcppProtocol());

        RequestResult result = waitForResult(chargeBoxId, taskId);
        processGenericResult("Trigger message", chargeBoxId, result);
    }

    @NotNull
    private ChargePointSelect getChargePointSelect(String chargeBoxId, String protocol) {
        ChargePointSelect c = getChargePoint(chargeBoxId, protocol);
        if (c == null) {
            log.error("Invalid charge point id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);

        }
        return c;
    }


    private OcppChargeBox getOcppChargeBox(String chargeBoxId) {
        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox == null) {
            log.error("Invalid charge box id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);
        }
        return chargeBox;
    }
}
