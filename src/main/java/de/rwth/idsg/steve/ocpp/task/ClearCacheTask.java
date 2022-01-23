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
package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class ClearCacheTask extends CommunicationTask<MultipleChargePointSelect, String> {

    public ClearCacheTask(OcppVersion ocppVersion, MultipleChargePointSelect params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ocpp.cp._2010._08.ClearCacheRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.ClearCacheRequest();
    }

    @Override
    public ocpp.cp._2012._06.ClearCacheRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.ClearCacheRequest();
    }

    @Override
    public ocpp.cp._2015._10.ClearCacheRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.ClearCacheRequest();
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.ClearCacheResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.ClearCacheResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.ClearCacheResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                ClearCacheTask.this.success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                ClearCacheTask.this.failed(chargeBoxId, e);
            }
        };
    }
}
