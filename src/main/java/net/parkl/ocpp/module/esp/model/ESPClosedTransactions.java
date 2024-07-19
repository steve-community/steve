package net.parkl.ocpp.module.esp.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ESPClosedTransactions implements Serializable {
    private List<ESPClosedTransaction> transactions;
    private long totalPages;
    private long totalElements;
}
