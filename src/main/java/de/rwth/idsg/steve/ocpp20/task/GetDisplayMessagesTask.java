package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.GetDisplayMessagesRequest;
import de.rwth.idsg.steve.ocpp20.model.GetDisplayMessagesResponse;
import de.rwth.idsg.steve.ocpp20.model.MessagePriorityEnum;
import de.rwth.idsg.steve.ocpp20.model.MessageStateEnum;
import lombok.Getter;
import java.util.List;

@Getter
public class GetDisplayMessagesTask extends Ocpp20Task<GetDisplayMessagesRequest, GetDisplayMessagesResponse> {

    private final Integer requestId;
    private final List<Integer> messageIds;
    private final MessagePriorityEnum priority;
    private final MessageStateEnum state;

    public GetDisplayMessagesTask(List<String> chargeBoxIdList, Integer requestId,
                                  List<Integer> messageIds, MessagePriorityEnum priority,
                                  MessageStateEnum state) {
        super("GetDisplayMessages", chargeBoxIdList);
        this.requestId = requestId;
        this.messageIds = messageIds;
        this.priority = priority;
        this.state = state;
    }

    @Override
    public GetDisplayMessagesRequest createRequest() {
        GetDisplayMessagesRequest request = new GetDisplayMessagesRequest();
        request.setRequestId(requestId);

        if (messageIds != null && !messageIds.isEmpty()) {
            request.setId(messageIds);
        }
        if (priority != null) {
            request.setPriority(priority);
        }
        if (state != null) {
            request.setState(state);
        }

        return request;
    }

    @Override
    public Class<GetDisplayMessagesResponse> getResponseClass() {
        return GetDisplayMessagesResponse.class;
    }
}
