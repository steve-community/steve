package de.rwth.idsg.steve.ocpp20.ws.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface Ocpp20MessageHandler<REQ, RES> {

    String getAction();

    Class<REQ> getRequestClass();

    RES handle(REQ request, String chargeBoxId);

    default RES handleJson(JsonNode payloadNode, String chargeBoxId, ObjectMapper objectMapper) throws Exception {
        String payloadJson = objectMapper.writeValueAsString(payloadNode);
        REQ request = objectMapper.readValue(payloadJson, getRequestClass());
        return handle(request, chargeBoxId);
    }
}