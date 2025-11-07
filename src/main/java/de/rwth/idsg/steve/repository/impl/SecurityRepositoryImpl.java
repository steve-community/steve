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
import de.rwth.idsg.steve.web.dto.BooleanType;
import de.rwth.idsg.steve.web.dto.SignedCertificateQueryForm;
import de.rwth.idsg.steve.repository.dto.InstalledCertificate;
import de.rwth.idsg.steve.repository.dto.SecurityEvent;
import de.rwth.idsg.steve.repository.dto.StatusEvent;
import de.rwth.idsg.steve.utils.CertificateUtils;
import de.rwth.idsg.steve.web.dto.InstalledCertificateQueryForm;
import de.rwth.idsg.steve.web.dto.SecurityEventsQueryForm;
import de.rwth.idsg.steve.web.dto.StatusEventType;
import de.rwth.idsg.steve.web.dto.StatusEventsQueryForm;
import de.rwth.idsg.steve.web.dto.ocpp.GetLogParams;
import de.rwth.idsg.steve.web.dto.ocpp.SignedUpdateFirmwareParams;
import jooq.steve.db.tables.records.CertificateRecord;
import jooq.steve.db.tables.records.ChargeBoxCertificateInstalledRecord;
import jooq.steve.db.tables.records.ChargeBoxCertificateSignedViewRecord;
import jooq.steve.db.tables.records.ChargeBoxFirmwareUpdateJobRecord;
import jooq.steve.db.tables.records.ChargeBoxLogUploadJobRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp._2022._02.security.CertificateHashData;
import org.joda.time.DateTime;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Nullable;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static de.rwth.idsg.steve.utils.CustomDSL.date;
import static de.rwth.idsg.steve.utils.CustomDSL.includes;
import static jooq.steve.db.Tables.CERTIFICATE;
import static jooq.steve.db.Tables.CHARGE_BOX_CERTIFICATE_SIGNED;
import static jooq.steve.db.Tables.CHARGE_BOX_CERTIFICATE_INSTALLED;
import static jooq.steve.db.Tables.CHARGE_BOX_CERTIFICATE_SIGNED_VIEW;
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
public class SecurityRepositoryImpl implements SecurityRepository {

    private final DSLContext ctx;

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
    public void insertLogUploadStatus(String chargeBoxId, Integer requestId, String status, DateTime timestamp) {
        var chargeBoxPk = getChargeBoxPkQuery(chargeBoxId);

        ctx.insertInto(CHARGE_BOX_LOG_UPLOAD_EVENT)
            .set(CHARGE_BOX_LOG_UPLOAD_EVENT.CHARGE_BOX_PK, chargeBoxPk)
            .set(CHARGE_BOX_LOG_UPLOAD_EVENT.JOB_ID, requestId)
            .set(CHARGE_BOX_LOG_UPLOAD_EVENT.EVENT_STATUS, status)
            .set(CHARGE_BOX_LOG_UPLOAD_EVENT.EVENT_TIMESTAMP, timestamp)
            .execute();
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
    public CertificateRecord insertCertificate(X509Certificate cert, String certificateChainPEM) {
        var rec = new CertificateRecord();
        rec.setCreatedAt(DateTime.now());
        rec.setSerialNumber(cert.getSerialNumber().toString());
        rec.setIssuerName(cert.getIssuerX500Principal().getName());
        rec.setSubjectName(cert.getSubjectX500Principal().getName());
        rec.setOrganizationName(CertificateUtils.getOrganization(cert));
        rec.setCommonName(CertificateUtils.getCommonName(cert));
        rec.setKeySize(CertificateUtils.getKeySize(cert));
        rec.setValidFrom(new DateTime(cert.getNotBefore().getTime()));
        rec.setValidTo(new DateTime(cert.getNotAfter().getTime()));
        rec.setSignatureAlgorithm(CertificateUtils.getSignatureAlgorithm(cert));
        rec.setCertificateChainPem(certificateChainPEM);

        int id = ctx.insertInto(CERTIFICATE)
            .set(rec)
            .returningResult(CERTIFICATE.CERTIFICATE_ID)
            .fetchOne()
            .value1();

        rec.setCertificateId(id);
        return rec;
    }

    @Override
    public void insertCertificateSignResponse(String chargeBoxId, int certificateId, boolean accepted) {
        var chargeBoxPk = getChargeBoxPkQuery(chargeBoxId);

        ctx.insertInto(CHARGE_BOX_CERTIFICATE_SIGNED)
            .set(CHARGE_BOX_CERTIFICATE_SIGNED.CHARGE_BOX_PK, chargeBoxPk)
            .set(CHARGE_BOX_CERTIFICATE_SIGNED.CERTIFICATE_ID, certificateId)
            .set(CHARGE_BOX_CERTIFICATE_SIGNED.ACCEPTED, accepted)
            .set(CHARGE_BOX_CERTIFICATE_SIGNED.RESPONDED_AT, DateTime.now())
            .execute();
    }

    @Override
    public ChargeBoxCertificateInstalledRecord getInstalledCertificateRecord(long installedCertificateId) {
        var rec = ctx.selectFrom(CHARGE_BOX_CERTIFICATE_INSTALLED)
            .where(CHARGE_BOX_CERTIFICATE_INSTALLED.ID.eq(installedCertificateId))
            .fetchOne();

        if (rec == null) {
            throw new SteveException.NotFound("Installed certificate not found");
        }

        return rec;
    }

    @Override
    public void deleteInstalledCertificate(long installedCertificateId) {
        ctx.deleteFrom(CHARGE_BOX_CERTIFICATE_INSTALLED)
            .where(CHARGE_BOX_CERTIFICATE_INSTALLED.ID.eq(installedCertificateId))
            .execute();
    }

    @Override
    public void deleteInstalledCertificates(String chargeBoxId, String certificateType) {
        var chargeBoxPk = getChargeBoxPkQuery(chargeBoxId);

        ctx.deleteFrom(CHARGE_BOX_CERTIFICATE_INSTALLED)
            .where(CHARGE_BOX_CERTIFICATE_INSTALLED.CHARGE_BOX_PK.eq(chargeBoxPk))
            .and(CHARGE_BOX_CERTIFICATE_INSTALLED.CERTIFICATE_TYPE.eq(certificateType))
            .execute();
    }

    @Override
    public void insertInstalledCertificates(String chargeBoxId, String certificateType, List<CertificateHashData> certificateHashData) {
        if (CollectionUtils.isEmpty(certificateHashData)) {
            return;
        }

        try {
            var chargeBoxPk = getChargeBoxPkQuery(chargeBoxId).fetchOne(CHARGE_BOX.CHARGE_BOX_PK);
            DateTime now = DateTime.now();

            var batch = certificateHashData.stream()
                .map(it -> ctx
                    .newRecord(CHARGE_BOX_CERTIFICATE_INSTALLED)
                    .setChargeBoxPk(chargeBoxPk)
                    .setRespondedAt(now)
                    .setCertificateType(certificateType)
                    .setHashAlgorithm(it.getHashAlgorithm().value())
                    .setIssuerNameHash(it.getIssuerNameHash())
                    .setIssuerKeyHash(it.getIssuerKeyHash())
                    .setSerialNumber(it.getSerialNumber())
                );

            ctx.loadInto(CHARGE_BOX_CERTIFICATE_INSTALLED)
                .loadRecords(batch)
                .fieldsCorresponding()
                .execute();
        } catch (Exception e) {
            throw new SteveException("Failed to insert InstalledCertificates", e);
        }
    }

    @Override
    public List<InstalledCertificate> getInstalledCertificates(InstalledCertificateQueryForm form) {
        List<Condition> conditions = new ArrayList<>();

        if (form.isChargeBoxIdSet()) {
            conditions.add(CHARGE_BOX.CHARGE_BOX_ID.eq(form.getChargeBoxId()));
        }

        if (form.getCertificateType() != null) {
            conditions.add(CHARGE_BOX_CERTIFICATE_INSTALLED.CERTIFICATE_TYPE.eq(form.getCertificateType().value()));
        }

        return ctx.select(
                CHARGE_BOX_CERTIFICATE_INSTALLED.ID,
                CHARGE_BOX.CHARGE_BOX_ID,
                CHARGE_BOX_CERTIFICATE_INSTALLED.CHARGE_BOX_PK,
                CHARGE_BOX_CERTIFICATE_INSTALLED.RESPONDED_AT,
                CHARGE_BOX_CERTIFICATE_INSTALLED.CERTIFICATE_TYPE,
                CHARGE_BOX_CERTIFICATE_INSTALLED.HASH_ALGORITHM,
                CHARGE_BOX_CERTIFICATE_INSTALLED.ISSUER_NAME_HASH,
                CHARGE_BOX_CERTIFICATE_INSTALLED.ISSUER_KEY_HASH,
                CHARGE_BOX_CERTIFICATE_INSTALLED.SERIAL_NUMBER)
            .from(CHARGE_BOX_CERTIFICATE_INSTALLED)
            .join(CHARGE_BOX).on(CHARGE_BOX_CERTIFICATE_INSTALLED.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
            .where(conditions)
            .orderBy(CHARGE_BOX_CERTIFICATE_INSTALLED.RESPONDED_AT.desc())
            .fetch(record -> InstalledCertificate.builder()
                .id(record.value1())
                .chargeBoxId(record.value2())
                .chargeBoxPk(record.value3())
                .respondedAt(record.value4())
                .certificateType(record.value5())
                .hashAlgorithm(record.value6())
                .issuerNameHash(record.value7())
                .issuerKeyHash(record.value8())
                .serialNumber(record.value9())
                .build()
            );
    }

    @Override
    public List<ChargeBoxCertificateSignedViewRecord> getSignedCertificates(SignedCertificateQueryForm form) {
        List<Condition> conditions = new ArrayList<>();

        if (form.isChargeBoxIdSet()) {
            conditions.add(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.CHARGE_BOX_ID.eq(form.getChargeBoxId()));
        }

        if (form.getSerialNumber() != null) {
            conditions.add(includes(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.SERIAL_NUMBER, form.getSerialNumber()));
        }

        if (form.getIssuerName() != null) {
            conditions.add(includes(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.SERIAL_NUMBER, form.getIssuerName()));
        }

        if (form.getSubjectName() != null) {
            conditions.add(includes(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.SERIAL_NUMBER, form.getSubjectName()));
        }

        if (form.getOrganizationName() != null) {
            conditions.add(includes(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.SERIAL_NUMBER, form.getOrganizationName()));
        }

        if (form.getAccepted() != BooleanType.ALL) {
            conditions.add(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.ACCEPTED.eq(form.getAccepted().getBoolValue()));
        }

        return ctx.selectFrom(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW)
            .where(conditions)
            .fetch();
    }

    private SelectConditionStep<Record1<Integer>> getChargeBoxPkQuery(String chargeBoxId) {
        return ctx.select(CHARGE_BOX.CHARGE_BOX_PK)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(chargeBoxId));
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
