package net.parkl.ocpp.service;

import java.util.List;

import net.parkl.ocpp.entities.ConnectorMeterValue;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.TransactionStart;

public interface OcppProxyService {
	OcppChargingProcess findOpenChargingProcessWithoutTransaction(String chargeBoxId, int connectorId);
	OcppChargingProcess findOpenChargingProcess(String chargeBoxId, int connectorId);
	OcppChargingProcess createChargingProcess(String chargeBoxId,int connectorId,String idTag, String licensePlate, Float limitKwh);

	OcppChargingProcess findOcppChargingProcess(String processId);

	OcppChargingProcess stopChargingProcess(String processId);

	List<ConnectorMeterValue> getConnectorMeterValueByTransactionAndMeasurand(TransactionStart transaction, String measurand);

	OcppChargingProcess stopRequested(String processId);
	ConnectorMeterValue getLastConnectorMeterValueByTransactionAndMeasurand(TransactionStart transaction, String measurand);
	OcppChargingProcess stopRequestCancelled(String externalChargeId);
	List<OcppChargingProcess> getActiveProcessesByChargeBox(String chargeBoxId);
	List<OcppChargingProcess> findOpenChargingProcessesWithoutTransaction();
	OcppChargingProcess checkForChargingProcessWithoutTransaction(String chargeBoxId, int connectorId);
	boolean isWaitingForChargingProcess(String chargeBoxId);

	List<OcppChargingProcess> findOpenChargingProcessesWithLimit();

}
