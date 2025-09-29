package de.rwth.idsg.steve.ocpp20.ws.handler;

import de.rwth.idsg.steve.ocpp20.model.BootNotificationRequest;
import de.rwth.idsg.steve.ocpp20.model.BootNotificationResponse;
import de.rwth.idsg.steve.ocpp20.service.CentralSystemService20;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class BootNotificationHandler implements Ocpp20MessageHandler<BootNotificationRequest, BootNotificationResponse> {

    private final CentralSystemService20 centralSystemService;

    @Override
    public String getAction() {
        return "BootNotification";
    }

    @Override
    public Class<BootNotificationRequest> getRequestClass() {
        return BootNotificationRequest.class;
    }

    @Override
    public BootNotificationResponse handle(BootNotificationRequest request, String chargeBoxId) {
        return centralSystemService.handleBootNotification(request, chargeBoxId);
    }
}