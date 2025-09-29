package de.rwth.idsg.steve.ocpp20.ws.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class Ocpp20MessageHandlerRegistry {

    private final Map<String, Ocpp20MessageHandler<?, ?>> handlers = new HashMap<>();
    private final ObjectMapper objectMapper;

    public Ocpp20MessageHandlerRegistry(List<Ocpp20MessageHandler<?, ?>> handlerList, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        for (Ocpp20MessageHandler<?, ?> handler : handlerList) {
            handlers.put(handler.getAction(), handler);
            log.debug("Registered OCPP 2.0 handler for action: {}", handler.getAction());
        }
        log.info("Registered {} OCPP 2.0 message handlers", handlers.size());
    }

    public Object dispatch(String action, JsonNode payloadNode, String chargeBoxId) throws Exception {
        Ocpp20MessageHandler<?, ?> handler = handlers.get(action);
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported action: " + action);
        }

        return handler.handleJson(payloadNode, chargeBoxId, objectMapper);
    }

    public boolean isActionSupported(String action) {
        return handlers.containsKey(action);
    }
}