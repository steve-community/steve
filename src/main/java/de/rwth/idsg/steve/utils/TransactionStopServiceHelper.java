/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.utils;

import com.google.common.base.Strings;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.UnitOfMeasure;
import ocpp.cs._2015._10.ValueFormat;

@Slf4j
public class TransactionStopServiceHelper {

    public static Double calculateEnergyConsumptionInKWh(Transaction t) {
        if (t.getStopValue() == null) {
            return null; // this transaction did not finish yet
        }

        try {
            Integer meterValueStop = Integer.valueOf(t.getStopValue());
            Integer meterValueStart = Integer.valueOf(t.getStartValue());
            return (meterValueStop - meterValueStart) / 1000.0; // --> kWh
        } catch (Exception e) {
            log.error("Failed to calculate charged energy", e);
            return null;
        }
    }

    public static String floatingStringToIntString(String s) {
        // meter values can be floating, whereas start/end values are int
        return Integer.toString((int) Math.ceil(Double.parseDouble(s)));
    }

    public static String kWhStringToWhString(String s) {
        double kWhValue = Double.parseDouble(s);
        return Double.toString(kWhValue * 1000);
    }

    public static boolean isEnergyValue(TransactionDetails.MeterValues v) {
        // should not happen, but check it to be safe.
        // https://github.com/steve-community/steve/issues/249
        if (Strings.isNullOrEmpty(v.getValue())) {
            return false;
        }

        // is it a proper numeric/decimal value?
        try {
            Double.parseDouble(v.getValue());
        } catch (Exception e) {
            // swallow the exception. we got what we wanted.
            return false;
        }

        // edge case handling for format
        {
            ValueFormat format = Strings.isNullOrEmpty(v.getFormat())
                ? ValueFormat.RAW
                : ValueFormat.fromValue(v.getFormat());

            // if the format is "SignedData", we cannot make any sense of this entry. we don't know how to decode it.
            // https://github.com/steve-community/steve/issues/816
            if (ValueFormat.SIGNED_DATA == format) {
                return false;
            }
        }

        // edge case handling for measurand
        {
            Measurand measurand = Strings.isNullOrEmpty(v.getMeasurand())
                ? Measurand.ENERGY_ACTIVE_IMPORT_REGISTER
                : Measurand.fromValue(v.getMeasurand());

            if (Measurand.ENERGY_ACTIVE_IMPORT_REGISTER != measurand) {
                return false;
            }
        }

        // edge case handling for unit
        {
            UnitOfMeasure unit = Strings.isNullOrEmpty(v.getUnit())
                ? UnitOfMeasure.WH
                : UnitOfMeasure.fromValue(v.getUnit());

            if (!isWHOrKWH(unit)) {
                return false;
            }
        }

        // at this point, we have a value with
        // - RAW format
        // - Wh or kWh unit
        // - Energy.Active.Import.Register as the measurand
        return true;
    }

    private static boolean isWHOrKWH(UnitOfMeasure unit) {
        return UnitOfMeasure.WH == unit || UnitOfMeasure.K_WH == unit;
    }
}
