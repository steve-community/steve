package de.rwth.idsg.steve.ocpp.ws.cluster;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.util.Collections.singletonList;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClusteredInvokerClient {
    private RestTemplate restTemplate;
    private final ClusteredWebSocketSessionStore clusteredWebSocketSessionStore;

    @Value("${ocpp.pod.container.port:8080}")
    private int podContainerPort;

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
    }
    @SneakyThrows
    public void invoke(String chargeBoxId, String messageId, String payload, String responseClassName) {
        log.info("Sending payload to charge box pod {}: {}", chargeBoxId, payload);
        ClusteredInvocationRequest request = new ClusteredInvocationRequest(chargeBoxId, messageId,
                Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8)),
                responseClassName,
                ClusteredWebSocketHelper.getPodIp());

        postFor(getChargeBoxPodUrl(chargeBoxId), request, Void.class);
    }

    private String getChargeBoxPodUrl(String chargeBoxId) {
        return String.format("http://%s:%d/ocpp/api/cluster/invoke",
                clusteredWebSocketSessionStore.getChargeBoxIp(chargeBoxId), podContainerPort);
    }
    private String getCallbackUrl(String podIp) {
        return String.format("http://%s:%d/ocpp/api/cluster/callback", podIp,
                podContainerPort);
    }

    private <T> T postFor(String url, Object request, Class<T> responseClass) throws IOException {
        HttpEntity<String> entity = getHttpEntity(request);
        ResponseEntity<T> response = restTemplate
                .exchange(url, HttpMethod.POST, entity, responseClass);
        return response.getBody();
    }

    @SneakyThrows
    private HttpEntity<String> getHttpEntity(Object request) {
        // create request body
        ObjectMapper objectMapper = new ObjectMapper();
        String input = objectMapper.writeValueAsString(request);

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(input, headers);
    }

    @SneakyThrows
    public void callback(String chargeBoxId, String payload, String originPodIp) {
        log.info("Sending callback to invoking pod {}: {}", chargeBoxId, payload);
        ClusteredInvocationCallback request = new ClusteredInvocationCallback(chargeBoxId,
                Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8)));

        postFor(getCallbackUrl(originPodIp), request, Void.class);
    }

}
