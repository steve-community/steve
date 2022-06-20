package de.rwth.idsg.steve.service.notification;

import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import lombok.Data;

@Data
public class OcppTransactionStarted {

  private final int transactionId;
  private final InsertTransactionParams params;
}
