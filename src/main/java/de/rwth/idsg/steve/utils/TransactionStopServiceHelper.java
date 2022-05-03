package de.rwth.idsg.steve.utils;

import com.google.common.base.Strings;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.UnitOfMeasure;
import ocpp.cs._2015._10.ValueFormat;

public class TransactionStopServiceHelper {

    public static boolean isEnergyValue(TransactionDetails.MeterValues v) {
        // should not happen, but check it to be safe.
        // https://github.com/RWTH-i5-IDSG/steve/issues/249
        if (Strings.isNullOrEmpty(v.getValue())) {
            return false;
        }

        // from 1.6 docs: "To retain backward compatibility, the default values of all of the optional fields on a
        // sampledValue element are such that a value without any additional fields will be interpreted, as a register
        // reading of active import energy in Wh (Watt-hour) units."
        if (Strings.isNullOrEmpty(v.getReadingContext())
            && Strings.isNullOrEmpty(v.getFormat())
            && Strings.isNullOrEmpty(v.getMeasurand())
            && Strings.isNullOrEmpty(v.getLocation())
            && Strings.isNullOrEmpty(v.getUnit())
            && Strings.isNullOrEmpty(v.getPhase())) {
            return true;
        }

        // if the format is "SignedData", we cannot make any sense of this entry. we don't know how to decode it.
        // https://github.com/RWTH-i5-IDSG/steve/issues/816
        if (ValueFormat.SIGNED_DATA.value().equals(v.getFormat())) {
            return false;
        }

        if (!isWHOrKWH(v.getUnit())) {
            return false;
        }

        if (!Measurand.ENERGY_ACTIVE_IMPORT_REGISTER.value().equals(v.getMeasurand())) {
            return false;
        }

        // at this point, we have a value with
        // - RAW or null format
        // - Wh or kWh unit
        // - Energy.Active.Import.Register as the measurand
        return true;
    }

    private static boolean isWHOrKWH(String str) {
        return UnitOfMeasure.WH.value().equals(str) || UnitOfMeasure.K_WH.value().equals(str);
    }
}
