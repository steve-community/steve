package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageParams;
import ocpp.cp._2015._10.MessageTrigger;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
public class TriggerMessageTask extends CommunicationTask<TriggerMessageParams, String> {

    public TriggerMessageTask(OcppVersion ocppVersion, TriggerMessageParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Deprecated
    @Override
    public <T extends RequestType> T getOcpp12Request() {
        throw new RuntimeException("Not supported");
    }

    @Deprecated
    @Override
    public <T extends RequestType> T getOcpp15Request() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public ocpp.cp._2015._10.TriggerMessageRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.TriggerMessageRequest()
                .withConnectorId(params.getConnectorId())
                .withRequestedMessage(MessageTrigger.fromValue(params.getTriggerMessage().value()));
    }

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp12Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
    }

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp15Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.TriggerMessageResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
