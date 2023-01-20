package net.parkl.ocpp.service.cluster;

import org.joda.time.DateTime;

public interface PersistentTaskResultCallback {
    void addNewResponse(int taskId, String chargeBoxId, String response, DateTime endTimestamp);

    void addNewError(int taskId, String chargeBoxId, String errorMessage, DateTime endTimestamp);
}
