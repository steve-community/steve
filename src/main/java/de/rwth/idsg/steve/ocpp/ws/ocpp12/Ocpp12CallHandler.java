package de.rwth.idsg.steve.ocpp.ws.ocpp12;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.ocpp.soap.CentralSystemService12_SoapServer;
import de.rwth.idsg.steve.ocpp.ws.pipeline.AbstractCallHandler;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2010._08.AuthorizeRequest;
import ocpp.cs._2010._08.BootNotificationRequest;
import ocpp.cs._2010._08.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2010._08.FirmwareStatusNotificationRequest;
import ocpp.cs._2010._08.HeartbeatRequest;
import ocpp.cs._2010._08.MeterValuesRequest;
import ocpp.cs._2010._08.StartTransactionRequest;
import ocpp.cs._2010._08.StatusNotificationRequest;
import ocpp.cs._2010._08.StopTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles OcppJsonCalls for OCPP 1.2
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.03.2015
 */
@Slf4j
@Component
public class Ocpp12CallHandler extends AbstractCallHandler {

    @Autowired private CentralSystemService12_SoapServer server;

    /**
     * Wrapper around actual service functions
     *
     * TODO: Not the most elegant solution. Could use command pattern leveraging polymorphism
     */
    @Override
    protected ResponseType dispatch(RequestType params, String chargeBoxId) {
        ResponseType r = null;

        if (params instanceof BootNotificationRequest) {
            r = server.bootNotificationWithTransport((BootNotificationRequest) params, chargeBoxId, OcppProtocol.V_12_JSON);

        } else if (params instanceof FirmwareStatusNotificationRequest) {
            r = server.firmwareStatusNotification((FirmwareStatusNotificationRequest) params, chargeBoxId);

        } else if (params instanceof StatusNotificationRequest) {
            r = server.statusNotification((StatusNotificationRequest) params, chargeBoxId);

        } else if (params instanceof MeterValuesRequest) {
            r = server.meterValues((MeterValuesRequest) params, chargeBoxId);

        } else if (params instanceof DiagnosticsStatusNotificationRequest) {
            r = server.diagnosticsStatusNotification((DiagnosticsStatusNotificationRequest) params, chargeBoxId);

        } else if (params instanceof StartTransactionRequest) {
            r = server.startTransaction((StartTransactionRequest) params, chargeBoxId);

        } else if (params instanceof StopTransactionRequest) {
            r = server.stopTransaction((StopTransactionRequest) params, chargeBoxId);

        } else if (params instanceof HeartbeatRequest) {
            r = server.heartbeat((HeartbeatRequest) params, chargeBoxId);

        } else if (params instanceof AuthorizeRequest) {
            r = server.authorize((AuthorizeRequest) params, chargeBoxId);
        }

        return r;
    }
}
