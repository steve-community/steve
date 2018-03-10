package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.pipeline.AbstractCallHandler;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Deserializer;
import de.rwth.idsg.steve.ocpp.ws.pipeline.IncomingPipeline;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Sender;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Serializer;
import de.rwth.idsg.steve.service.CentralSystemService15_Service;
import lombok.RequiredArgsConstructor;
import ocpp.cs._2012._06.AuthorizeRequest;
import ocpp.cs._2012._06.BootNotificationRequest;
import ocpp.cs._2012._06.DataTransferRequest;
import ocpp.cs._2012._06.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2012._06.FirmwareStatusNotificationRequest;
import ocpp.cs._2012._06.HeartbeatRequest;
import ocpp.cs._2012._06.MeterValuesRequest;
import ocpp.cs._2012._06.StartTransactionRequest;
import ocpp.cs._2012._06.StatusNotificationRequest;
import ocpp.cs._2012._06.StopTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.03.2015
 */
@Component
public class Ocpp15WebSocketEndpoint extends AbstractWebSocketEndpoint {

    @Autowired private CentralSystemService15_Service service;

    @Autowired private Serializer serializer;
    @Autowired private Sender sender;
    @Autowired private FutureResponseContextStore futureResponseContextStore;

    @PostConstruct
    public void init() {
        Deserializer deserializer = new Deserializer(futureResponseContextStore, Ocpp15TypeStore.INSTANCE);
        IncomingPipeline pipeline = new IncomingPipeline(deserializer, new Ocpp15CallHandler(service), serializer, sender);
        super.init(pipeline);
    }

    @RequiredArgsConstructor
    private static class Ocpp15CallHandler extends AbstractCallHandler {

        private final CentralSystemService15_Service service;

        @Override
        protected ResponseType dispatch(RequestType params, String chargeBoxId) {
            ResponseType r = null;

            if (params instanceof BootNotificationRequest) {
                r = service.bootNotification((BootNotificationRequest) params, chargeBoxId, OcppProtocol.V_15_JSON);

            } else if (params instanceof FirmwareStatusNotificationRequest) {
                r = service.firmwareStatusNotification((FirmwareStatusNotificationRequest) params, chargeBoxId);

            } else if (params instanceof StatusNotificationRequest) {
                r = service.statusNotification((StatusNotificationRequest) params, chargeBoxId);

            } else if (params instanceof MeterValuesRequest) {
                r = service.meterValues((MeterValuesRequest) params, chargeBoxId);

            } else if (params instanceof DiagnosticsStatusNotificationRequest) {
                r = service.diagnosticsStatusNotification((DiagnosticsStatusNotificationRequest) params, chargeBoxId);

            } else if (params instanceof StartTransactionRequest) {
                r = service.startTransaction((StartTransactionRequest) params, chargeBoxId);

            } else if (params instanceof StopTransactionRequest) {
                r = service.stopTransaction((StopTransactionRequest) params, chargeBoxId);

            } else if (params instanceof HeartbeatRequest) {
                r = service.heartbeat((HeartbeatRequest) params, chargeBoxId);

            } else if (params instanceof AuthorizeRequest) {
                r = service.authorize((AuthorizeRequest) params, chargeBoxId);

            } else if (params instanceof DataTransferRequest) {
                r = service.dataTransfer((DataTransferRequest) params, chargeBoxId);
            }

            return r;
        }
    }
}
