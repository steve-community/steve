package de.rwth.idsg.steve.service.notification;

import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import lombok.Data;

@Data
public class OcppTransactionEnded {

  private final UpdateTransactionParams params;
}
