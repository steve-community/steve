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
package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.web.dto.ocpp.ChargePointSelection;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 30.10.2018
 */
public abstract class Ocpp16AndAboveTask<S extends ChargePointSelection, RESPONSE> extends Ocpp15AndAboveTask<S, RESPONSE> {

    public Ocpp16AndAboveTask(OcppVersion ocppVersion, S params) {
        super(ocppVersion, params);
    }

    @Deprecated
    @Override
    public <T extends RequestType> T getOcpp15Request() {
        throw new RuntimeException("Not supported");
    }

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp15Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
    }
}
