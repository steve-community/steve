package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class RemoteStopTransactionTask extends CommunicationTask<RemoteStopTransactionParams, String> {

    public RemoteStopTransactionTask(OcppVersion ocppVersion, RemoteStopTransactionParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ocpp.cp._2010._08.RemoteStopTransactionRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.RemoteStopTransactionRequest()
                .withTransactionId(params.getTransactionId());
    }

    @Override
    public ocpp.cp._2012._06.RemoteStopTransactionRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.RemoteStopTransactionRequest()
                .withTransactionId(params.getTransactionId());
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.RemoteStopTransactionResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.RemoteStopTransactionResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
