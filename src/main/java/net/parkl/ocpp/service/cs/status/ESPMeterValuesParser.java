package net.parkl.ocpp.service.cs.status;

import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.module.esp.model.ESPMeterValues;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.SampledValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ESPMeterValuesParser {
    @Autowired
    private ApplicationContext applicationContext;
    private static final List<Class<? extends MeterValueParser>> PARSERS = List.of(
            ActivePowerParser.class,
            StateOfChargeParser.class,
            TotalEnergyParser.class
    );
    public ESPMeterValues parseMeterValues(TransactionStart transactionStart, List<MeterValue> meterValues) {
        ESPMeterValues values = new ESPMeterValues();
        for (MeterValue meterValue : meterValues) {
            for (SampledValue sampledValue : meterValue.getSampledValue()) {
                for (Class<? extends MeterValueParser> parserClass : PARSERS) {
                    applicationContext.getBean(parserClass).parseMeterValue(values, transactionStart, sampledValue);
                }
            }
        }
        return values;
    }
}
