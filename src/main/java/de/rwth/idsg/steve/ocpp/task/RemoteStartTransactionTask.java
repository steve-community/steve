package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class RemoteStartTransactionTask extends CommunicationTask<RemoteStartTransactionParams, String> {

    public RemoteStartTransactionTask(OcppVersion ocppVersion, RemoteStartTransactionParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ocpp.cp._2010._08.RemoteStartTransactionRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.RemoteStartTransactionRequest()
                .withIdTag(params.getIdTag())
                .withConnectorId(params.getConnectorId());
    }

    @Override
    public ocpp.cp._2012._06.RemoteStartTransactionRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.RemoteStartTransactionRequest()
                .withIdTag(params.getIdTag())
                .withConnectorId(params.getConnectorId());
    }

    /**
     * TODO: RemoteStartTransactionRequest.chargingProfile not implemented
     */
    @Override
    public ocpp.cp._2015._10.RemoteStartTransactionRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.RemoteStartTransactionRequest()
                .withIdTag(params.getIdTag())
                .withConnectorId(params.getConnectorId());
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.RemoteStartTransactionResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.RemoteStartTransactionResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }


    @Override
    public AsyncHandler<ocpp.cp._2015._10.RemoteStartTransactionResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
