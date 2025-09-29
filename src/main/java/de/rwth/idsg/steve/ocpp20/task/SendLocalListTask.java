package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.AuthorizationData;
import de.rwth.idsg.steve.ocpp20.model.SendLocalListRequest;
import de.rwth.idsg.steve.ocpp20.model.SendLocalListResponse;
import de.rwth.idsg.steve.ocpp20.model.UpdateEnum;
import lombok.Getter;

import java.util.List;

@Getter
public class SendLocalListTask extends Ocpp20Task<SendLocalListRequest, SendLocalListResponse> {

    private final Integer versionNumber;
    private final UpdateEnum updateType;
    private final List<AuthorizationData> localAuthorizationList;

    public SendLocalListTask(List<String> chargeBoxIdList, Integer versionNumber,
                           UpdateEnum updateType, List<AuthorizationData> localAuthorizationList) {
        super("SendLocalList", chargeBoxIdList);
        this.versionNumber = versionNumber;
        this.updateType = updateType;
        this.localAuthorizationList = localAuthorizationList;
    }

    @Override
    public SendLocalListRequest createRequest() {
        SendLocalListRequest request = new SendLocalListRequest();
        request.setVersionNumber(versionNumber);
        request.setUpdateType(updateType);

        if (localAuthorizationList != null && !localAuthorizationList.isEmpty()) {
            request.setLocalAuthorizationList(localAuthorizationList);
        }

        return request;
    }

    @Override
    public Class<SendLocalListResponse> getResponseClass() {
        return SendLocalListResponse.class;
    }
}