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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.EventRepository;
import de.rwth.idsg.steve.repository.dto.SecurityEvent;
import de.rwth.idsg.steve.repository.dto.StatusEvent;
import de.rwth.idsg.steve.web.dto.SecurityEventsQueryForm;
import de.rwth.idsg.steve.web.dto.StatusEventType;
import de.rwth.idsg.steve.web.dto.StatusEventsQueryForm;
import de.rwth.idsg.steve.web.dto.ocpp.GetLogParams;
import de.rwth.idsg.steve.web.dto.ocpp.SignedUpdateFirmwareParams;
import jooq.steve.db.tables.records.ChargeBoxFirmwareUpdateJobRecord;
import jooq.steve.db.tables.records.ChargeBoxLogUploadJobRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static de.rwth.idsg.steve.utils.CustomDSL.getTimeCondition;
import static jooq.steve.db.Tables.CHARGE_BOX_FIRMWARE_UPDATE_EVENT;
import static jooq.steve.db.Tables.CHARGE_BOX_FIRMWARE_UPDATE_JOB;
import static jooq.steve.db.Tables.CHARGE_BOX_LOG_UPLOAD_EVENT;
import static jooq.steve.db.Tables.CHARGE_BOX_LOG_UPLOAD_JOB;
import static jooq.steve.db.Tables.CHARGE_BOX_STATUS_EVENT;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.ChargeBoxSecurityEvent.CHARGE_BOX_SECURITY_EVENT;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final DSLContext ctx;

    // -------------------------------------------------------------------------
    // Security stuff
    // -------------------------------------------------------------------------

    @Override
    public void insertSecurityEvent(String chargeBoxId, String eventType, DateTime timestamp, String techInfo) {
        var chargeBoxPk = getChargeBoxPkQuery(chargeBoxId);

        ctx.insertInto(CHARGE_BOX_SECURITY_EVENT)
           .set(CHARGE_BOX_SECURITY_EVENT.CHARGE_BOX_PK, chargeBoxPk)
           .set(CHARGE_BOX_SECURITY_EVENT.TYPE, eventType)
           .set(CHARGE_BOX_SECURITY_EVENT.TIMESTAMP, timestamp)
           .set(CHARGE_BOX_SECURITY_EVENT.TECH_INFO, techInfo)
           .set(CHARGE_BOX_SECURITY_EVENT.EVENT_TIMESTAMP, DateTime.now())
           .execute();

        log.info("Security event '{}' recorded for chargeBox '{}'", eventType, chargeBoxId);
    }

    @Override
    public List<SecurityEvent> getSecurityEvents(SecurityEventsQueryForm form) {
        List<Condition> conditions = new ArrayList<>();

        if (form.isChargeBoxIdSet()) {
            conditions.add(CHARGE_BOX.CHARGE_BOX_ID.eq(form.getChargeBoxId()));
        }

        var timeCondition = getTimeCondition(CHARGE_BOX_SECURITY_EVENT.TIMESTAMP, form);
        if (timeCondition != null) {
            conditions.add(timeCondition);
        }

        return ctx.select(
                CHARGE_BOX.CHARGE_BOX_ID,
                CHARGE_BOX_SECURITY_EVENT.CHARGE_BOX_PK,
                CHARGE_BOX_SECURITY_EVENT.TYPE,
                CHARGE_BOX_SECURITY_EVENT.TIMESTAMP,
                CHARGE_BOX_SECURITY_EVENT.TECH_INFO)
            .from(CHARGE_BOX_SECURITY_EVENT)
            .join(CHARGE_BOX).on(CHARGE_BOX_SECURITY_EVENT.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
            .where(conditions)
            .orderBy(CHARGE_BOX_SECURITY_EVENT.TIMESTAMP.desc())
            .fetch(record -> SecurityEvent.builder()
                .chargeBoxId(record.value1())
                .chargeBoxPk(record.value2())
                .type(record.value3())
                .timestamp(record.value4())
                .techInfo(record.value5())
                .build()
            );
    }

    // -------------------------------------------------------------------------
    // Firmware Upload
    // -------------------------------------------------------------------------

    @Override
    public int insertFirmwareUpdateJob(SignedUpdateFirmwareParams params) {
        return ctx.insertInto(CHARGE_BOX_FIRMWARE_UPDATE_JOB)
            .set(CHARGE_BOX_FIRMWARE_UPDATE_JOB.CREATED_AT, DateTime.now())
            .set(CHARGE_BOX_FIRMWARE_UPDATE_JOB.FIRMWARE_LOCATION, params.getLocation())
            .set(CHARGE_BOX_FIRMWARE_UPDATE_JOB.RETRIEVE_DATETIME, params.getRetrieveDateTime())
            .set(CHARGE_BOX_FIRMWARE_UPDATE_JOB.INSTALL_DATETIME, params.getInstallDateTime())
            .set(CHARGE_BOX_FIRMWARE_UPDATE_JOB.SIGNING_CERTIFICATE, params.getSigningCertificate())
            .set(CHARGE_BOX_FIRMWARE_UPDATE_JOB.SIGNATURE, params.getSignature())
            .returning(CHARGE_BOX_FIRMWARE_UPDATE_JOB.JOB_ID)
            .fetchOne()
            .getJobId();
    }

    @Override
    public ChargeBoxFirmwareUpdateJobRecord getFirmwareUpdateDetails(int jobId) {
        var rec = ctx.selectFrom(CHARGE_BOX_FIRMWARE_UPDATE_JOB)
            .where(CHARGE_BOX_FIRMWARE_UPDATE_JOB.JOB_ID.eq(jobId))
            .fetchOne();

        if (rec == null) {
            throw new SteveException.NotFound("Firmware update job not found");
        }

        return rec;
    }

    @Override
    public void insertFirmwareUpdateStatus(String chargeBoxId, Integer requestId, String status, DateTime timestamp) {
        var chargeBoxPk = getChargeBoxPkQuery(chargeBoxId);

        ctx.insertInto(CHARGE_BOX_FIRMWARE_UPDATE_EVENT)
            .set(CHARGE_BOX_FIRMWARE_UPDATE_EVENT.CHARGE_BOX_PK, chargeBoxPk)
            .set(CHARGE_BOX_FIRMWARE_UPDATE_EVENT.JOB_ID, requestId)
            .set(CHARGE_BOX_FIRMWARE_UPDATE_EVENT.EVENT_STATUS, status)
            .set(CHARGE_BOX_FIRMWARE_UPDATE_EVENT.EVENT_TIMESTAMP, timestamp)
            .execute();
    }

    // -------------------------------------------------------------------------
    // Log Upload
    // -------------------------------------------------------------------------

    @Override
    public int insertLogUploadJob(GetLogParams params) {
        return ctx.insertInto(CHARGE_BOX_LOG_UPLOAD_JOB)
            .set(CHARGE_BOX_LOG_UPLOAD_JOB.CREATED_AT, DateTime.now())
            .set(CHARGE_BOX_LOG_UPLOAD_JOB.LOG_TYPE, params.getLogType().value())
            .set(CHARGE_BOX_LOG_UPLOAD_JOB.REMOTE_LOCATION, params.getLocation())
            .set(CHARGE_BOX_LOG_UPLOAD_JOB.OLDEST_TIMESTAMP, params.getStart())
            .set(CHARGE_BOX_LOG_UPLOAD_JOB.LATEST_TIMESTAMP, params.getStop())
            .returning(CHARGE_BOX_LOG_UPLOAD_JOB.JOB_ID)
            .fetchOne()
            .getJobId();
    }

    @Override
    public ChargeBoxLogUploadJobRecord getLogUploadDetails(int jobId) {
        var rec = ctx.selectFrom(CHARGE_BOX_LOG_UPLOAD_JOB)
            .where(CHARGE_BOX_LOG_UPLOAD_JOB.JOB_ID.eq(jobId))
            .fetchOne();

        if (rec == null) {
            throw new SteveException.NotFound("Log upload job not found");
        }

        return rec;
    }

    @Override
    public void insertLogUploadStatus(String chargeBoxId, Integer requestId, String status, DateTime timestamp) {
        var chargeBoxPk = getChargeBoxPkQuery(chargeBoxId);

        ctx.insertInto(CHARGE_BOX_LOG_UPLOAD_EVENT)
            .set(CHARGE_BOX_LOG_UPLOAD_EVENT.CHARGE_BOX_PK, chargeBoxPk)
            .set(CHARGE_BOX_LOG_UPLOAD_EVENT.JOB_ID, requestId)
            .set(CHARGE_BOX_LOG_UPLOAD_EVENT.EVENT_STATUS, status)
            .set(CHARGE_BOX_LOG_UPLOAD_EVENT.EVENT_TIMESTAMP, timestamp)
            .execute();
    }

    // -------------------------------------------------------------------------
    // Firmware Upload + Log Upload
    // -------------------------------------------------------------------------

    @Override
    public List<StatusEvent> getStatusEvents(StatusEventsQueryForm form) {
        List<Condition> conditions = new ArrayList<>();

        if (form.getEventType() != null) {
            conditions.add(CHARGE_BOX_STATUS_EVENT.EVENT_TYPE.eq(form.getEventType().name()));
        }

        if (form.isJobIdSet()) {
            conditions.add(CHARGE_BOX_STATUS_EVENT.JOB_ID.eq(form.getJobId()));
        }

        if (form.isChargeBoxIdSet()) {
            conditions.add(CHARGE_BOX.CHARGE_BOX_ID.eq(form.getChargeBoxId()));
        }

        var timeCondition = getTimeCondition(CHARGE_BOX_STATUS_EVENT.EVENT_TIMESTAMP, form);
        if (timeCondition != null) {
            conditions.add(timeCondition);
        }

        return ctx.select(
                CHARGE_BOX_STATUS_EVENT.JOB_ID,
                CHARGE_BOX.CHARGE_BOX_ID,
                CHARGE_BOX_STATUS_EVENT.CHARGE_BOX_PK,
                CHARGE_BOX_STATUS_EVENT.EVENT_STATUS,
                CHARGE_BOX_STATUS_EVENT.EVENT_TIMESTAMP,
                CHARGE_BOX_STATUS_EVENT.EVENT_TYPE)
            .from(CHARGE_BOX_STATUS_EVENT)
            .join(CHARGE_BOX).on(CHARGE_BOX_STATUS_EVENT.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
            .where(conditions)
            .orderBy(CHARGE_BOX_STATUS_EVENT.EVENT_TIMESTAMP.desc())
            .fetch(record -> StatusEvent.builder()
                .jobId(record.value1())
                .chargeBoxId(record.value2())
                .chargeBoxPk(record.value3())
                .status(record.value4())
                .timestamp(record.value5())
                .eventType(StatusEventType.valueOf(record.value6()))
                .build()
            );
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private SelectConditionStep<Record1<Integer>> getChargeBoxPkQuery(String chargeBoxId) {
        return ctx.select(CHARGE_BOX.CHARGE_BOX_PK)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(chargeBoxId));
    }
}
