package net.parkl.ocpp.service;

import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.module.esp.model.*;

import java.util.List;


/**
 * Middleware interface between the e-mobility service provider (ESP) backend and the SteVe Pluggable library.<br>
 * This interface publishes methods to handle incoming events from the ESP backend.<br>
 * @see net.parkl.ocpp.module.esp.EmobilityServiceProvider
 * @author andor
 *
 */
public interface OcppMiddleware {

	/**
	 * Tries to start a charging process on the OCPP server (SteVe Pluggable)
	 * @param request Charging start request (from the ESP backend)
	 * @return Charging start result
	 */
	ESPChargingStartResult startCharging(ESPChargingStartRequest request);

	/**
	 * Requests the status of a charging process
	 * @param externalChargeId The ID of the charging process on the OCPP server (SteVe Pluggable)
	 * @return Status result
	 */
	ESPChargingStatusResult getStatus(String externalChargeId);

	/**
	 * Tries to stop a charging process on the OCPP server (SteVe Pluggable)
	 * @param req Töltés leállítási kérelem
	 * @return Töltés leállítás eredménye
	 */
	ESPChargingResult stopCharging(ESPChargingUserStopRequest req);

	/**
	 * Queries the configuration settings of a charge box from the charge box itself
	 * @param chargeBoxId Charge box ID on the OCPP server (SteVe Pluggable)
	 * @return List of active configuration settings for the charge box
	 */
	List<ESPChargeBoxConfiguration> getChargeBoxConfiguration(String chargeBoxId);
	
	/**
	 * Sets a configuration setting on the charge box itself
	 * @param chargeBoxId Charge box ID on the OCPP server
	 * @param key The key to set
	 * @param value New value
	 * @return List of active configuration settings for the charge box
	 */
	List<ESPChargeBoxConfiguration> changeChargeBoxConfiguration(String chargeBoxId,String key, String value);

	/**
	 * Registers a new charge box on the OCPP server (SteVe Pluggable)
	 * @param chargeBoxId Unique charge box ID on the OCPP server
	 */
	void registerChargeBox(String chargeBoxId);
	/**
	 * Unregisters an existing charge box from the OCPP server (SteVe Pluggable)
	 * @param chargeBoxId Charge box ID on the OCPP server
	 */
	void unregisterChargeBox(String chargeBoxId);
	/**
	 * Sets the availability of a connector (a single charger head) on the OCPP server
	 * @param chargeBoxId Charge box ID on the OCPP server
	 * @param chargerId Connector ID
	 * @param available True if available
	 */
	void changeAvailability(String chargeBoxId,String chargerId,boolean available);
	/**
	 * Unlocks the connector cable on the specified connector (ChargeBox+Connector)
	 * @param chargeBoxId Charge box ID on the OCPP server
	 * @param chargerId Connector ID
	 */
	void unlockConnector(String chargeBoxId,String chargerId);
	/**
	 * Resets a charge box
	 * @param chargeBoxId Charge box ID on the OCPP server
	 * @param soft True for soft reset, false for hard reset
	 */
	void resetChargeBox(String chargeBoxId,boolean soft);
	
	
	void stopChargingExternal(OcppChargingProcess process, String reason);

	/**
	 * Updates consumption data of a charging process asynchronously towards the ESP backend (after the charging has stopped)
	 * @param process Charging process on the OCPP server
	 * @param startValue Transaction start value
	 * @param stopValue Transaction stop value
	 */
	void updateConsumption(OcppChargingProcess process, String startValue, String stopValue);
	
	boolean isConnectorCharging(String chargeBoxId, int connectorId);
	void registerConsumptionListener(OcppConsumptionListener l);

	void registerStopListener(OcppStopListener l);

	ESPChargerStatusResult getChargerStatuses();
	
	ESPChargerState getChargerStatus(String chargeBoxId, int connectorId);

	PowerValue getPowerValue(TransactionStart transaction);

        void stopChargingWithLimit(String chargingProcessId, float totalPower);

	void stopChargingWithPreparingTimeout(String chargingProcessId);

	void sendHeartBeatOfflineAlert(String chargeBoxId);

	void notifyAboutRfidStart(ESPRfidChargingStartRequest startRequest);

	boolean checkRfidTag(String rfidTag, String chargeBoxId);
}
