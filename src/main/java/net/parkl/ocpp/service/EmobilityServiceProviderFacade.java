package net.parkl.ocpp.service;

import java.util.List;

import net.parkl.ocpp.module.esp.model.*;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.TransactionStart;


/**
 * Homlokzati interface a Parkl szerver és az OCPP szerver közötti kommunikációra.
 * @author andor
 *
 */
public interface EmobilityServiceProviderFacade {

	/**
	 * Megkísérel elindítani egy töltési folyamatot az OCPP szerveren
	 * @param request Töltési kérelem
	 * @return Töltés indítás eredménye
	 */
	ESPChargingStartResult startCharging(ESPChargingStartRequest request);

	/**
	 * Lekéri egy töltési folyamat státuszát az OCPP szervertől
	 * @param externalChargeId Töltés azonosítója az OCPP szerveren
	 * @return Státusz lekérés eredménye
	 */
	ESPChargingStatusResult getStatus(String externalChargeId);

	/**
	 * Megkísérel leállítani egy töltési folyamatot az OCPP szerveren
	 * @param req Töltés leállítási kérelem
	 * @return Töltés leállítás eredménye
	 */
	ESPChargingResult stopCharging(ESPChargingUserStopRequest req);

	/**
	 * Lekéri egy töltő (ChargeBox) beállításait magától a töltőtől
	 * @param chargeBoxId Töltő az OCPP szerveren egyedi azonosítója
	 * @return Beállítások listája
	 */
	List<ESPChargeBoxConfiguration> getChargeBoxConfiguration(String chargeBoxId);
	
	/**
	 * Átállítja egy töltő (ChargeBox) egy megadott beállítását magán a töltőn
	 * @param chargeBoxId Töltő az OCPP szerveren egyedi azonosítója
	 * @param key Átállítandó kulcs
	 * @param value Új érték
	 * @return Beállítások listája a mentés után
	 */
	List<ESPChargeBoxConfiguration> changeChargeBoxConfiguration(String chargeBoxId,String key, String value);

	/**
	 * OCPP szerver oldalon beregisztrál egy új töltőt (ChargeBox)
	 * @param chargeBoxId Töltő az OCPP szerveren egyedi azonosítója
	 */
	void registerChargeBox(String chargeBoxId);
	/**
	 * OCPP szerver oldalon töröl egy regisztrált töltőt (ChargeBox)
	 * @param chargeBoxId Töltő az OCPP szerveren egyedi azonosítója
	 */
	void unregisterChargeBox(String chargeBoxId);
	/**
	 * OCPP szerver oldalon beállítja egy töltőfej (Connector) elérhetőségét
	 * @param chargeBoxId Töltő az OCPP szerveren egyedi azonosítója
	 * @param chargerId Töltőfej azonosító
	 * @param available Igaz, ha elérhető
	 */
	void changeAvailability(String chargeBoxId,String chargerId,boolean available);
	/**
	 * Kioldja a kábelt a megadott töltőfejen (ChargeBox+Connector) 
	 * @param chargeBoxId Töltő az OCPP szerveren egyedi azonosítója
	 * @param chargerId Töltőfej azonosító
	 */
	void unlockConnector(String chargeBoxId,String chargerId);
	/**
	 * Újraindít egy töltőt (ChargeBox)
	 * @param chargeBoxId Töltő az OCPP szerveren egyedi azonosítója
	 * @param soft Igaz, ha soft reset, hamis, ha hard reset
	 */
	void resetChargeBox(String chargeBoxId,boolean soft);
	
	
	void stopChargingExternal(OcppChargingProcess process, String reason);

	/**
	 * Aszinkron módon updateli egy töltési folyamat fogyasztási adatait a Parkl szerver felé
	 * @param process Töltési folyamat az OCPP szerveren
	 * @param startValue Mérőállás a tranzakció kezdetén
	 * @param stopValue Mérőállás a tranzakció végén
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

	void notifyParklAboutRfidStart(ESPRfidChargingStartRequest startRequest);

	boolean checkRfidTag(String rfidTag, String chargeBoxId);
}
