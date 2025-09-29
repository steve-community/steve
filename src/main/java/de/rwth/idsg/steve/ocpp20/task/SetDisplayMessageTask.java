package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.SetDisplayMessageRequest;
import de.rwth.idsg.steve.ocpp20.model.SetDisplayMessageResponse;
import de.rwth.idsg.steve.ocpp20.model.MessageInfo;
import de.rwth.idsg.steve.ocpp20.model.MessagePriorityEnum;
import de.rwth.idsg.steve.ocpp20.model.MessageStateEnum;
import lombok.Getter;
import java.util.List;

@Getter
public class SetDisplayMessageTask extends Ocpp20Task<SetDisplayMessageRequest, SetDisplayMessageResponse> {

    private final MessageInfo message;

    public SetDisplayMessageTask(List<String> chargeBoxIdList, MessageInfo message) {
        super("SetDisplayMessage", chargeBoxIdList);
        this.message = message;
    }

    @Override
    public SetDisplayMessageRequest createRequest() {
        SetDisplayMessageRequest request = new SetDisplayMessageRequest();
        request.setMessage(message);
        return request;
    }

    @Override
    public Class<SetDisplayMessageResponse> getResponseClass() {
        return SetDisplayMessageResponse.class;
    }
}
