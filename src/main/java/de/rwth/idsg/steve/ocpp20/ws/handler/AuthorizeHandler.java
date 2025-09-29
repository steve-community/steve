package de.rwth.idsg.steve.ocpp20.ws.handler;

import de.rwth.idsg.steve.ocpp20.model.AuthorizeRequest;
import de.rwth.idsg.steve.ocpp20.model.AuthorizeResponse;
import de.rwth.idsg.steve.ocpp20.service.CentralSystemService20;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class AuthorizeHandler implements Ocpp20MessageHandler<AuthorizeRequest, AuthorizeResponse> {

    private final CentralSystemService20 centralSystemService;

    @Override
    public String getAction() {
        return "Authorize";
    }

    @Override
    public Class<AuthorizeRequest> getRequestClass() {
        return AuthorizeRequest.class;
    }

    @Override
    public AuthorizeResponse handle(AuthorizeRequest request, String chargeBoxId) {
        return centralSystemService.handleAuthorize(request, chargeBoxId);
    }
}