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
package de.rwth.idsg.steve.web;

import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;

import java.beans.PropertyEditorSupport;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.12.2014
 */
public class ChargePointSelectEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        if (!text.isEmpty()) {
            String[] chargePointItem = text.split(";");

            // chargePointItem[0] : ocpp transport type
            // chargePointItem[1] : chargebox id
            // chargePointItem[2] : endpoint (IP) address
            ChargePointSelect cps = new ChargePointSelect(
                    OcppTransport.fromName(chargePointItem[0]),
                    chargePointItem[1],
                    chargePointItem[2]
            );

            setValue(cps);
        }
    }
}
