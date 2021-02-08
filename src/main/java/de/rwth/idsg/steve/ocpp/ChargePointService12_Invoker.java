/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2020 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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

import de.rwth.idsg.steve.ocpp.task.*;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2015
 */
public interface ChargePointService12_Invoker {

    void reset(ChargePointSelect cp, ResetTask task);

    void clearCache(ChargePointSelect cp, ClearCacheTask task);

    void getDiagnostics(ChargePointSelect cp, GetDiagnosticsTask task);

    void updateFirmware(ChargePointSelect cp, UpdateFirmwareTask task);

    void unlockConnector(ChargePointSelect cp, UnlockConnectorTask task);

    void changeAvailability(ChargePointSelect cp, ChangeAvailabilityTask task);

    void changeConfiguration(ChargePointSelect cp, ChangeConfigurationTask task);

    void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionTask task);

    void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionTask task);
}
