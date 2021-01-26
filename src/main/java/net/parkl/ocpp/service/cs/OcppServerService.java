package net.parkl.ocpp.service.cs;

import java.util.List;

import org.joda.time.DateTime;

import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import ocpp.cs._2012._06.MeterValue;

public interface OcppServerService {

	void updateChargeboxHeartbeat(String chargeBoxId, DateTime now);

	void updateEndpointAddress(String chargeBoxId, String endpointAddress);

	boolean updateChargebox(UpdateChargeboxParams params);

	void updateChargeboxFirmwareStatus(String chargeBoxIdentity, String status);

	void insertConnectorStatus(InsertConnectorStatusParams params);

	void updateChargeboxDiagnosticsStatus(String chargeBoxIdentity, String status);


	Integer insertTransaction(InsertTransactionParams params);

	void updateTransaction(UpdateTransactionParams params);


	void insertMeterValues(String chargeBoxIdentity, List<ocpp.cs._2015._10.MeterValue> meterValue, int connectorId,
			Integer transactionId);

	void insertMeterValues(String chargeBoxIdentity, List<ocpp.cs._2015._10.MeterValue> transactionData,
			int transactionId);

}
