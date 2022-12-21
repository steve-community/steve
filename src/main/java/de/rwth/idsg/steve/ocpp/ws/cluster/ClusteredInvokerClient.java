package de.rwth.idsg.steve.ocpp.ws.cluster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.web.dto.ocpp.ChargePointSelection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static java.util.Collections.singletonList;

@Component
@RequiredArgsConstructor
public class ClusteredInvokerClient {
    private RestTemplate restTemplate;
    private final ClusteredWebSocketSessionStore clusteredWebSocketSessionStore;

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
    }
    @SneakyThrows
    public void invoke(String chargeBoxId, String payload) {
        ClusteredInvocationRequest request = new ClusteredInvocationRequest(chargeBoxId,
                payload, ClusteredWebSocketHelper.getPodIp());

        postFor(getPodUrl(chargeBoxId), request, Void.class);
    }

    private String getPodUrl(String chargeBoxId) {
        return String.format("http://%s/api/cluster/invoke",
                clusteredWebSocketSessionStore.getChargeBoxIp(chargeBoxId));
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

    public void callback(String chargeBoxId, ResponseType payload, String originPodIp) {
    }

    public void errorCallback(String chargeBoxId, ResponseType payload, String originPodIp) {
    }
}
