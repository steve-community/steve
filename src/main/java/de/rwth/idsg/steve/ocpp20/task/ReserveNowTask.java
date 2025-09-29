package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.IdToken;
import de.rwth.idsg.steve.ocpp20.model.ReserveNowRequest;
import de.rwth.idsg.steve.ocpp20.model.ReserveNowResponse;
import lombok.Getter;
import org.joda.time.DateTime;

import java.util.List;

@Getter
public class ReserveNowTask extends Ocpp20Task<ReserveNowRequest, ReserveNowResponse> {

    private final Integer id;
    private final DateTime expiryDateTime;
    private final IdToken idToken;
    private final Integer evseId;
    private final String groupIdToken;

    public ReserveNowTask(List<String> chargeBoxIdList, Integer id, DateTime expiryDateTime,
                         IdToken idToken, Integer evseId, String groupIdToken) {
        super("ReserveNow", chargeBoxIdList);
        this.id = id;
        this.expiryDateTime = expiryDateTime;
        this.idToken = idToken;
        this.evseId = evseId;
        this.groupIdToken = groupIdToken;
    }

    @Override
    public ReserveNowRequest createRequest() {
        ReserveNowRequest request = new ReserveNowRequest();
        request.setId(id);
        request.setExpiryDateTime(java.time.OffsetDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(expiryDateTime.getMillis()),
            java.time.ZoneOffset.UTC));
        request.setIdToken(idToken);

        if (evseId != null) {
            request.setEvseId(evseId);
        }
        if (groupIdToken != null) {
            IdToken groupToken = new IdToken();
            groupToken.setIdToken(groupIdToken);
            request.setGroupIdToken(groupToken);
        }

        return request;
    }

    @Override
    public Class<ReserveNowResponse> getResponseClass() {
        return ReserveNowResponse.class;
    }
}