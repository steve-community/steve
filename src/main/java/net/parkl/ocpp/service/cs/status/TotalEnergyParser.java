package net.parkl.ocpp.service.cs.status;

import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.module.esp.model.ESPMeterValues;
import net.parkl.ocpp.service.OcppConstants;
import ocpp.cs._2015._10.SampledValue;

import static net.parkl.ocpp.service.OcppConsumptionHelper.getKwhValue;

public class TotalEnergyParser implements MeterValueParser {
    public void parseMeterValue(ESPMeterValues values, TransactionStart transactionStart, SampledValue sampledValue) {
        boolean measurandEnergyActiveImport = sampledValue.isSetMeasurand() &&
                OcppConstants.MEASURAND_ENERGY_ACTIVE_IMPORT.equals(sampledValue.getMeasurand().value());
        if (!sampledValue.isSetMeasurand() || measurandEnergyActiveImport) {
            // handling Mennekes style chargers where measurand is null
            float currentValue = Float.parseFloat(sampledValue.getValue());
            float startValue = Float.parseFloat(transactionStart.getStartValue());
            values.setTotalEnergyImport(getKwhValue(currentValue-startValue,
                    sampledValue.getUnit().value()));
        }
    }
}
