package net.parkl.ocpp.service.cs.cleanup;

import net.parkl.ocpp.entities.Transaction;

public interface TransactionCleanupChecker {
    boolean checkTransactionForCleanup(Transaction transaction);
}
