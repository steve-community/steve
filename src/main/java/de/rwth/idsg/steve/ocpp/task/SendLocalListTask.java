package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.service.OcppTagService;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListParams;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListUpdateType;

import javax.xml.ws.AsyncHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class SendLocalListTask extends CommunicationTask<SendLocalListParams, String> {

    private final Object requestLock = new Object();
    private final OcppTagService ocppTagService;

    private ocpp.cp._2015._10.SendLocalListRequest cachedOcpp16Request;

    public SendLocalListTask(OcppVersion ocppVersion, SendLocalListParams params, OcppTagService ocppTagService) {
        super(ocppVersion, params);
        this.ocppTagService = ocppTagService;
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

    @Override
    public ocpp.cp._2012._06.SendLocalListRequest getOcpp15Request() {
        ocpp.cp._2015._10.SendLocalListRequest ocpp16Request = createOcpp16Request();

        return new ocpp.cp._2012._06.SendLocalListRequest()
                .withListVersion(ocpp16Request.getListVersion())
                .withUpdateType(ocpp.cp._2012._06.UpdateType.fromValue(ocpp16Request.getUpdateType().value()))
                .withLocalAuthorisationList(toOcpp15(ocpp16Request.getLocalAuthorizationList()));
    }

    @Override
    public ocpp.cp._2015._10.SendLocalListRequest getOcpp16Request() {
        synchronized (requestLock) {
            if (cachedOcpp16Request == null) {
                cachedOcpp16Request = createOcpp16Request();
            }
            return cachedOcpp16Request;
        }
    }

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp12Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.SendLocalListResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.SendLocalListResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private ocpp.cp._2015._10.SendLocalListRequest createOcpp16Request() {
        // DIFFERENTIAL update
        if (params.getUpdateType() == SendLocalListUpdateType.DIFFERENTIAL) {
            List<ocpp.cp._2015._10.AuthorizationData> auths = new ArrayList<>();

            // Step 1: For the idTags to be deleted, insert only the idTag
            for (String idTag : params.getDeleteList()) {
                auths.add(new ocpp.cp._2015._10.AuthorizationData().withIdTag(idTag));
            }

            // Step 2: For the idTags to be added or updated, insert them with their IdTagInfos
            auths.addAll(ocppTagService.getAuthData(params.getAddUpdateList()));

            return new ocpp.cp._2015._10.SendLocalListRequest()
                    .withListVersion(params.getListVersion())
                    .withUpdateType(ocpp.cp._2015._10.UpdateType.DIFFERENTIAL)
                    .withLocalAuthorizationList(auths);

        // FULL update
        } else {
            return new ocpp.cp._2015._10.SendLocalListRequest()
                    .withListVersion(params.getListVersion())
                    .withUpdateType(ocpp.cp._2015._10.UpdateType.FULL)
                    .withLocalAuthorizationList(ocppTagService.getAuthDataOfAllTags());
        }
    }

    private static List<ocpp.cp._2012._06.AuthorisationData> toOcpp15(List<ocpp.cp._2015._10.AuthorizationData> ocpp16) {
        return ocpp16.stream()
                     .map(k -> new ocpp.cp._2012._06.AuthorisationData().withIdTag(k.getIdTag())
                                                                        .withIdTagInfo(toOcpp15(k.getIdTagInfo())))
                     .collect(Collectors.toList());
    }

    private static ocpp.cp._2012._06.IdTagInfo toOcpp15(ocpp.cp._2015._10.IdTagInfo ocpp16) {
        return new ocpp.cp._2012._06.IdTagInfo()
                .withParentIdTag(ocpp16.getParentIdTag())
                .withExpiryDate(ocpp16.getExpiryDate())
                .withStatus(ocpp.cp._2012._06.AuthorizationStatus.fromValue(ocpp16.getStatus().value()));
    }
}
