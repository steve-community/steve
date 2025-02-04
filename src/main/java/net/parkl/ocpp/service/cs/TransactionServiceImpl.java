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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm.QueryPeriodType;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.*;
import net.parkl.ocpp.repositories.*;
import net.parkl.ocpp.service.ChargingProcessService;
import net.parkl.ocpp.service.ESPNotificationService;
import net.parkl.ocpp.util.AsyncWaiter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.Writer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static de.rwth.idsg.steve.web.dto.TransactionQueryForm.QueryType;
import static net.parkl.ocpp.service.cs.converter.TransactionDtoConverter.toTransactionDto;
import static net.parkl.ocpp.service.cs.factory.TransactionFactory.*;
import static net.parkl.ocpp.util.ListTransform.transformToMap;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepo;
    @Autowired
    private TransactionStartRepository transactionStartRepo;
    @Autowired
    private TransactionStopRepository transactionStopRepo;
    @Autowired
    private TransactionStopFailedRepository transactionStopFailedRepo;
    @Autowired
    private TransactionCriteriaRepository transactionCriteriaRepository;

    @Autowired
    private ConnectorMeterValueService connectorMeterValueService;

    @Autowired
    private ChargingProcessService chargingProcessService;
    @Autowired
    private ConnectorService connectorService;
    @Autowired
    private ChargePointService chargePointService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ESPNotificationService espNotificationService;


    @Override
    public List<Integer> getActiveTransactionIds(String chargeBoxId) {
        return transactionRepo.findActiveTransactionIds(chargeBoxId);
    }

    @Override
    public List<de.rwth.idsg.steve.repository.dto.Transaction> getTransactions(TransactionQueryForm form) {
        List<Transaction> list = transactionCriteriaRepository.getInternal(form);



        Map<String, OcppChargeBox> boxMap = transformToMap(chargePointService.findAllChargePoints(),
                OcppChargeBox::getChargeBoxId);

        List<de.rwth.idsg.steve.repository.dto.Transaction> ret = new ArrayList<>();
        for (Transaction t : list) {
            OcppChargeBox box = boxMap.get(t.getConnector().getChargeBoxId());
            if (box == null) {
                throw new IllegalStateException("Invalid charge box id: " + t.getConnector().getChargeBoxId());
            }

            ret.add(toTransactionDto(t, box));
        }
        return ret;
    }


    @Override
    public TransactionDetails getDetails(int transactionPk, boolean firstArrivingMeterValueIfMultiple) {
        TransactionQueryForm form = new TransactionQueryForm();
        form.setTransactionPk(transactionPk);
        form.setType(QueryType.ALL);
        form.setPeriodType(QueryPeriodType.ALL);

        List<Transaction> transactions = transactionCriteriaRepository.getInternal(form);

        if (transactions == null || transactions.isEmpty()) {
            throw new SteveException("There is no transaction with id '%s'", transactionPk);
        }

        Transaction transaction = transactions.get(0);

        LocalDateTime startTimestamp = transaction.getStartTimestamp();
        LocalDateTime stopTimestamp = transaction.getStopTimestamp();
        String stopValue = transaction.getStopValue();
        String chargeBoxId = transaction.getConnector().getChargeBoxId();
        int connectorId = transaction.getConnector().getConnectorId();

        OcppChargeBox box = chargePointService.findByChargeBoxId(transaction.getConnector().getChargeBoxId());
        if (box == null) {
            throw new IllegalStateException("Invalid charge box id: " + transaction.getConnector().getChargeBoxId());
        }

        TransactionStart nextTransaction = null;

        // Case 1: Ideal and most accurate case. Station sends meter values with transaction id set.
        List<ConnectorMeterValueDetail> cmv1 = connectorMeterValueService.findByTransactionPk(transaction.getTransactionPk());

        // Case 2: Fall back to filtering according to time windows
        List<ConnectorMeterValueDetail> cmv2;
        if (stopTimestamp == null && stopValue == null) {
            // https://github.com/RWTH-i5-IDSG/steve/issues/97
            //
            // handle "zombie" transaction, for which we did not receive any StopTransaction. if we do not handle it,
            // meter values for all subsequent transactions at this chargebox and connector will be falsely attributed
            // to this zombie transaction.
            //
            // "what is the subsequent transaction at the same chargebox and connector?"

            nextTransaction = transactionStartRepo.findNextTransactions(chargeBoxId, connectorId, startTimestamp,
                    PageRequest.of(0, 1)).stream().findFirst().orElse(null);
            if (nextTransaction == null) {
                // the last active transaction
                cmv2 = connectorMeterValueService.findByChargeBoxIdAndConnectorIdAfter(chargeBoxId,
                        connectorId,
                        startTimestamp);
            } else {
                cmv2 = connectorMeterValueService.findByChargeBoxIdAndConnectorIdBetween(chargeBoxId,
                        connectorId,
                        startTimestamp,
                        nextTransaction.getStartTimestamp());
            }
        } else {
            // finished transaction
            cmv2 = connectorMeterValueService.findByChargeBoxIdAndConnectorIdBetween(chargeBoxId,
                    connectorId,
                    startTimestamp,
                    stopTimestamp);
        }


        // Actually, either case 1 applies or 2. If we retrieved values using 1, case 2 is should not be
        // executed (best case). In worst case (1 returns empty list and we fall back to case 2) though,
        // we make two db calls. Alternatively, we can pass both queries in one go, and make the db work.
        //
        // UNION removes all duplicate records
        //
        List<ConnectorMeterValueDetail> union = new ArrayList<>();
        union.addAll(cmv1);
        union.addAll(cmv2);

        // -------------------------------------------------------------------------
        // Step 3: Charging station might send meter vales at fixed intervals (e.g.
        // every 15 min) regardless of the fact that connector's meter value did not
        // change (e.g. vehicle is fully charged, but cable is still connected). This
        // yields multiple entries in db with the same value but different timestamp.
        // We are only interested in the first (or last) arriving entry.
        // -------------------------------------------------------------------------


        union.sort((o1, o2) -> {
            if (firstArrivingMeterValueIfMultiple) {
                return o1.getValueTimestamp().compareTo(o2.getValueTimestamp());
            } else {
                return o2.getValueTimestamp().compareTo(o1.getValueTimestamp());
            }

        });


        List<TransactionDetails.MeterValues> values = new ArrayList<>();
        for (ConnectorMeterValueDetail v : union) {
            values.add(TransactionDetails.MeterValues.builder()
                    .valueTimestamp(new DateTime(v.getValueTimestamp()))
                    .value(v.getValue())
                    .readingContext(v.getReadingContext())
                    .format(v.getFormat())
                    .measurand(v.getMeasurand())
                    .location(v.getLocation())
                    .unit(v.getUnit())
                    .phase(v.getPhase())
                    .build());
        }

        return new TransactionDetails(toTransactionDto(transaction, box), values, nextTransaction);
    }

    @Override
    public void writeTransactionsCSV(TransactionQueryForm form, Writer writer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getChargeBoxIdsOfActiveTransactions(String idTag) {
        return transactionRepo.findActiveChargeBoxIdsByOcppTag(idTag);
    }

    @Override
    public Optional<Transaction> findTransaction(int transactionPk) {
        return transactionRepo.findById(transactionPk);
    }

    @Override
    public Optional<TransactionStart> findTransactionStart(int transactionPk) {
        return transactionStartRepo.findById(transactionPk);
    }

    @Override
    public long getActiveTransactionCountByIdTag(String idTag) {
        return transactionRepo.countActiveTransactionsByIdTag(idTag);
    }

    @Override
    public Integer insertTransaction(InsertTransactionParams params) {
        log.info("Starting transaction: chargeBoxId={},connectorId={},idTag={}...",
                params.getChargeBoxId(), params.getConnectorId(), params.getIdTag());
        Connector connector = connectorService.createConnectorIfNotExists(params.getChargeBoxId(),
                params.getConnectorId());

        TransactionStart existing = transactionStartRepo.findByConnectorAndIdTagAndStartValues(
                connector, params.getIdTag(),
                params.getStartTimestamp() != null ? params.getStartTimestamp().toDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime() : null,
                params.getStartMeterValue());

        if (existing != null) {
            log.warn("Transaction already exists: {}", existing.getTransactionPk());
            return existing.getTransactionPk();
        }

        TransactionStart transactionStart = transactionStartRepo.save(createTransactionStart(connector, params));

        log.info("Transaction saved id={}, querying charging suitable process for connector: {}",
                transactionStart.getTransactionPk(),
                connector.getConnectorId());
        OcppChargingProcess chargingProcess = chargingProcessService.fetchChargingProcess(params.getConnectorId(),
                params.getChargeBoxId(),
                new AsyncWaiter<>(90000));
        if (chargingProcess != null) {
            log.info("Setting transaction on connector {} to process: {}...",
                    connector.getConnectorId(),
                    chargingProcess.getOcppChargingProcessId());
            chargingProcess.setTransactionStart(transactionStart);
            chargingProcessService.save(chargingProcess);
        } else {
            log.warn("No active charging process found without transaction for connector: {}", connector.getConnectorId());
        }

        if (params.getReservationId()!=null) {
            reservationService.markReservationAsUsed(transactionStart, params.getReservationId(), params.getChargeBoxId());
        }

        if (chargePointService.shouldInsertConnectorStatusAfterTransactionMsg(params.getChargeBoxId())) {
            connectorService.createConnectorStatus(transactionStart.getConnector(),
                    params.getStartTimestamp(),
                    params.getStatusUpdate());
        }

        return transactionStart.getTransactionPk();
    }

    @Override
    public void updateTransaction(UpdateTransactionParams p) {
        TransactionStart transactionStart=null;
        try {
            int transactionId = p.getTransactionId();
            log.info("Update transaction received from charge box {} with transaction id {}", p.getChargeBoxId(), transactionId);
            Transaction transaction = transactionRepo.findById(transactionId).orElse(null);

            if (transaction == null){
                log.warn("Invalid transaction id {}", transactionId);
                return;
            }

            TransactionStop transactionStop = createTransactionStop(p);
            OcppChargingProcess chargingProcess = chargingProcessService.findByTransactionId(transactionId);

            if (chargingProcess == null){
                log.warn("Charging process not found for transaction {}", transactionId);
                return;
            }
            if (transactionStopRepo.countByTransactionId(transactionId)>0) {
                log.warn("Transaction {} already stopped for charging process: {}", transactionId,
                        chargingProcess.getOcppChargingProcessId());
                return;

            }

            transactionStart = chargingProcess.getTransactionStart();
            transactionStop.setTransaction(transactionStart);
            transactionStopRepo.save(transactionStop);

            if (p.getStopTimestamp() != null) {
                log.info("Transaction update: {} with end date: {}", transactionId, p.getStopTimestamp());
                String chargingProcessId = chargingProcess.getOcppChargingProcessId();
                log.info("Ending charging process on transaction update: {} with end date: {}", chargingProcessId, p.getStopTimestamp());
                chargingProcess.setEndDate(p.getStopTimestamp().toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

                chargingProcess = chargingProcessService.save(chargingProcess);
            }

            if (chargePointService.shouldInsertConnectorStatusAfterTransactionMsg(p.getChargeBoxId())) {
                transactionStart =
                        transactionStartRepo.findById(transactionId)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction id: " + transactionId));

                connectorService.createConnectorStatus(
                        transactionStart.getConnector(),
                        p.getStopTimestamp(),
                        p.getStatusUpdate()
                );
            }

            if (transaction.vehicleUnplugged() || chargingProcess.stoppedExternally()) {
                if (chargingProcess.stoppedExternally()) {
                    espNotificationService.notifyAboutChargingStopped(chargingProcess);
                } else {
                    String startValue = chargingProcess.getTransactionStart().getStartValue();
                    String stopValue = transactionStop.getStopValue();
                    espNotificationService.notifyAboutConsumptionUpdated(chargingProcess, startValue, stopValue);
                }
            }
        } catch (Exception e) {
            log.error("Transaction save failed", e);
            if (transactionStart != null) {
                TransactionStopFailed stopFailed = createTransactionStopFailed(p, e);
                stopFailed.setTransaction(transactionStart);
                transactionStopFailedRepo.save(stopFailed);
            }
        }
    }

}
