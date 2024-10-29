package net.parkl.ocpp.service.cs.status;

import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.module.esp.model.ESPMeterValues;
import ocpp.cs._2015._10.SampledValue;
import org.springframework.stereotype.Component;

import static net.parkl.ocpp.service.OcppConstants.MEASURAND_POWER_ACTIVE_IMPORT;
import static net.parkl.ocpp.service.OcppConsumptionHelper.getKwValue;

@Component
public class ActivePowerParser implements MeterValueParser {
    public void parseMeterValue(ESPMeterValues values, TransactionStart transactionStart, SampledValue sampledValue) {
        if (sampledValue.isSetMeasurand() && MEASURAND_POWER_ACTIVE_IMPORT.equals(sampledValue.getMeasurand().value())) {
            values.setActivePowerImport(getKwValue(Float.parseFloat(sampledValue.getValue()),
                    sampledValue.getUnit().value()));
        }
    }
}
