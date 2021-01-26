package net.parkl.ocpp.module.esp;

import net.parkl.ocpp.module.esp.model.ESPChargingConsumptionRequest;
import net.parkl.ocpp.module.esp.model.ESPChargingStopRequest;
import net.parkl.ocpp.module.esp.model.ESPRfidChargingStartRequest;

/**
 * Interface for the communication with an e-mobility service provider, initiated by the SteVe Pluggable library (outgoing events).<br>
 * Implementors are free to use any communication protocols to any e-mobility service provider (ESP) backends.
 * @see net.parkl.ocpp.service.EmobilityServiceProviderFacade
 */
public interface EmobilityServiceProvider {
    /**
     * Stops a specific charging from the charger side
     * @param req Stop charging request (from the charger)
     */
    void stopChargingExternal(ESPChargingStopRequest req);

    /**
     * Updates the consumption of a charging process asynchronously, after the charging has stopped.
     * @param req Consumption request (from the charger)
     */
    void updateChargingConsumptionExternal(ESPChargingConsumptionRequest req);

    /**
     * Notifies the e-mobility service provider of a charge box failing to send heartbeat messages
     * @param chargeBoxId Charge box not sending heartbeat
     */
    void sendHeartBeatOfflineAlert(String chargeBoxId);

    /**
     * Checks if an ID (RFID) tag is authorized to use a specific charge box
     * @param rfidTag ID tag
     * @param chargeBoxId Charge box ID
     * @return True if the tag is authorized to use the charge box
     */
    boolean checkRfidTag(String rfidTag, String chargeBoxId);

    /**
     * Notifies the e-mobility service provider of a charging process started by an RFID tag
     * @param startRequest RFID tag start request (from the charger)
     */
    void notifyAboutRfidStart(ESPRfidChargingStartRequest startRequest);
}
