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
import de.rwth.idsg.steve.repository.CertificateRepository;
import de.rwth.idsg.steve.repository.dto.InstalledCertificate;
import de.rwth.idsg.steve.utils.CertificateUtils;
import de.rwth.idsg.steve.web.dto.BooleanType;
import de.rwth.idsg.steve.web.dto.InstalledCertificateQueryForm;
import de.rwth.idsg.steve.web.dto.SignedCertificateQueryForm;
import jooq.steve.db.tables.records.CertificateRecord;
import jooq.steve.db.tables.records.ChargeBoxCertificateInstalledRecord;
import jooq.steve.db.tables.records.ChargeBoxCertificateSignedViewRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp._2022._02.security.CertificateHashData;
import org.joda.time.DateTime;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static de.rwth.idsg.steve.utils.CustomDSL.getTimeCondition;
import static de.rwth.idsg.steve.utils.CustomDSL.includes;
import static jooq.steve.db.Tables.CERTIFICATE;
import static jooq.steve.db.Tables.CHARGE_BOX_CERTIFICATE_INSTALLED;
import static jooq.steve.db.Tables.CHARGE_BOX_CERTIFICATE_SIGNED;
import static jooq.steve.db.Tables.CHARGE_BOX_CERTIFICATE_SIGNED_VIEW;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CertificateRepositoryImpl implements CertificateRepository {

    private final DSLContext ctx;

    // -------------------------------------------------------------------------
    // Certificate signing
    // -------------------------------------------------------------------------

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
    public List<ChargeBoxCertificateSignedViewRecord> getSignedCertificates(SignedCertificateQueryForm form) {
        List<Condition> conditions = new ArrayList<>();

        if (form.isChargeBoxIdSet()) {
            conditions.add(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.CHARGE_BOX_ID.eq(form.getChargeBoxId()));
        }

        if (form.getSerialNumber() != null) {
            conditions.add(includes(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.SERIAL_NUMBER, form.getSerialNumber()));
        }

        if (form.getIssuerName() != null) {
            conditions.add(includes(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.ISSUER_NAME, form.getIssuerName()));
        }

        if (form.getSubjectName() != null) {
            conditions.add(includes(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.SUBJECT_NAME, form.getSubjectName()));
        }

        if (form.getOrganizationName() != null) {
            conditions.add(includes(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.ORGANIZATION_NAME, form.getOrganizationName()));
        }

        if (form.getAccepted() != BooleanType.ALL) {
            conditions.add(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.ACCEPTED.eq(form.getAccepted().getBoolValue()));
        }

        var timeCondition = getTimeCondition(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW.RESPONDED_AT, form);
        if (timeCondition != null) {
            conditions.add(timeCondition);
        }

        return ctx.selectFrom(CHARGE_BOX_CERTIFICATE_SIGNED_VIEW)
            .where(conditions)
            .fetch();
    }

    // -------------------------------------------------------------------------
    // Installed certificates
    // -------------------------------------------------------------------------

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

        var timeCondition = getTimeCondition(CHARGE_BOX_CERTIFICATE_INSTALLED.RESPONDED_AT, form);
        if (timeCondition != null) {
            conditions.add(timeCondition);
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
    public List<Long> getInstalledCertificateIds(String chargeBoxId) {
        return ctx.select(CHARGE_BOX_CERTIFICATE_INSTALLED.ID)
            .from(CHARGE_BOX_CERTIFICATE_INSTALLED)
            .join(CHARGE_BOX).on(CHARGE_BOX_CERTIFICATE_INSTALLED.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(chargeBoxId))
            .orderBy(CHARGE_BOX_CERTIFICATE_INSTALLED.ID.desc())
            .fetch(CHARGE_BOX_CERTIFICATE_INSTALLED.ID);
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
