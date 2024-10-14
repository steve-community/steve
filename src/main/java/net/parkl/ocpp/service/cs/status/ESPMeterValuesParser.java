package net.parkl.ocpp.service.cs.status;

import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.module.esp.model.ESPMeterValues;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.SampledValue;

import java.util.List;

public class ESPMeterValuesParser {
    private static final List<MeterValueParser> PARSERS = List.of(
            new ActivePowerParser(),
            new StateOfChargeParser(),
            new TotalEnergyParser()
    );
    public static ESPMeterValues parseMeterValues(TransactionStart transactionStart, List<MeterValue> meterValues) {
        ESPMeterValues values = new ESPMeterValues();
        for (MeterValue meterValue : meterValues) {
            for (SampledValue sampledValue : meterValue.getSampledValue()) {
                for (MeterValueParser parser : PARSERS) {
                    parser.parseMeterValue(values, transactionStart, sampledValue);
                }
            }
        }
        return values;
    }
}
