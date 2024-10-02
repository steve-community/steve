package net.parkl.ocpp.module.esp.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ESPConnectorStopResult {
    private String externalProcessId;
    private Integer transactionId;
    private boolean success;
    private String error;
}
