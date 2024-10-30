package net.parkl.ocpp.service.cs.status;

import lombok.RequiredArgsConstructor;
import net.parkl.ocpp.entities.AbstractTransactionEnergyImport;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.module.esp.model.ESPMeterValues;
import net.parkl.ocpp.service.OcppConstants;
import net.parkl.ocpp.service.PowerValue;
import net.parkl.ocpp.service.cs.EnergyImportLoader;
import ocpp.cs._2015._10.SampledValue;
import org.springframework.stereotype.Component;

import static net.parkl.ocpp.service.OcppConsumptionHelper.getKwhValue;

@Component
@RequiredArgsConstructor
public class TotalEnergyParser implements MeterValueParser {
    private final EnergyImportLoader energyImportLoader;

    public void parseMeterValue(ESPMeterValues values, TransactionStart transactionStart, SampledValue sampledValue) {
        boolean measurandEnergyActiveImport = sampledValue.isSetMeasurand() &&
                OcppConstants.MEASURAND_ENERGY_ACTIVE_IMPORT.equals(sampledValue.getMeasurand().value());
        boolean hasPhase = sampledValue.isSetPhase();
        if ((!sampledValue.isSetMeasurand() || measurandEnergyActiveImport) && !hasPhase) {
            AbstractTransactionEnergyImport energyImport = energyImportLoader.loadEnergyImport(
                    transactionStart.getTransactionPk());
            if (energyImport != null) {
                float currentValue = Float.parseFloat(sampledValue.getValue());
                float startValue = energyImport.getStartValue();
                String unit = OcppConstants.UNIT_WH;
                if (sampledValue.getUnit()!=null && sampledValue.getUnit().value()!=null) {
                    unit = sampledValue.getUnit().value();
                }
                values.setTotalEnergyImport(getKwhValue(currentValue - startValue,
                        unit));
            }
        }
    }


}
