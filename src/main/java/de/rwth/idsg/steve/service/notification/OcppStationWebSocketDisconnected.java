package de.rwth.idsg.steve.service.notification;

import lombok.Data;

@Data
public class OcppStationWebSocketDisconnected {

  private final String chargeBoxId;
}
