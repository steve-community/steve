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

import de.rwth.idsg.steve.repository.SecurityRepository;
import de.rwth.idsg.steve.repository.dto.Certificate;
import de.rwth.idsg.steve.repository.dto.FirmwareUpdate;
import de.rwth.idsg.steve.repository.dto.LogFile;
import de.rwth.idsg.steve.repository.dto.SecurityEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static jooq.steve.db.Tables.CHARGE_BOX_FIRMWARE_UPDATE_STATUS;
import static jooq.steve.db.Tables.CHARGE_BOX_LOG_UPLOAD_STATUS;
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
    public List<SecurityEvent> getSecurityEvents(String chargeBoxId, Integer limit) {
        return Collections.emptyList();

//        var chargeBoxPk = getChargeBoxPk(chargeBoxId);
//        if (chargeBoxPk == null) {
//            return List.of();
//        }
//
//        var baseQuery = ctx.select(
//                       SECURITY_EVENT.EVENT_ID,
//                       CHARGE_BOX.CHARGE_BOX_ID,
//                       SECURITY_EVENT.EVENT_TYPE,
//                       SECURITY_EVENT.EVENT_TIMESTAMP,
//                       SECURITY_EVENT.TECH_INFO,
//                       SECURITY_EVENT.SEVERITY
//                   )
//                   .from(SECURITY_EVENT)
//                   .join(CHARGE_BOX).on(SECURITY_EVENT.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
//                   .where(SECURITY_EVENT.CHARGE_BOX_PK.eq(chargeBoxPk))
//                   .orderBy(SECURITY_EVENT.EVENT_TIMESTAMP.desc());
//
//        var query = (limit != null && limit > 0) ? baseQuery.limit(limit) : baseQuery;
//
//        return query.fetch(record -> SecurityEvent.builder()
//                                                   .securityEventId(record.value1())
//                                                   .chargeBoxId(record.value2())
//                                                   .eventType(record.value3())
//                                                   .eventTimestamp(record.value4())
//                                                   .techInfo(record.value5())
//                                                   .severity(record.value6())
//                                                   .build());
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
}
