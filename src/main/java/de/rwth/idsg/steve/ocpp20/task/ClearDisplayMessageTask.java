package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.ClearDisplayMessageRequest;
import de.rwth.idsg.steve.ocpp20.model.ClearDisplayMessageResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class ClearDisplayMessageTask extends Ocpp20Task<ClearDisplayMessageRequest, ClearDisplayMessageResponse> {

    private final Integer messageId;

    public ClearDisplayMessageTask(List<String> chargeBoxIdList, Integer messageId) {
        super("ClearDisplayMessage", chargeBoxIdList);
        this.messageId = messageId;
    }

    @Override
    public ClearDisplayMessageRequest createRequest() {
        ClearDisplayMessageRequest request = new ClearDisplayMessageRequest();
        request.setId(messageId);
        return request;
    }

    @Override
    public Class<ClearDisplayMessageResponse> getResponseClass() {
        return ClearDisplayMessageResponse.class;
    }
}
