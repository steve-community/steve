package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp.UnlockConnectorParams;

public interface IChargePointService12_Client {

	int remoteStopTransaction(RemoteStopTransactionParams params);

	int changeAvailability(ChangeAvailabilityParams params);

	int remoteStartTransaction(RemoteStartTransactionParams params);

	int unlockConnector(UnlockConnectorParams params);

	int reset(ResetParams params);

}
