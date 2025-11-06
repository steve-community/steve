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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.SecurityRepository;
import de.rwth.idsg.steve.repository.dto.Certificate;
import de.rwth.idsg.steve.repository.dto.FirmwareUpdate;
import de.rwth.idsg.steve.repository.dto.LogFile;
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
import org.jooq.Field;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.rwth.idsg.steve.utils.CustomDSL.date;
import static jooq.steve.db.Tables.CHARGE_BOX_FIRMWARE_UPDATE_JOB;
import static jooq.steve.db.Tables.CHARGE_BOX_FIRMWARE_UPDATE_STATUS;
import static jooq.steve.db.Tables.CHARGE_BOX_LOG_UPLOAD_JOB;
import static jooq.steve.db.Tables.CHARGE_BOX_LOG_UPLOAD_STATUS;
import static jooq.steve.db.Tables.CHARGE_BOX_STATUS_EVENT;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.ChargeBoxSecurityEvent.CHARGE_BOX_SECURITY_EVENT;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SecurityRepositoryImpl implements SecurityRepository {

    private final DSLContext ctx;

    @Override
    public void insertSecurityEvent(String chargeBoxId, String eventType, DateTime timestamp, String techInfo) {
        var chargeBoxPk = getChargeBoxPk(chargeBoxId);
        if (chargeBoxPk == null) {
            log.error("Cannot insert security event for unknown chargeBoxId: {}", chargeBoxId);
            return;
        }

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
    public void insertLogUploadStatus(String chargeBoxId, Integer requestId, String status, DateTime timestamp) {
        var chargeBoxPk = getChargeBoxPk(chargeBoxId);
        if (chargeBoxPk == null) {
            log.error("Cannot insert log upload status for unknown chargeBoxId: {}", chargeBoxId);
            return;
        }

        ctx.insertInto(CHARGE_BOX_LOG_UPLOAD_STATUS)
            .set(CHARGE_BOX_LOG_UPLOAD_STATUS.CHARGE_BOX_PK, chargeBoxPk)
            .set(CHARGE_BOX_LOG_UPLOAD_STATUS.JOB_ID, requestId)
            .set(CHARGE_BOX_LOG_UPLOAD_STATUS.EVENT_STATUS, status)
            .set(CHARGE_BOX_LOG_UPLOAD_STATUS.EVENT_TIMESTAMP, timestamp)
            .execute();
    }

    @Override
    public void insertFirmwareUpdateStatus(String chargeBoxId, Integer requestId, String status, DateTime timestamp) {
        var chargeBoxPk = getChargeBoxPk(chargeBoxId);
        if (chargeBoxPk == null) {
            log.error("Cannot insert log upload status for unknown chargeBoxId: {}", chargeBoxId);
            return;
        }

        ctx.insertInto(CHARGE_BOX_FIRMWARE_UPDATE_STATUS)
            .set(CHARGE_BOX_FIRMWARE_UPDATE_STATUS.CHARGE_BOX_PK, chargeBoxPk)
            .set(CHARGE_BOX_FIRMWARE_UPDATE_STATUS.JOB_ID, requestId)
            .set(CHARGE_BOX_FIRMWARE_UPDATE_STATUS.EVENT_STATUS, status)
            .set(CHARGE_BOX_FIRMWARE_UPDATE_STATUS.EVENT_TIMESTAMP, timestamp)
            .execute();
    }

    @Override
    public int insertNewLogUploadJob(GetLogParams params) {
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
    public int insertNewFirmwareUpdateJob(SignedUpdateFirmwareParams params) {
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
    public int insertCertificate(String chargeBoxId, String certificateType, String certificateData,
                                 String serialNumber, String issuerName, String subjectName,
                                 DateTime validFrom, DateTime validTo, String signatureAlgorithm,
                                 Integer keySize) {
        return 0;
//        var chargeBoxPk = getChargeBoxPk(chargeBoxId);
//        if (chargeBoxPk == null) {
//            log.error("Cannot insert certificate for unknown chargeBoxId: {}", chargeBoxId);
//            return -1;
//        }
//
//        var certificateId = ctx.insertInto(CERTIFICATE)
//                               .set(CERTIFICATE.CHARGE_BOX_PK, chargeBoxPk)
//                               .set(CERTIFICATE.CERTIFICATE_TYPE, certificateType)
//                               .set(CERTIFICATE.CERTIFICATE_DATA, certificateData)
//                               .set(CERTIFICATE.SERIAL_NUMBER, serialNumber)
//                               .set(CERTIFICATE.ISSUER_NAME, issuerName)
//                               .set(CERTIFICATE.SUBJECT_NAME, subjectName)
//                               .set(CERTIFICATE.VALID_FROM, validFrom)
//                               .set(CERTIFICATE.VALID_TO, validTo)
//                               .set(CERTIFICATE.SIGNATURE_ALGORITHM, signatureAlgorithm)
//                               .set(CERTIFICATE.KEY_SIZE, keySize)
//                               .set(CERTIFICATE.STATUS, "Installed")
//                               .returningResult(CERTIFICATE.CERTIFICATE_ID)
//                               .fetchOne()
//                               .value1();
//
//        log.info("Certificate type '{}' installed for chargeBox '{}' with ID {}", certificateType, chargeBoxId, certificateId);
//        return certificateId;
    }

    @Override
    public void updateCertificateStatus(int certificateId, String status) {
//        ctx.update(CERTIFICATE)
//           .set(CERTIFICATE.STATUS, status)
//           .where(CERTIFICATE.CERTIFICATE_ID.eq(certificateId))
//           .execute();
    }

    @Override
    public List<Certificate> getInstalledCertificates(String chargeBoxId, String certificateType) {
        return Collections.emptyList();

//        var chargeBoxPk = getChargeBoxPk(chargeBoxId);
//        if (chargeBoxPk == null) {
//            return List.of();
//        }
//
//        var query = ctx.select(
//                       CERTIFICATE.CERTIFICATE_ID,
//                       CHARGE_BOX.CHARGE_BOX_ID,
//                       CERTIFICATE.CERTIFICATE_TYPE,
//                       CERTIFICATE.CERTIFICATE_DATA,
//                       CERTIFICATE.SERIAL_NUMBER,
//                       CERTIFICATE.ISSUER_NAME,
//                       CERTIFICATE.SUBJECT_NAME,
//                       CERTIFICATE.VALID_FROM,
//                       CERTIFICATE.VALID_TO,
//                       CERTIFICATE.SIGNATURE_ALGORITHM,
//                       CERTIFICATE.KEY_SIZE,
//                       CERTIFICATE.INSTALLED_DATE,
//                       CERTIFICATE.STATUS
//                   )
//                   .from(CERTIFICATE)
//                   .join(CHARGE_BOX).on(CERTIFICATE.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
//                   .where(CERTIFICATE.CHARGE_BOX_PK.eq(chargeBoxPk))
//                   .and(CERTIFICATE.STATUS.eq("Installed"));
//
//        if (certificateType != null) {
//            query = query.and(CERTIFICATE.CERTIFICATE_TYPE.eq(certificateType));
//        }
//
//        return query.fetch(record -> Certificate.builder()
//                                                .certificateId(record.value1())
//                                                .chargeBoxId(record.value2())
//                                                .certificateType(record.value3())
//                                                .certificateData(record.value4())
//                                                .serialNumber(record.value5())
//                                                .issuerName(record.value6())
//                                                .subjectName(record.value7())
//                                                .validFrom(record.value8())
//                                                .validTo(record.value9())
//                                                .signatureAlgorithm(record.value10())
//                                                .keySize(record.value11())
//                                                .installedDate(record.value12())
//                                                .status(record.value13())
//                                                .build());
    }

    @Override
    public void deleteCertificate(int certificateId) {
//        ctx.update(CERTIFICATE)
//           .set(CERTIFICATE.STATUS, "Deleted")
//           .where(CERTIFICATE.CERTIFICATE_ID.eq(certificateId))
//           .execute();
//
//        log.info("Certificate {} marked as deleted", certificateId);
    }

    @Override
    public Certificate getCertificateBySerialNumber(String serialNumber) {
        return null;

//        return ctx.select(
//                      CERTIFICATE.CERTIFICATE_ID,
//                      CHARGE_BOX.CHARGE_BOX_ID,
//                      CERTIFICATE.CERTIFICATE_TYPE,
//                      CERTIFICATE.CERTIFICATE_DATA,
//                      CERTIFICATE.SERIAL_NUMBER,
//                      CERTIFICATE.ISSUER_NAME,
//                      CERTIFICATE.SUBJECT_NAME,
//                      CERTIFICATE.VALID_FROM,
//                      CERTIFICATE.VALID_TO,
//                      CERTIFICATE.SIGNATURE_ALGORITHM,
//                      CERTIFICATE.KEY_SIZE,
//                      CERTIFICATE.INSTALLED_DATE,
//                      CERTIFICATE.STATUS
//                  )
//                  .from(CERTIFICATE)
//                  .join(CHARGE_BOX).on(CERTIFICATE.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
//                  .where(CERTIFICATE.SERIAL_NUMBER.eq(serialNumber))
//                  .fetchOne(record -> Certificate.builder()
//                                                 .certificateId(record.value1())
//                                                 .chargeBoxId(record.value2())
//                                                 .certificateType(record.value3())
//                                                 .certificateData(record.value4())
//                                                 .serialNumber(record.value5())
//                                                 .issuerName(record.value6())
//                                                 .subjectName(record.value7())
//                                                 .validFrom(record.value8())
//                                                 .validTo(record.value9())
//                                                 .signatureAlgorithm(record.value10())
//                                                 .keySize(record.value11())
//                                                 .installedDate(record.value12())
//                                                 .status(record.value13())
//                                                 .build());
    }

    @Override
    public int insertLogFile(String chargeBoxId, String logType, Integer requestId, String filePath) {
        return 0;

//        var chargeBoxPk = getChargeBoxPk(chargeBoxId);
//        if (chargeBoxPk == null) {
//            log.error("Cannot insert log file for unknown chargeBoxId: {}", chargeBoxId);
//            return -1;
//        }
//
//        var logFileId = ctx.insertInto(LOG_FILE)
//                           .set(LOG_FILE.CHARGE_BOX_PK, chargeBoxPk)
//                           .set(LOG_FILE.LOG_TYPE, logType)
//                           .set(LOG_FILE.REQUEST_ID, requestId)
//                           .set(LOG_FILE.FILE_PATH, filePath)
//                           .set(LOG_FILE.UPLOAD_STATUS, "Pending")
//                           .returningResult(LOG_FILE.LOG_ID)
//                           .fetchOne()
//                           .value1();
//
//        log.info("Log file request created for chargeBox '{}' with ID {}", chargeBoxId, logFileId);
//        return logFileId;
    }

    @Override
    public void updateLogFileStatus(int logFileId, String uploadStatus, Long bytesUploaded) {
//        ctx.update(LOG_FILE)
//           .set(LOG_FILE.UPLOAD_STATUS, uploadStatus)
//           .set(LOG_FILE.BYTES_UPLOADED, bytesUploaded)
//           .where(LOG_FILE.LOG_ID.eq(logFileId))
//           .execute();
    }

    @Override
    public LogFile getLogFile(int logFileId) {
        return null;

//        return ctx.select(
//                      LOG_FILE.LOG_ID,
//                      CHARGE_BOX.CHARGE_BOX_ID,
//                      LOG_FILE.LOG_TYPE,
//                      LOG_FILE.REQUEST_ID,
//                      LOG_FILE.FILE_PATH,
//                      LOG_FILE.REQUEST_TIMESTAMP,
//                      LOG_FILE.UPLOAD_STATUS,
//                      LOG_FILE.BYTES_UPLOADED
//                  )
//                  .from(LOG_FILE)
//                  .join(CHARGE_BOX).on(LOG_FILE.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
//                  .where(LOG_FILE.LOG_ID.eq(logFileId))
//                  .fetchOne(record -> LogFile.builder()
//                                             .logFileId(record.value1())
//                                             .chargeBoxId(record.value2())
//                                             .logType(record.value3())
//                                             .requestId(record.value4())
//                                             .filePath(record.value5())
//                                             .requestTimestamp(record.value6())
//                                             .uploadStatus(record.value7())
//                                             .bytesUploaded(record.value8())
//                                             .build());
    }

    @Override
    public int insertFirmwareUpdate(String chargeBoxId, String firmwareLocation, String firmwareSignature,
                                    String signingCertificate, DateTime retrieveDate, DateTime installDate) {
        return 0;

//        var chargeBoxPk = getChargeBoxPk(chargeBoxId);
//        if (chargeBoxPk == null) {
//            log.error("Cannot insert firmware update for unknown chargeBoxId: {}", chargeBoxId);
//            return -1;
//        }
//
//        var firmwareUpdateId = ctx.insertInto(FIRMWARE_UPDATE)
//                                  .set(FIRMWARE_UPDATE.CHARGE_BOX_PK, chargeBoxPk)
//                                  .set(FIRMWARE_UPDATE.FIRMWARE_LOCATION, firmwareLocation)
//                                  .set(FIRMWARE_UPDATE.FIRMWARE_SIGNATURE, firmwareSignature)
//                                  .set(FIRMWARE_UPDATE.SIGNING_CERTIFICATE, signingCertificate)
//                                  .set(FIRMWARE_UPDATE.RETRIEVE_DATE, retrieveDate)
//                                  .set(FIRMWARE_UPDATE.INSTALL_DATE, installDate)
//                                  .set(FIRMWARE_UPDATE.STATUS, "Pending")
//                                  .returningResult(FIRMWARE_UPDATE.UPDATE_ID)
//                                  .fetchOne()
//                                  .value1();
//
//        log.info("Firmware update request created for chargeBox '{}' with ID {}", chargeBoxId, firmwareUpdateId);
//        return firmwareUpdateId;
    }

    @Override
    public void updateFirmwareUpdateStatus(int firmwareUpdateId, String status) {
//        ctx.update(FIRMWARE_UPDATE)
//           .set(FIRMWARE_UPDATE.STATUS, status)
//           .where(FIRMWARE_UPDATE.UPDATE_ID.eq(firmwareUpdateId))
//           .execute();
    }

    @Override
    public FirmwareUpdate getCurrentFirmwareUpdate(String chargeBoxId) {
        return null;

//        var chargeBoxPk = getChargeBoxPk(chargeBoxId);
//        if (chargeBoxPk == null) {
//            return null;
//        }
//
//        return ctx.select(
//                      FIRMWARE_UPDATE.UPDATE_ID,
//                      CHARGE_BOX.CHARGE_BOX_ID,
//                      FIRMWARE_UPDATE.FIRMWARE_LOCATION,
//                      FIRMWARE_UPDATE.FIRMWARE_SIGNATURE,
//                      FIRMWARE_UPDATE.SIGNING_CERTIFICATE,
//                      FIRMWARE_UPDATE.REQUEST_TIMESTAMP,
//                      FIRMWARE_UPDATE.RETRIEVE_DATE,
//                      FIRMWARE_UPDATE.INSTALL_DATE,
//                      FIRMWARE_UPDATE.STATUS
//                  )
//                  .from(FIRMWARE_UPDATE)
//                  .join(CHARGE_BOX).on(FIRMWARE_UPDATE.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
//                  .where(FIRMWARE_UPDATE.CHARGE_BOX_PK.eq(chargeBoxPk))
//                  .orderBy(FIRMWARE_UPDATE.REQUEST_TIMESTAMP.desc())
//                  .limit(1)
//                  .fetchOne(record -> FirmwareUpdate.builder()
//                                                    .firmwareUpdateId(record.value1())
//                                                    .chargeBoxId(record.value2())
//                                                    .firmwareLocation(record.value3())
//                                                    .firmwareSignature(record.value4())
//                                                    .signingCertificate(record.value5())
//                                                    .requestTimestamp(record.value6())
//                                                    .retrieveDate(record.value7())
//                                                    .installDate(record.value8())
//                                                    .status(record.value9())
//                                                    .build());
    }

    private Integer getChargeBoxPk(String chargeBoxId) {
        var record = ctx.select(CHARGE_BOX.CHARGE_BOX_PK)
                                     .from(CHARGE_BOX)
                                     .where(CHARGE_BOX.CHARGE_BOX_ID.eq(chargeBoxId))
                                     .fetchOne();
        return record != null ? record.value1() : null;
    }

    @Nullable
    private static Condition getTimeCondition(Field<DateTime> timestampField, SecurityEventsQueryForm form) {
        switch (form.getPeriodType()) {
            case TODAY:
                return date(timestampField).eq(date(DateTime.now()));

            case LAST_10:
            case LAST_30:
            case LAST_90:
                DateTime now = DateTime.now();
                return date(timestampField).between(
                    date(now.minusDays(form.getPeriodType().getInterval())),
                    date(now)
                );

            case ALL:
                return null;

            case FROM_TO:
                DateTime from = form.getFrom();
                DateTime to = form.getTo();
                return timestampField.between(from, to);
            default:
                throw new SteveException("Unknown enum type");
        }
    }
}
