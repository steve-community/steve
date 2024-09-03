package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageParams;

public interface IChargePointService16_Client extends IChargePointService15_Client {
    int triggerMessage(TriggerMessageParams params);
}
