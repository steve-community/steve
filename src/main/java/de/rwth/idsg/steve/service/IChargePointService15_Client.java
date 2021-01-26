package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListParams;

public interface IChargePointService15_Client extends IChargePointService12_Client {

	int getConfiguration(GetConfigurationParams params);

	int getLocalListVersion(MultipleChargePointSelect params);

	int sendLocalList(SendLocalListParams params);

	int changeConfiguration(ChangeConfigurationParams params);

}
