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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.repository.EventRepository;
import de.rwth.idsg.steve.web.dto.SecurityEventsQueryForm;
import de.rwth.idsg.steve.web.dto.StatusEventsQueryForm;
import de.rwth.idsg.steve.web.dto.ocpp.GetLogParams;
import de.rwth.idsg.steve.web.dto.ocpp.SignedUpdateFirmwareParams;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static jooq.steve.db.tables.ChargeBoxFirmwareUpdateJob.CHARGE_BOX_FIRMWARE_UPDATE_JOB;
import static jooq.steve.db.tables.ChargeBoxFirmwareUpdateEvent.CHARGE_BOX_FIRMWARE_UPDATE_EVENT;
import static jooq.steve.db.tables.ChargeBoxLogUploadJob.CHARGE_BOX_LOG_UPLOAD_JOB;
import static jooq.steve.db.tables.ChargeBoxLogUploadEvent.CHARGE_BOX_LOG_UPLOAD_EVENT;
import static jooq.steve.db.tables.ChargeBoxSecurityEvent.CHARGE_BOX_SECURITY_EVENT;

/**
 * Created with assistance from GPT-5.3-Codex
 */
public class EventRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private EventRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void insertSecurityEvent() {
        assertNoDatabaseException(() -> repository.insertSecurityEvent(KNOWN_CHARGE_BOX_ID, "event", DateTime.now(), "info"));

        Integer count = dslContext.selectCount()
            .from(CHARGE_BOX_SECURITY_EVENT)
            .fetchOne(0, int.class);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void getSecurityEvents() {
        repository.insertSecurityEvent(KNOWN_CHARGE_BOX_ID, "event", DateTime.now(), "info");
        var rows = assertNoDatabaseException(() -> repository.getSecurityEvents(new SecurityEventsQueryForm()));
        Assertions.assertFalse(rows.isEmpty());
    }

    @Test
    public void insertFirmwareUpdateJob() {
        Integer jobId = assertNoDatabaseException(() -> repository.insertFirmwareUpdateJob(signedUpdateFirmwareParams()));
        Assertions.assertNotNull(jobId);
    }

    @Test
    public void getFirmwareUpdateDetails() {
        Integer jobId = repository.insertFirmwareUpdateJob(signedUpdateFirmwareParams());
        var row = assertNoDatabaseException(() -> repository.getFirmwareUpdateDetails(jobId));
        Assertions.assertNotNull(row);
        Assertions.assertEquals(jobId, row.getJobId());
    }

    @Test
    public void insertFirmwareUpdateStatus() {
        Integer jobId = dslContext.insertInto(CHARGE_BOX_FIRMWARE_UPDATE_JOB)
            .set(CHARGE_BOX_FIRMWARE_UPDATE_JOB.FIRMWARE_LOCATION, "https://example.com/fw.bin")
            .returning(CHARGE_BOX_FIRMWARE_UPDATE_JOB.JOB_ID)
            .fetchOne()
            .getJobId();

        assertNoDatabaseException(() -> repository.insertFirmwareUpdateStatus(KNOWN_CHARGE_BOX_ID, jobId, "Accepted", DateTime.now()));

        Integer count = dslContext.selectCount()
            .from(CHARGE_BOX_FIRMWARE_UPDATE_EVENT)
            .where(CHARGE_BOX_FIRMWARE_UPDATE_EVENT.JOB_ID.eq(jobId))
            .fetchOne(0, int.class);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void insertLogUploadJob() {
        Integer jobId = assertNoDatabaseException(() -> repository.insertLogUploadJob(getLogParams()));
        Assertions.assertNotNull(jobId);
    }

    @Test
    public void getLogUploadDetails() {
        Integer jobId = repository.insertLogUploadJob(getLogParams());
        var row = assertNoDatabaseException(() -> repository.getLogUploadDetails(jobId));
        Assertions.assertNotNull(row);
        Assertions.assertEquals(jobId, row.getJobId());
    }

    @Test
    public void insertLogUploadStatus() {
        Integer jobId = dslContext.insertInto(CHARGE_BOX_LOG_UPLOAD_JOB)
            .set(CHARGE_BOX_LOG_UPLOAD_JOB.LOG_TYPE, "DiagnosticsLog")
            .returning(CHARGE_BOX_LOG_UPLOAD_JOB.JOB_ID)
            .fetchOne()
            .getJobId();

        assertNoDatabaseException(() -> repository.insertLogUploadStatus(KNOWN_CHARGE_BOX_ID, jobId, "Accepted", DateTime.now()));

        Integer count = dslContext.selectCount()
            .from(CHARGE_BOX_LOG_UPLOAD_EVENT)
            .where(CHARGE_BOX_LOG_UPLOAD_EVENT.JOB_ID.eq(jobId))
            .fetchOne(0, int.class);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void getStatusEvents() {
        Integer jobId = repository.insertFirmwareUpdateJob(signedUpdateFirmwareParams());
        repository.insertFirmwareUpdateStatus(KNOWN_CHARGE_BOX_ID, jobId, "Accepted", DateTime.now());

        var rows = assertNoDatabaseException(() -> repository.getStatusEvents(new StatusEventsQueryForm()));
        Assertions.assertFalse(rows.isEmpty());
    }

    private static SignedUpdateFirmwareParams signedUpdateFirmwareParams() {
        var params = new SignedUpdateFirmwareParams();
        params.setChargeBoxIdList(List.of(KNOWN_CHARGE_BOX_ID));
        params.setLocation("https://example.com/fw.bin");
        params.setRetrieveDateTime(DateTime.now().plusHours(1));
        params.setInstallDateTime(DateTime.now().plusHours(2));
        params.setSigningCertificate("CERT");
        params.setSignature("SIG");
        return params;
    }

    private static GetLogParams getLogParams() {
        var params = new GetLogParams();
        params.setChargeBoxIdList(List.of(KNOWN_CHARGE_BOX_ID));
        params.setLocation("https://example.com/log");
        params.setLogType(ocpp._2022._02.security.GetLog.LogEnumType.values()[0]);
        params.setStart(DateTime.now().minusHours(2));
        params.setStop(DateTime.now().minusHours(1));
        return params;
    }
}
