/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.UnitOfMeasure;
import ocpp.cs._2015._10.ValueFormat;

public class TransactionStopServiceHelper {

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
        // https://github.com/RWTH-i5-IDSG/steve/issues/249
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
