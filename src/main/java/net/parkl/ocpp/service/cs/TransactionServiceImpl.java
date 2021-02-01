package net.parkl.ocpp.service.cs;

import com.google.common.base.Throwables;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.ConnectorMeterValue;
import net.parkl.ocpp.entities.ConnectorStatus;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.OcppReservation;
import net.parkl.ocpp.entities.OcppTag;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.entities.TransactionStop;
import net.parkl.ocpp.entities.TransactionStopFailed;
import net.parkl.ocpp.entities.TransactionStopId;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.ConnectorStatusRepository;
import net.parkl.ocpp.repositories.OcppChargeBoxRepository;
import net.parkl.ocpp.repositories.OcppReservationRepository;
import net.parkl.ocpp.repositories.OcppTagRepository;
import net.parkl.ocpp.repositories.TransactionCriteriaRepository;
import net.parkl.ocpp.repositories.TransactionRepository;
import net.parkl.ocpp.repositories.TransactionStartRepository;
import net.parkl.ocpp.repositories.TransactionStopFailedRepository;
import net.parkl.ocpp.repositories.TransactionStopRepository;
import net.parkl.ocpp.service.ChargingProcessService;
import net.parkl.ocpp.service.OcppConstants;
import net.parkl.ocpp.service.OcppMiddleware;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.stevep.util.ListTransform;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.parkl.ocpp.service.cs.converter.TransactionDtoConverter.toTransactionDto;

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
    private OcppChargeBoxRepository chargeBoxRepo;

    @Autowired
    private OcppTagRepository tagRepo;
    @Autowired
    private ConnectorMeterValueService connectorMeterValueService;
    @Autowired
    private ConnectorRepository connectorRepo;
    @Autowired
    private ConnectorStatusRepository connectorStatusRepo;

    @Autowired
    private OcppReservationRepository reservationRepo;

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor executor;

    @Autowired
    private OcppMiddleware ocppMiddleware;

    @Autowired
    private AdvancedChargeBoxConfiguration config;

    @Autowired
    private ChargingProcessService chargingProcessService;

    @Override
    public List<Integer> getActiveTransactionIds(String chargeBoxId) {
        return transactionRepo.findActiveTransactionIds(chargeBoxId);
    }

    @Override
    public List<de.rwth.idsg.steve.repository.dto.Transaction> getTransactions(TransactionQueryForm form) {
        List<Transaction> list = transactionCriteriaRepository.getInternal(form);


        Map<String, OcppTag> tagMap = ListTransform.transformToMap(tagRepo.findAll(),
                OcppTag::getIdTag);

        Map<String, OcppChargeBox> boxMap = ListTransform.transformToMap(chargeBoxRepo.findAll(),
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
        // -------------------------------------------------------------------------
        // Step 1: Collect general data about transaction
        // -------------------------------------------------------------------------

        TransactionQueryForm form = new TransactionQueryForm();
        form.setTransactionPk(transactionPk);
        form.setType(TransactionQueryForm.QueryType.ALL);
        form.setPeriodType(TransactionQueryForm.QueryPeriodType.ALL);

        List<Transaction>
                transactions = transactionCriteriaRepository.getInternal(form);

        if (transactions == null || transactions.isEmpty()) {
            throw new SteveException("There is no transaction with id '%s'", transactionPk);
        }

        Transaction transaction = transactions.get(0);

        Date startTimestamp = transaction.getStartTimestamp();
        Date stopTimestamp = transaction.getStopTimestamp();
        String stopValue = transaction.getStopValue();
        String chargeBoxId = transaction.getConnector().getChargeBoxId();
        int connectorId = transaction.getConnector().getConnectorId();

        OcppTag tag = tagRepo.findByIdTag(transaction.getOcppTag());
        if (tag == null) {
            throw new IllegalStateException("Invalid id tag: " + transaction.getOcppTag());
        }

        OcppChargeBox box = chargeBoxRepo.findByChargeBoxId(transaction.getConnector().getChargeBoxId());
        if (box == null) {
            throw new IllegalStateException("Invalid charge box id: " + transaction.getConnector().getChargeBoxId());
        }

        // -------------------------------------------------------------------------
        // Step 2: Collect intermediate meter values
        // -------------------------------------------------------------------------

        TransactionStart nextTx = null;

        // Case 1: Ideal and most accurate case. Station sends meter values with transaction id set.
        //
        List<ConnectorMeterValue> cmv1 = connectorMeterValueService.findByTransactionPk(transaction.getTransactionPk());

        // Case 2: Fall back to filtering according to time windows
        //
        List<ConnectorMeterValue> cmv2;
        if (stopTimestamp == null && stopValue == null) {
            // https://github.com/RWTH-i5-IDSG/steve/issues/97
            //
            // handle "zombie" transaction, for which we did not receive any StopTransaction. if we do not handle it,
            // meter values for all subsequent transactions at this chargebox and connector will be falsely attributed
            // to this zombie transaction.
            //
            // "what is the subsequent transaction at the same chargebox and connector?"

            nextTx = transactionStartRepo.findNextTransactions(chargeBoxId, connectorId, startTimestamp,
                    PageRequest.of(0, 1)).stream().findFirst().orElse(null);
            if (nextTx == null) {
                // the last active transaction
                cmv2 = connectorMeterValueService.findByChargeBoxIdAndConnectorIdAfter(chargeBoxId, connectorId, startTimestamp);
            } else {
                cmv2 = connectorMeterValueService.findByChargeBoxIdAndConnectorIdBetween(chargeBoxId, connectorId, startTimestamp, nextTx.getStartTimestamp());
            }
        } else {
            // finished transaction
            cmv2 = connectorMeterValueService.findByChargeBoxIdAndConnectorIdBetween(chargeBoxId, connectorId, startTimestamp, stopTimestamp);
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

        return new TransactionDetails(toTransactionDto(transaction, box, tag), values, nextTx);
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
        Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(params.getChargeBoxId(), params.getConnectorId());
        // -------------------------------------------------------------------------
        // Step 1: Insert connector and idTag, if they are new to us
        // -------------------------------------------------------------------------
        if (c == null) {
            c = new Connector();
            c.setChargeBoxId(params.getChargeBoxId());
            c.setConnectorId(params.getConnectorId());
            c = connectorRepo.save(c);
        }

        OcppTag tag = tagRepo.findByIdTag(params.getIdTag());
        boolean unknownTagInserted = false;
        if (tag == null) {
            tag = new OcppTag();
            tag.setIdTag(params.getIdTag());
            String note = "This unknown idTag was used in a transaction that started @ " + params.getStartTimestamp()
                    + ". It was reported @ " + DateTime.now() + ".";
            tag.setMaxActiveTransactionCount(0);
            tagRepo.save(tag);

            unknownTagInserted = true;
        }

        // -------------------------------------------------------------------------
        // Step 2: Insert transaction if it does not exist already
        // ---------------------------------------------------------------------------
        TransactionStart existing = transactionStartRepo.findByConnectorAndIdTagAndStartValues(
                c, params.getIdTag(),
                params.getStartTimestamp() != null ? params.getStartTimestamp().toDate() : null,
                params.getStartMeterValue());

        if (existing != null) {
            log.warn("Transaction already exists: {}", existing.getTransactionPk());
            return existing.getTransactionPk();
        }

        TransactionStart t = new TransactionStart();
        t.setConnector(c);
        t.setOcppTag(params.getIdTag());
        if (params.getStartTimestamp() != null) {
            t.setStartTimestamp(params.getStartTimestamp().toDate());
        }
        t.setStartValue(params.getStartMeterValue());
        if (params.getEventTimestamp() != null) {
            t.setEventTimestamp(params.getEventTimestamp().toDate());
        }


        t = transactionStartRepo.save(t);


        if (unknownTagInserted) {
            log.warn("The transaction '{}' contains an unknown idTag '{}' which was inserted into DB "
                    + "to prevent information loss and has been blocked", t.getTransactionPk(), params.getIdTag());
        }


        log.info("Transaction saved id={}, querying charing suitable process for connector: {}",
                t.getTransactionPk(),
                c.getConnectorId());
        OcppChargingProcess proc = chargingProcessService.fetchChargingProcess(params.getConnectorId(),
                params.getChargeBoxId(),
                2000);
        if (proc != null) {
            log.info("Setting transaction on connector {} to process: {}...", c.getConnectorId(), proc.getOcppChargingProcessId());
            proc.setTransaction(t);
            chargingProcessService.save(proc);
        } else {
            log.warn("No active charging process found without transaction for connector: {}", c.getConnectorId());
        }

        // -------------------------------------------------------------------------
        // Step 3 for OCPP >= 1.5: A startTransaction may be related to a reservation
        // -------------------------------------------------------------------------
        if (params.isSetReservationId() && params.getReservationId() != -1 && config.checkReservationId(params.getChargeBoxId())) {
            OcppReservation r = reservationRepo.findById(params.getReservationId()).
                    orElseThrow(() -> new IllegalArgumentException("Invalid reservation: " + params.getReservationId()));

            r.setStatus(ReservationStatus.USED.name());
            r.setTransaction(t);
            reservationRepo.save(r);
        }

        // -------------------------------------------------------------------------
        // Step 4: Set connector status
        // -------------------------------------------------------------------------

        if (shouldInsertConnectorStatusAfterTransactionMsg(params.getChargeBoxId())) {
            ConnectorStatus s = new ConnectorStatus();
            s.setConnector(t.getConnector());
            if (params.getStartTimestamp() != null) {
                s.setStatusTimestamp(params.getStartTimestamp().toDate());
            }
            s.setStatus(params.getStatusUpdate().getStatus());
            s.setErrorCode(params.getStatusUpdate().getErrorCode());

            connectorStatusRepo.save(s);
        }

        return t.getTransactionPk();
    }

    @Override
    @Transactional
    public void updateTransaction(UpdateTransactionParams params) {
        // -------------------------------------------------------------------------
        // Step 1: insert transaction stop data
        // -------------------------------------------------------------------------

        Transaction transaction = transactionRepo.findById(params.getTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction id: " + params.getTransactionId()));

        boolean stopValueNull = transaction.getStopValue() == null;
        OcppChargingProcess savedProcess = null;
        boolean notifyStop = false;

        try {
            TransactionStop stop = new TransactionStop();

            TransactionStopId id = new TransactionStopId();
            id.setTransactionPk(params.getTransactionId());
            id.setEventTimestamp(params.getEventTimestamp().toDate());
            stop.setTransactionStopId(id);

            stop.setEventActor(params.getEventActor());
            if (params.getStopTimestamp() != null) {
                stop.setStopTimestamp(params.getStopTimestamp().toDate());
            }
            stop.setStopValue(params.getStopMeterValue());
            stop.setStopReason(params.getStopReason());

            OcppChargingProcess process = chargingProcessService.findByTransactionId(params.getTransactionId());
            stop.setTransaction(process.getTransaction());
            transactionStopRepo.save(stop);

            //update charging process

            if (params.getStopTimestamp() != null) {
                log.info("Transaction update: {} with end date: {}",
                        params.getTransactionId(),
                        params.getStopTimestamp());
                log.info("Ending charging process on transaction update: {} with end date: {}",
                        process.getOcppChargingProcessId(),
                        params.getStopTimestamp());
                process.setEndDate(params.getStopTimestamp().toDate());
                if (process.getStopRequestDate() == null) {
                    notifyStop = true;
                }
                savedProcess = chargingProcessService.save(process);
            }
        } catch (Exception e) {
            log.error("Transaction save failed", e);
            try {
                TransactionStopFailed fail = new TransactionStopFailed();

                TransactionStopId id = new TransactionStopId();
                id.setTransactionPk(params.getTransactionId());
                id.setEventTimestamp(params.getEventTimestamp().toDate());
                fail.setTransactionStopId(id);

                fail.setEventActor(params.getEventActor());
                if (params.getStopTimestamp() != null) {
                    fail.setStopTimestamp(params.getStopTimestamp().toDate());
                }
                fail.setStopValue(params.getStopMeterValue());
                fail.setStopReason(params.getStopReason());
                fail.setFailReason(Throwables.getStackTraceAsString(e));

                transactionStopFailedRepo.save(fail);
            } catch (Exception ex) {
                // This is where we give up and just log
                log.error("Transaction stop failure save error", e);
            }
        }

        // -------------------------------------------------------------------------
        // Step 2: Set connector status back. We do this even in cases where step 1
        // fails. It probably and hopefully makes sense.
        // -------------------------------------------------------------------------

        if (shouldInsertConnectorStatusAfterTransactionMsg(params.getChargeBoxId())) {
            TransactionStart transactionStart = transactionStartRepo.findById(params.getTransactionId()).orElseThrow(() -> new IllegalArgumentException("Invalid transaction id: " + params.getTransactionId()));

            ConnectorStatus s = new ConnectorStatus();
            s.setConnector(transactionStart.getConnector());
            if (params.getStopTimestamp() != null) {
                s.setStatusTimestamp(params.getStopTimestamp().toDate());
            }
            s.setStatus(params.getStatusUpdate().getStatus());
            s.setErrorCode(params.getStatusUpdate().getErrorCode());

            connectorStatusRepo.save(s);

        }

        //ESP notification
        if (stopValueNull || notifyStop) {
            boolean stopped = notifyStop;
            final OcppChargingProcess pr = savedProcess;
            executor.execute(() -> {
                if (stopped) {
                    // if stop charging was not initiated by the ESP
                    log.info("Notifying ESP about stop transaction from charger: {}...", params.getTransactionId());
                    ocppMiddleware.stopChargingExternal(pr, OcppConstants.REASON_VEHICLE_CHARGED);
                } else {
                    log.info("Notifying ESP about consumption of transaction: {}...", params.getTransactionId());
                    Transaction t = transactionRepo.findById(params.getTransactionId()).orElseThrow(() -> new IllegalArgumentException("Invalid transaction id: " + params.getTransactionId()));
                    log.info("start: {}, stop: {}", t.getStartValue(), t.getStopValue());
                    ocppMiddleware.updateConsumption(pr, t.getStartValue(), t.getStopValue());
                }
            });
        }
    }

    private boolean shouldInsertConnectorStatusAfterTransactionMsg(String chargeBoxId) {
        OcppChargeBox cb = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        return cb != null && cb.getInsertConnectorStatusAfterTransactionMsg() != null &&
                cb.getInsertConnectorStatusAfterTransactionMsg();

    }


}
