package net.parkl.ocpp.service.cs.cleanup;

import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.entities.TransactionStop;
import net.parkl.ocpp.repositories.*;
import net.parkl.ocpp.service.ChargingProcessService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static net.parkl.ocpp.service.cs.factory.TransactionFactory.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionCleanupService {
    private final TransactionRepository transactionRepo;
    private final TransactionStartRepository transactionStartRepo;
    private final TransactionStopRepository transactionStopRepo;
    private final TransactionStopFailedRepository transactionStopFailedRepo;
    private final ChargingProcessService chargingProcessService;

    @Transactional
    public boolean cleanupTransaction(int transactionId) {
        try {
            log.info("Cleaning up transaction with transaction id: {}", transactionId);
            Transaction transaction = transactionRepo.findById(transactionId).orElse(null);

            if (transaction == null){
                log.warn("Invalid transaction id {}", transactionId);
                return false;
            }

            TransactionStart transactionStart =
                    transactionStartRepo.findById(transactionId)
                            .orElseThrow(() -> new IllegalArgumentException("Transaction start not found for transaction id: " + transactionId));

            DateTime eventTime = DateTime.now();
            TransactionStop transactionStop = createTransactionStopForCleanup(transactionId, eventTime);
            List<OcppChargingProcess> chargingProcesses = chargingProcessService.findListByTransactionId(transactionId);

            transactionStop.setTransaction(transactionStart);
            transactionStopRepo.save(transactionStop);

            if (!chargingProcesses.isEmpty()) {
                log.info("Ending charging processes on transaction cleanup: {} with end date: {}", transactionId, eventTime);

                for (OcppChargingProcess chargingProcess : chargingProcesses) {
                    log.info("Ending charging process on transaction cleanup: {} with end date: {}", chargingProcess.getOcppChargingProcessId(), eventTime);
                    chargingProcess.setEndDate(eventTime.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    chargingProcessService.save(chargingProcess);
                }
            } else {
                log.warn("Charging processes not found for transaction {}", transactionId);
            }

            log.info("Transaction cleaned up: transaction id={}, charging processes={}", transactionId,
                    chargingProcesses.size());
            return true;
        } catch (Exception e) {
            log.error("Transaction cleanup failed for: "+transactionId, e);
            transactionStopFailedRepo.save(createTransactionStopFailedForCleanup(transactionId, e));
            return false;
        }
    }


    public List<Transaction> getTransactionsForCleanup(LocalDateTime threshold) {
        return transactionRepo.findActiveStartedBefore(threshold);
    }


}
