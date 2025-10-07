package net.parkl.ocpp.analytics;

import net.parkl.analytics.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AnalyticsClient {

    private final WebClient webClient;

    public AnalyticsClient(@Value("${analytics.service.host}") String host,
                           @Value("${analytics.service.port}") int port,
                           WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(String.format("http://%s:%d/analytics/v2", host, port))
                .build();
    }

    public Mono<ChargerConnection> createConnection(ChargerConnectionRequest req) {
        return webClient.post()
                .uri("/charger-connection")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(ChargerConnection.class);
    }

    public Mono<ChargerConnection> updateConnection(ChargerConnectionDisconnectRequest req) {
        return webClient.post()
                .uri("/charger-connection/disconnect")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(ChargerConnection.class);
    }

    public Mono<ChargerConnection> updateLastSeen(ChargerConnectionHeartbeatRequest req) {
        return webClient.post()
                .uri("/charger-connection/heartbeat")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(ChargerConnection.class);
    }
}
