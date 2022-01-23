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

import de.rwth.idsg.steve.ocpp.task.CancelReservationTask;
import de.rwth.idsg.steve.ocpp.task.DataTransferTask;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.GetLocalListVersionTask;
import de.rwth.idsg.steve.ocpp.task.ReserveNowTask;
import de.rwth.idsg.steve.ocpp.task.SendLocalListTask;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 20.03.2015
 */
public interface ChargePointService15_Invoker extends ChargePointService12_Invoker {

    void dataTransfer(ChargePointSelect cp, DataTransferTask task);

    void getConfiguration(ChargePointSelect cp, GetConfigurationTask task);

    void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionTask task);

    void sendLocalList(ChargePointSelect cp, SendLocalListTask task);

    void reserveNow(ChargePointSelect cp, ReserveNowTask task);

    void cancelReservation(ChargePointSelect cp, CancelReservationTask task);
}
