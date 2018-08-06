package de.rwth.idsg.steve.web.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 06.08.2018
 */
@Getter
public enum EndpointInfo {
    INSTANCE;

    private final ItemsWithInfo webInterface = new ItemsWithInfo("Access the web interface using", "/manager/home");
    private final ItemsWithInfo ocppSoap = new ItemsWithInfo("SOAP endpoint for OCPP", "/services/CentralSystemService");
    private final ItemsWithInfo ocppWebSocket = new ItemsWithInfo("WebSocket/JSON endpoint for OCPP", "/websocket/CentralSystemService/(chargeBoxId)");

    @Getter
    public static class ItemsWithInfo {
        private final String info;
        private final String dataElementPostFix;
        private List<String> data;

        private ItemsWithInfo(String info, String dataElementPostFix) {
            this.info = info;
            this.dataElementPostFix = dataElementPostFix;
            this.data = Collections.emptyList();
        }

        public synchronized void setData(List<String> data) {
            this.data = data.stream()
                            .map(s -> s + dataElementPostFix)
                            .collect(Collectors.toList());
        }
    }
}
