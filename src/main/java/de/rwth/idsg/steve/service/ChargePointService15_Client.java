package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.handler.ocpp15.CancelReservationResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.ChangeAvailabilityResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.ChangeConfigurationResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.ClearCacheResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.DataTransferResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.GetConfigurationResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.GetDiagnosticsResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.GetLocalListVersionResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.RemoteStartTransactionResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.RemoteStopTransactionResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.ReserveNowResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.ResetResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.SendLocalListResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.UnlockConnectorResponseHandler;
import de.rwth.idsg.steve.handler.ocpp15.UpdateFirmwareResponseHandler;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.RequestTaskStore;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.RequestTask;
import de.rwth.idsg.steve.web.dto.common.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.common.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.common.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.common.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.common.UpdateFirmwareParams;
import de.rwth.idsg.steve.web.dto.ocpp15.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ConfigurationKeyEnum;
import de.rwth.idsg.steve.web.dto.ocpp15.DataTransferParams;
import de.rwth.idsg.steve.web.dto.ocpp15.GetConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ReserveNowParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp15.SendLocalListParams;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2012._06.AuthorisationData;
import ocpp.cp._2012._06.CancelReservationRequest;
import ocpp.cp._2012._06.ChangeAvailabilityRequest;
import ocpp.cp._2012._06.ChangeConfigurationRequest;
import ocpp.cp._2012._06.ClearCacheRequest;
import ocpp.cp._2012._06.DataTransferRequest;
import ocpp.cp._2012._06.GetConfigurationRequest;
import ocpp.cp._2012._06.GetDiagnosticsRequest;
import ocpp.cp._2012._06.GetLocalListVersionRequest;
import ocpp.cp._2012._06.RemoteStartTransactionRequest;
import ocpp.cp._2012._06.RemoteStopTransactionRequest;
import ocpp.cp._2012._06.ReserveNowRequest;
import ocpp.cp._2012._06.ResetRequest;
import ocpp.cp._2012._06.SendLocalListRequest;
import ocpp.cp._2012._06.UnlockConnectorRequest;
import ocpp.cp._2012._06.UpdateFirmwareRequest;
import ocpp.cp._2012._06.UpdateType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toDateTime;

/**
 * Transport-level agnostic client implementation of OCPP V1.5
 * which builds the request payloads and delegates to an invoker.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
@Slf4j
@Service
public class ChargePointService15_Client {
    private static final OcppVersion VERSION = OcppVersion.V_15;

    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private RequestTaskStore requestTaskStore;
    @Autowired private ChargePointService15_Dispatcher dispatcher;

    // -------------------------------------------------------------------------
    // Create Request Payloads
    // -------------------------------------------------------------------------

    private ChangeAvailabilityRequest prepareChangeAvailability(ChangeAvailabilityParams params) {
        return new ChangeAvailabilityRequest()
                .withConnectorId(params.getConnectorId())
                .withType(params.getAvailType());
    }

    private ChangeConfigurationRequest prepareChangeConfiguration(ChangeConfigurationParams params) {
        return new ChangeConfigurationRequest()
                .withKey(params.getConfKey().value())
                .withValue(params.getValue());
    }

    private ClearCacheRequest prepareClearCache() {
        return new ClearCacheRequest();
    }

    private GetDiagnosticsRequest prepareGetDiagnostics(GetDiagnosticsParams params) {
        return new GetDiagnosticsRequest()
                .withLocation(params.getLocation())
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval())
                .withStartTime(toDateTime(params.getStart()))
                .withStopTime(toDateTime(params.getStop()));
    }

    private RemoteStartTransactionRequest prepareRemoteStartTransaction(RemoteStartTransactionParams params) {
        return new RemoteStartTransactionRequest()
                .withIdTag(params.getIdTag())
                .withConnectorId(params.getConnectorId());
    }

    private RemoteStopTransactionRequest prepareRemoteStopTransaction(RemoteStopTransactionParams params) {
        return new RemoteStopTransactionRequest()
                .withTransactionId(params.getTransactionId());
    }

    private ResetRequest prepareReset(ResetParams params) {
        return new ResetRequest()
                .withType(params.getResetType());
    }

    private UnlockConnectorRequest prepareUnlockConnector(UnlockConnectorParams params) {
        return new UnlockConnectorRequest()
                .withConnectorId(params.getConnectorId());
    }

    private UpdateFirmwareRequest prepareUpdateFirmware(UpdateFirmwareParams params) {
        return new UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(toDateTime(params.getRetrieve()))
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval());
    }

    /**
     * Dummy implementation. It must be vendor-specific.
     */
    private DataTransferRequest prepareDataTransfer(DataTransferParams params) {
        return new DataTransferRequest()
                .withVendorId(params.getVendorId())
                .withMessageId(params.getMessageId())
                .withData(params.getData());
    }

    private GetConfigurationRequest prepareGetConfiguration(GetConfigurationParams params) {
        List<String> stringList = params.getConfKeyList()
                                        .stream()
                                        .map(ConfigurationKeyEnum::value)
                                        .collect(Collectors.toList());

        return new GetConfigurationRequest().withKey(stringList);
    }

    private GetLocalListVersionRequest prepareGetLocalListVersion() {
        return new GetLocalListVersionRequest();
    }

    private SendLocalListRequest prepareSendLocalList(SendLocalListParams params) {
        // DIFFERENTIAL update
        if (UpdateType.DIFFERENTIAL.equals(params.getUpdateType())) {
            List<AuthorisationData> auths = new ArrayList<>();

            // Step 1: For the idTags to be deleted, insert only the idTag
            for (String idTag : params.getDeleteList()) {
                auths.add(new AuthorisationData().withIdTag(idTag));
            }

            // Step 2: For the idTags to be added or updated, insert them with their IdTagInfos
            auths.addAll(userService.getAuthData(params.getAddUpdateList()));

            return new SendLocalListRequest()
                    .withListVersion(params.getListVersion())
                    .withUpdateType(UpdateType.DIFFERENTIAL)
                    .withLocalAuthorisationList(auths);

        // FULL update
        } else {
            return new SendLocalListRequest()
                    .withListVersion(params.getListVersion())
                    .withUpdateType(UpdateType.FULL)
                    .withLocalAuthorisationList(userService.getAuthDataOfAllUsers());
        }
    }

    private ReserveNowRequest prepareReserveNow(ReserveNowParams params, int reservationId) {
        String idTag = params.getIdTag();
        return new ReserveNowRequest()
                .withConnectorId(params.getConnectorId())
                .withReservationId(reservationId)
                .withExpiryDate(params.getExpiry().toDateTime())
                .withIdTag(idTag)
                .withParentIdTag(userRepository.getParentIdtag(idTag));
    }

    private CancelReservationRequest prepareCancelReservation(CancelReservationParams params) {
        return new CancelReservationRequest()
                .withReservationId(params.getReservationId());
    }

    // -------------------------------------------------------------------------
    // Multiple Execution
    // -------------------------------------------------------------------------

    public int changeAvailability(ChangeAvailabilityParams params) {
        ChangeAvailabilityRequest req = this.prepareChangeAvailability(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Change Availability", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            ChangeAvailabilityResponseHandler handler =
                    new ChangeAvailabilityResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.changeAvailability(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int changeConfiguration(ChangeConfigurationParams params) {
        ChangeConfigurationRequest req = this.prepareChangeConfiguration(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Change Configuration", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            ChangeConfigurationResponseHandler handler =
                    new ChangeConfigurationResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.changeConfiguration(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int clearCache(MultipleChargePointSelect params) {
        ClearCacheRequest req = this.prepareClearCache();
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Clear Cache", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            ClearCacheResponseHandler handler = new ClearCacheResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.clearCache(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int getDiagnostics(GetDiagnosticsParams params) {
        GetDiagnosticsRequest req = this.prepareGetDiagnostics(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Get Diagnostics", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            GetDiagnosticsResponseHandler handler =
                    new GetDiagnosticsResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.getDiagnostics(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int reset(ResetParams params) {
        ResetRequest req = this.prepareReset(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Reset", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            ResetResponseHandler handler = new ResetResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.reset(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int updateFirmware(UpdateFirmwareParams params) {
        UpdateFirmwareRequest req = this.prepareUpdateFirmware(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Update Firmware", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            UpdateFirmwareResponseHandler handler = new UpdateFirmwareResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.updateFirmware(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int dataTransfer(DataTransferParams params) {
        DataTransferRequest req = this.prepareDataTransfer(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Data Transfer", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            DataTransferResponseHandler handler = new DataTransferResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.dataTransfer(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int getConfiguration(GetConfigurationParams params) {
        GetConfigurationRequest req = this.prepareGetConfiguration(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Get Configuration", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            GetConfigurationResponseHandler handler =
                    new GetConfigurationResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.getConfiguration(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int getLocalListVersion(MultipleChargePointSelect params) {
        GetLocalListVersionRequest req = this.prepareGetLocalListVersion();
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Get Local List Version", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            GetLocalListVersionResponseHandler handler =
                    new GetLocalListVersionResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.getLocalListVersion(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    public int sendLocalList(SendLocalListParams params) {
        SendLocalListRequest req = this.prepareSendLocalList(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Send Local List", chargePointSelectList);

        for (ChargePointSelect c : chargePointSelectList) {
            SendLocalListResponseHandler handler = new SendLocalListResponseHandler(requestTask, c.getChargeBoxId());
            dispatcher.sendLocalList(c, req, handler);
        }
        return requestTaskStore.add(requestTask);
    }

    // -------------------------------------------------------------------------
    // Single Execution
    // -------------------------------------------------------------------------

    public int remoteStartTransaction(RemoteStartTransactionParams params) {
        RemoteStartTransactionRequest req = this.prepareRemoteStartTransaction(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Remote Start Transaction", chargePointSelectList);

        ChargePointSelect c = chargePointSelectList.get(0);
        RemoteStartTransactionResponseHandler handler =
                new RemoteStartTransactionResponseHandler(requestTask, c.getChargeBoxId());
        dispatcher.remoteStartTransaction(c, req, handler);
        return requestTaskStore.add(requestTask);
    }

    public int remoteStopTransaction(RemoteStopTransactionParams params) {
        RemoteStopTransactionRequest req = this.prepareRemoteStopTransaction(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Remote Stop Transaction", chargePointSelectList);

        ChargePointSelect c = chargePointSelectList.get(0);
        RemoteStopTransactionResponseHandler handler =
                new RemoteStopTransactionResponseHandler(requestTask, c.getChargeBoxId());
        dispatcher.remoteStopTransaction(c, req, handler);
        return requestTaskStore.add(requestTask);
    }

    public int unlockConnector(UnlockConnectorParams params) {
        UnlockConnectorRequest req = this.prepareUnlockConnector(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Unlock Connector", chargePointSelectList);

        ChargePointSelect c = chargePointSelectList.get(0);
        UnlockConnectorResponseHandler handler = new UnlockConnectorResponseHandler(requestTask, c.getChargeBoxId());
        dispatcher.unlockConnector(c, req, handler);
        return requestTaskStore.add(requestTask);
    }

    public int reserveNow(ReserveNowParams params) {
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        ChargePointSelect c = chargePointSelectList.get(0);
        String chargeBoxId = c.getChargeBoxId();

        // Insert into DB
        DateTime startTimestamp = DateTime.now();
        DateTime expiryTimestamp = params.getExpiry().toDateTime();
        int reservationId = reservationRepository.insert(params.getIdTag(), chargeBoxId,
                                                         startTimestamp, expiryTimestamp);

        ReserveNowRequest req = this.prepareReserveNow(params, reservationId);
        RequestTask requestTask = new RequestTask(VERSION, "Reserve Now", chargePointSelectList);
        ReserveNowResponseHandler handler = new ReserveNowResponseHandler(requestTask, chargeBoxId,
                reservationRepository, reservationId);
        dispatcher.reserveNow(c, req, handler);
        return requestTaskStore.add(requestTask);
    }

    public int cancelReservation(CancelReservationParams params) {
        CancelReservationRequest req = this.prepareCancelReservation(params);
        List<ChargePointSelect> chargePointSelectList = params.getChargePointSelectList();
        RequestTask requestTask = new RequestTask(VERSION, "Cancel Reservation", chargePointSelectList);

        ChargePointSelect c = chargePointSelectList.get(0);
        CancelReservationResponseHandler handler =
                new CancelReservationResponseHandler(requestTask, c.getChargeBoxId(),
                        reservationRepository, params.getReservationId());
        dispatcher.cancelReservation(c, req, handler);
        return requestTaskStore.add(requestTask);
    }
}
