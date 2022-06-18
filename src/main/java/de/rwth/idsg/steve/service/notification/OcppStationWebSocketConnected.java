package de.rwth.idsg.steve.service.notification;

import lombok.Data;

@Data
public class OcppStationWebSocketConnected {

  private final String chargeBoxId;
}
