package de.rwth.idsg.steve.service.notification;

import java.util.Optional;
import lombok.Data;
import ocpp.cs._2015._10.RegistrationStatus;

@Data
public class OccpStationBooted {

  private final String chargeBoxId;
  private final Optional<RegistrationStatus> status;
}
