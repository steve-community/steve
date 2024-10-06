package net.parkl.ocpp.module.esp.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ESPConnectorStopResults {
    private int transactionsCleanedUp;
    private int processesCleanedUp;
}
