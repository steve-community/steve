/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.SecurityEvent;
import de.rwth.idsg.steve.repository.dto.StatusEvent;
import de.rwth.idsg.steve.web.dto.SecurityEventsQueryForm;
import de.rwth.idsg.steve.web.dto.StatusEventsQueryForm;
import de.rwth.idsg.steve.web.dto.ocpp.GetLogParams;
import de.rwth.idsg.steve.web.dto.ocpp.SignedUpdateFirmwareParams;
import jooq.steve.db.tables.records.ChargeBoxFirmwareUpdateJobRecord;
import jooq.steve.db.tables.records.ChargeBoxLogUploadJobRecord;
import org.joda.time.DateTime;

import java.util.List;

public interface EventRepository {

    // -------------------------------------------------------------------------
    // Security
    // -------------------------------------------------------------------------

    void insertSecurityEvent(String chargeBoxId, String eventType, DateTime timestamp, String techInfo);
    List<SecurityEvent> getSecurityEvents(SecurityEventsQueryForm form);

    // -------------------------------------------------------------------------
    // Firmware Upload
    // -------------------------------------------------------------------------

    int insertFirmwareUpdateJob(SignedUpdateFirmwareParams params);
    ChargeBoxFirmwareUpdateJobRecord getFirmwareUpdateDetails(int jobId);
    void insertFirmwareUpdateStatus(String chargeBoxIdentity, Integer requestId, String value, DateTime timestamp);

    // -------------------------------------------------------------------------
    // Log Upload
    // -------------------------------------------------------------------------

    int insertLogUploadJob(GetLogParams params);
    ChargeBoxLogUploadJobRecord getLogUploadDetails(int jobId);
    void insertLogUploadStatus(String chargeBoxIdentity, Integer requestId, String status, DateTime timestamp);

    // -------------------------------------------------------------------------
    // Firmware Upload + Log Upload
    // -------------------------------------------------------------------------

    List<StatusEvent> getStatusEvents(StatusEventsQueryForm params);
}
