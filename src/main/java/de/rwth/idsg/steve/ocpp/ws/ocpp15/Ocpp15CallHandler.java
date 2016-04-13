package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.pipeline.AbstractCallHandler;
import de.rwth.idsg.steve.service.CentralSystemService15_Service;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Handles OcppJsonCalls for OCPP 1.5
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.03.2015
 */
@Slf4j
@Component
public class Ocpp15CallHandler extends AbstractCallHandler {

    @Autowired private CentralSystemService15_Service service;

    /**
     * Wrapper around actual service functions
     *
     * TODO: Not the most elegant solution. Could use command pattern leveraging polymorphism
     */
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
