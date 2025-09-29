package de.rwth.idsg.steve.ocpp20.ws.handler;

import de.rwth.idsg.steve.ocpp20.model.HeartbeatRequest;
import de.rwth.idsg.steve.ocpp20.model.HeartbeatResponse;
import de.rwth.idsg.steve.ocpp20.service.CentralSystemService20;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class HeartbeatHandler implements Ocpp20MessageHandler<HeartbeatRequest, HeartbeatResponse> {

    private final CentralSystemService20 centralSystemService;

    @Override
    public String getAction() {
        return "Heartbeat";
    }

    @Override
    public Class<HeartbeatRequest> getRequestClass() {
        return HeartbeatRequest.class;
    }

    @Override
    public HeartbeatResponse handle(HeartbeatRequest request, String chargeBoxId) {
        return centralSystemService.handleHeartbeat(request, chargeBoxId);
    }
}