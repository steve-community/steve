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
import net.parkl.stevep.util.ListTransform;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Writer;
import java.util.*;

import static de.rwth.idsg.steve.web.dto.TransactionQueryForm.QueryType;
import static net.parkl.ocpp.service.cs.converter.TransactionDtoConverter.toTransactionDto;
import static net.parkl.ocpp.service.cs.factory.TransactionFactory.*;

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
    private OcppIdTagService ocppIdTagService;
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


        Map<String, OcppTag> tagMap = ListTransform.transformToMap(ocppIdTagService.findTags(),
                OcppTag::getIdTag);

        Map<String, OcppChargeBox> boxMap = ListTransform.transformToMap(chargePointService.findAllChargePoints(),
                OcppChargeBox::getChargeBoxId);

        List<de.rwth.idsg.steve.repository.dto.Transaction> ret = new ArrayList<>();
        for (Transaction t : list) {
            OcppChargeBox box = boxMap.get(t.getConnector().getChargeBoxId());
            if (box == null) {
                throw new IllegalStateException("Invalid charge box id: " + t.getConnector().getChargeBoxId());
            }

            OcppTag tag = tagMap.get(t.getOcppTag());
            if (tag == null) {
                throw new IllegalStateException("Invalid id tag: " + t.getOcppTag());
            }
            ret.add(toTransactionDto(t, box, tag));
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

        Date startTimestamp = transaction.getStartTimestamp();
        Date stopTimestamp = transaction.getStopTimestamp();
        String stopValue = transaction.getStopValue();
        String chargeBoxId = transaction.getConnector().getChargeBoxId();
        int connectorId = transaction.getConnector().getConnectorId();

        OcppTag tag = ocppIdTagService.getRecord(transaction.getOcppTag());
        if (tag == null) {
            throw new IllegalStateException("Invalid id tag: " + transaction.getOcppTag());
        }

        OcppChargeBox box = chargePointService.findByChargeBoxId(transaction.getConnector().getChargeBoxId());
        if (box == null) {
            throw new IllegalStateException("Invalid charge box id: " + transaction.getConnector().getChargeBoxId());
        }

        TransactionStart nextTransaction = null;

        // Case 1: Ideal and most accurate case. Station sends meter values with transaction id set.
        List<ConnectorMeterValue> cmv1 = connectorMeterValueService.findByTransactionPk(transaction.getTransactionPk());

        // Case 2: Fall back to filtering according to time windows
        List<ConnectorMeterValue> cmv2;
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
        List<ConnectorMeterValue> union = new ArrayList<>();
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
        for (ConnectorMeterValue v : union) {
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

        return new TransactionDetails(toTransactionDto(transaction, box, tag), values, nextTransaction);
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
    @Transactional
    public Integer insertTransaction(InsertTransactionParams params) {
        log.info("Starting transaction: chargeBoxId={},connectorId={},idTag={}...",
                params.getChargeBoxId(), params.getConnectorId(), params.getIdTag());
        Connector connector = connectorService.createConnectorIfNotExists(params.getChargeBoxId(),
                params.getConnectorId());

        ocppIdTagService.createTagWithoutActiveTransactionIfNotExists(params.getIdTag());

        TransactionStart existing = transactionStartRepo.findByConnectorAndIdTagAndStartValues(
                connector, params.getIdTag(),
                params.getStartTimestamp() != null ? params.getStartTimestamp().toDate() : null,
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
                new AsyncWaiter<>(2000));
        if (chargingProcess != null) {
            log.info("Setting transaction on connector {} to process: {}...",
                    connector.getConnectorId(),
                    chargingProcess.getOcppChargingProcessId());
            chargingProcess.setTransaction(transactionStart);
            chargingProcessService.save(chargingProcess);
        } else {
            log.warn("No active charging process found without transaction for connector: {}", connector.getConnectorId());
        }

        if (params.hasReservation()) {
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
    @Transactional
    public void updateTransaction(UpdateTransactionParams p) {
        Transaction transaction = transactionRepo.findById(p.getTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction id: " + p.getTransactionId()));

        try {
            TransactionStop stop = createTransactionStop(p);

            OcppChargingProcess chargingProcess = updateChargingProcess(stop, p);
            if (chargePointService.shouldInsertConnectorStatusAfterTransactionMsg(p.getChargeBoxId())) {
                TransactionStart transactionStart =
                        transactionStartRepo.findById(p.getTransactionId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction id: "
                                        + p.getTransactionId()));

                connectorService.createConnectorStatus(transactionStart.getConnector(), p.getStopTimestamp(), p.getStatusUpdate());
            }

            if (transaction.vehicleUnplugged() || chargingProcess.stoppedExternally()) {
                if (chargingProcess.stoppedExternally()) {
                    espNotificationService.notifyAboutChargingStopped(chargingProcess);
                } else {
                    espNotificationService.notifyAboutConsumptionUpdated(chargingProcess, transaction);
                }
            }
        } catch (Exception e) {
            log.error("Transaction save failed", e);
            transactionStopFailedRepo.save(createTransactionStopFailed(p, e));
        }
    }

    private OcppChargingProcess updateChargingProcess(TransactionStop stop, UpdateTransactionParams params) {
        OcppChargingProcess process = chargingProcessService.findByTransactionId(params.getTransactionId());
        stop.setTransaction(process.getTransaction());
        transactionStopRepo.save(stop);

        if (params.getStopTimestamp() != null) {
            log.info("Transaction update: {} with end date: {}",
                    params.getTransactionId(),
                    params.getStopTimestamp());
            log.info("Ending charging process on transaction update: {} with end date: {}",
                    process.getOcppChargingProcessId(),
                    params.getStopTimestamp());
            process.setEndDate(params.getStopTimestamp().toDate());

            return chargingProcessService.save(process);
        }
        return process;
    }
}
