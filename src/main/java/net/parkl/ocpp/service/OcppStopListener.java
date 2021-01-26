package net.parkl.ocpp.service;

import net.parkl.ocpp.module.esp.model.ESPChargingData;
import net.parkl.ocpp.entities.OcppChargingProcess;


public interface OcppStopListener {

	void chargingStopped(OcppChargingProcess process, ESPChargingData chargingData, String reason);

}
