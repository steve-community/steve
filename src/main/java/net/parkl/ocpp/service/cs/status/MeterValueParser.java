package net.parkl.ocpp.service.cs.status;

import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.module.esp.model.ESPMeterValues;
import ocpp.cs._2015._10.SampledValue;

public interface MeterValueParser {
    void parseMeterValue(ESPMeterValues values, TransactionStart transactionStart, SampledValue sampledValue);
}
