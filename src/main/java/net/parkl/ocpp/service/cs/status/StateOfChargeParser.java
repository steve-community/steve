package net.parkl.ocpp.service.cs.status;

import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.module.esp.model.ESPMeterValues;
import net.parkl.ocpp.service.OcppConstants;
import ocpp.cs._2015._10.SampledValue;
import org.springframework.stereotype.Component;

@Component
public class StateOfChargeParser implements MeterValueParser {
    public void parseMeterValue(ESPMeterValues values, TransactionStart transactionStart, SampledValue sampledValue) {
        if (sampledValue.isSetMeasurand() && OcppConstants.MEASURAND_SOC.equals(sampledValue.getMeasurand().value())) {
            values.setSoc(Float.parseFloat(sampledValue.getValue()));
        }
    }
}
