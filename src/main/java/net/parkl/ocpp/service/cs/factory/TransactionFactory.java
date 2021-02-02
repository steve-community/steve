package net.parkl.ocpp.service.cs.factory;

import com.google.common.base.Throwables;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import net.parkl.ocpp.entities.TransactionStop;
import net.parkl.ocpp.entities.TransactionStopFailed;
import net.parkl.ocpp.entities.TransactionStopId;
import org.joda.time.DateTime;

public class TransactionFactory {

    public static TransactionStop createTransactionStop(UpdateTransactionParams params) {
        TransactionStop stop = new TransactionStop();
        decorateTransactionStop(stop, params);
        return stop;
    }

    public static TransactionStopFailed createTransactionStopFailed(UpdateTransactionParams params, Exception exception) {
        TransactionStopFailed failed = new TransactionStopFailed();
        decorateTransactionStop(failed, params);
        failed.setFailReason(Throwables.getStackTraceAsString(exception));
        return failed;
    }

    public static TransactionStopId createTransactionStopId(int transactionId, DateTime stop) {
        TransactionStopId transactionStopId = new TransactionStopId();
        transactionStopId.setTransactionPk(transactionId);
        transactionStopId.setEventTimestamp(stop.toDate());
        return transactionStopId;
    }

    private static void decorateTransactionStop(TransactionStop transactionStop, UpdateTransactionParams params) {
        transactionStop.setTransactionStopId(createTransactionStopId(params.getTransactionId(), params.getEventTimestamp()));
        transactionStop.setEventActor(params.getEventActor());
        if (params.getStopTimestamp() != null) {
            transactionStop.setStopTimestamp(params.getStopTimestamp().toDate());
        }
        transactionStop.setStopValue(params.getStopMeterValue());
        transactionStop.setStopReason(params.getStopReason());
    }
}
