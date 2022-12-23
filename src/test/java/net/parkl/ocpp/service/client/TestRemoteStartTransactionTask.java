package net.parkl.ocpp.service.client;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.RemoteStartTransactionTask;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class TestRemoteStartTransactionTask extends RemoteStartTransactionTask {

    private CountDownLatch finishedLatch;

    public TestRemoteStartTransactionTask(OcppVersion ocppVersion,
                                          RemoteStartTransactionParams params,
                                          CountDownLatch responseLatch) {
        super(ocppVersion, params);
        finishedLatch = new CountDownLatch(1);
        this.addCallback(new DefaultOcppCallback<String>() {
            @Override
            public void success(String chargeBoxId, String s, boolean remote) {
                log.info("test remote start transaction finished for chargeBox: {}", chargeBoxId);
                responseLatch.countDown();
            }
        });
    }

    @Override
    public boolean isFinished() {
        return super.isFinished();
    }
}
