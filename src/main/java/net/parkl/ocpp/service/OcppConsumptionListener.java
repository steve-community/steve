package net.parkl.ocpp.service;

import net.parkl.ocpp.module.esp.model.ESPChargingConsumptionRequest;

public interface OcppConsumptionListener {

	void consumptionUpdated(ESPChargingConsumptionRequest req);

}
