package net.parkl.ocpp.service.cs.cleanup;

import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.entities.TransactionStop;
import net.parkl.ocpp.repositories.TransactionRepository;
import net.parkl.ocpp.repositories.TransactionStartRepository;
import net.parkl.ocpp.repositories.TransactionStopFailedRepository;
import net.parkl.ocpp.repositories.TransactionStopRepository;
import net.parkl.ocpp.service.ChargingProcessService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
            OcppChargingProcess chargingProcess = chargingProcessService.findByTransactionId(transactionId);

            transactionStop.setTransaction(transactionStart);
            transactionStopRepo.save(transactionStop);

            if (chargingProcess != null) {
                log.info("Transaction update: {} with end date: {}", transactionId, eventTime);
                String chargingProcessId = chargingProcess.getOcppChargingProcessId();
                log.info("Ending charging process on transaction update: {} with end date: {}", chargingProcessId, eventTime);
                chargingProcess.setEndDate(eventTime.toDate());

                chargingProcess = chargingProcessService.save(chargingProcess);
            } else {
                log.warn("Charging process not found for transaction {}", transactionId);
            }

            log.info("Transaction cleaned up: transaction id={}, charging process id={}", transactionId,
                    chargingProcess !=null ? chargingProcess.getOcppChargingProcessId() : "-");
            return true;
        } catch (Exception e) {
            log.error("Transaction cleanup failed for: "+transactionId, e);
            transactionStopFailedRepo.save(createTransactionStopFailedForCleanup(transactionId, e));
            return false;
        }
    }


    public List<Transaction> getTransactionsForCleanup(Date threshold) {
        return transactionRepo.findActiveStartedBefore(threshold);
    }
}
