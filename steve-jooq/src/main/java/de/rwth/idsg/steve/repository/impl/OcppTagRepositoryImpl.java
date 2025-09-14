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
import de.rwth.idsg.steve.jooq.mapper.OcppTagMapper;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.dto.OcppTag.OcppTagOverview;
import de.rwth.idsg.steve.repository.dto.OcppTagActivity;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.JoinType;
import org.jooq.Record11;
import org.jooq.RecordMapper;
import org.jooq.SelectQuery;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static de.rwth.idsg.steve.utils.CustomDSL.includes;
import static de.rwth.idsg.steve.utils.DateTimeUtils.humanize;
import static de.rwth.idsg.steve.utils.DateTimeUtils.toInstant;
import static de.rwth.idsg.steve.utils.DateTimeUtils.toLocalDateTime;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.OcppTagActivity.OCPP_TAG_ACTIVITY;
import static jooq.steve.db.tables.UserOcppTag.USER_OCPP_TAG;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 14.08.2014
 */
@Slf4j
@Repository
public class OcppTagRepositoryImpl implements OcppTagRepository {

    private final DSLContext ctx;

    @Autowired
    public OcppTagRepositoryImpl(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public List<OcppTagOverview> getOverview(OcppTagQueryForm form) {
        SelectQuery selectQuery = ctx.selectQuery();
        selectQuery.addFrom(OCPP_TAG_ACTIVITY);

        var parentTable = OCPP_TAG_ACTIVITY.as("parent");
        var userOcppTagTable = USER_OCPP_TAG.as("user_ocpp_tag");

        selectQuery.addSelect(
                OCPP_TAG_ACTIVITY.OCPP_TAG_PK,
                parentTable.OCPP_TAG_PK,
                OCPP_TAG_ACTIVITY.ID_TAG,
                OCPP_TAG_ACTIVITY.PARENT_ID_TAG,
                OCPP_TAG_ACTIVITY.EXPIRY_DATE,
                OCPP_TAG_ACTIVITY.IN_TRANSACTION,
                OCPP_TAG_ACTIVITY.BLOCKED,
                OCPP_TAG_ACTIVITY.MAX_ACTIVE_TRANSACTION_COUNT,
                OCPP_TAG_ACTIVITY.ACTIVE_TRANSACTION_COUNT,
                OCPP_TAG_ACTIVITY.NOTE,
                userOcppTagTable.USER_PK);

        selectQuery.addJoin(
                parentTable, JoinType.LEFT_OUTER_JOIN, parentTable.ID_TAG.eq(OCPP_TAG_ACTIVITY.PARENT_ID_TAG));
        selectQuery.addJoin(
                userOcppTagTable,
                JoinType.LEFT_OUTER_JOIN,
                userOcppTagTable.OCPP_TAG_PK.eq(OCPP_TAG_ACTIVITY.OCPP_TAG_PK));

        if (form.isOcppTagPkSet()) {
            selectQuery.addConditions(OCPP_TAG_ACTIVITY.OCPP_TAG_PK.eq(form.getOcppTagPk()));
        }

        if (form.isIdTagSet()) {
            selectQuery.addConditions(OCPP_TAG_ACTIVITY.ID_TAG.eq(form.getIdTag()));
        }

        if (form.isParentIdTagSet()) {
            selectQuery.addConditions(OCPP_TAG_ACTIVITY.PARENT_ID_TAG.eq(form.getParentIdTag()));
        }

        if (form.isUserIdSet()) {
            selectQuery.addConditions(userOcppTagTable.USER_PK.eq(form.getUserId()));
        }

        if (form.isNoteSet()) {
            selectQuery.addConditions(includes(OCPP_TAG_ACTIVITY.NOTE, form.getNote()));
        }

        switch (form.getUserFilter()) {
            case OnlyTagsWithUser:
                selectQuery.addConditions(userOcppTagTable.USER_PK.isNotNull());
                break;
            case OnlyTagsWithoutUser:
                selectQuery.addConditions(userOcppTagTable.USER_PK.isNull());
                break;
            default:
                break;
        }

        switch (form.getExpired()) {
            case ALL -> {
                // want all: no filter
            }
            case TRUE ->
                selectQuery.addConditions(OCPP_TAG_ACTIVITY.EXPIRY_DATE.lessOrEqual(DSL.currentLocalDateTime()));
            case FALSE ->
                selectQuery.addConditions(OCPP_TAG_ACTIVITY
                        .EXPIRY_DATE
                        .isNull()
                        .or(OCPP_TAG_ACTIVITY.EXPIRY_DATE.greaterThan(DSL.currentLocalDateTime())));
            default -> throw new SteveException.InternalError("Unknown enum type");
        }

        processBooleanType(selectQuery, OCPP_TAG_ACTIVITY.IN_TRANSACTION, form.getInTransaction());
        processBooleanType(selectQuery, OCPP_TAG_ACTIVITY.BLOCKED, form.getBlocked());

        return selectQuery.fetch().map(new OcppTagOverviewMapper());
    }

    @Override
    public List<OcppTagActivity> getRecords() {
        return ctx.selectFrom(OCPP_TAG_ACTIVITY).fetch(OcppTagMapper::fromRecord);
    }

    @Override
    public List<OcppTagActivity> getRecords(List<String> idTagList) {
        return ctx.selectFrom(OCPP_TAG_ACTIVITY)
                .where(OCPP_TAG_ACTIVITY.ID_TAG.in(idTagList))
                .fetch(OcppTagMapper::fromRecord);
    }

    @Override
    public Optional<OcppTagActivity> getRecord(String idTag) {
        return ctx.selectFrom(OCPP_TAG_ACTIVITY)
                .where(OCPP_TAG_ACTIVITY.ID_TAG.equal(idTag))
                .fetchOptional(OcppTagMapper::fromRecord);
    }

    @Override
    public Optional<OcppTagActivity> getRecord(int ocppTagPk) {
        return ctx.selectFrom(OCPP_TAG_ACTIVITY)
                .where(OCPP_TAG_ACTIVITY.OCPP_TAG_PK.equal(ocppTagPk))
                .fetchOptional(OcppTagMapper::fromRecord);
    }

    @Override
    public List<String> getIdTags() {
        return ctx.select(OCPP_TAG.ID_TAG).from(OCPP_TAG).fetch(OCPP_TAG.ID_TAG);
    }

    @Override
    public List<String> getIdTagsWithoutUser() {
        return ctx.select(OCPP_TAG.ID_TAG)
                .from(OCPP_TAG)
                .leftJoin(USER_OCPP_TAG)
                .on(OCPP_TAG.OCPP_TAG_PK.eq(USER_OCPP_TAG.OCPP_TAG_PK))
                .where(USER_OCPP_TAG.OCPP_TAG_PK.isNull())
                .orderBy(OCPP_TAG.ID_TAG)
                .fetch(OCPP_TAG.ID_TAG);
    }

    @Override
    public List<String> getActiveIdTags() {
        return ctx.select(OCPP_TAG_ACTIVITY.ID_TAG)
                .from(OCPP_TAG_ACTIVITY)
                .where(OCPP_TAG_ACTIVITY
                        .ACTIVE_TRANSACTION_COUNT
                        .lessThan(OCPP_TAG_ACTIVITY.MAX_ACTIVE_TRANSACTION_COUNT.cast(Long.class))
                        .or(OCPP_TAG_ACTIVITY.MAX_ACTIVE_TRANSACTION_COUNT.lessThan(0)))
                .and(OCPP_TAG_ACTIVITY.BLOCKED.isFalse())
                .and(OCPP_TAG_ACTIVITY
                        .EXPIRY_DATE
                        .isNull()
                        .or(OCPP_TAG_ACTIVITY.EXPIRY_DATE.greaterThan(DSL.currentLocalDateTime())))
                .fetch(OCPP_TAG_ACTIVITY.ID_TAG);
    }

    @Override
    public List<String> getParentIdTags() {
        return ctx.selectDistinct(OCPP_TAG.PARENT_ID_TAG)
                .from(OCPP_TAG)
                .where(OCPP_TAG.PARENT_ID_TAG.isNotNull())
                .fetch(OCPP_TAG.PARENT_ID_TAG);
    }

    @Override
    public String getParentIdTag(String idTag) {
        return ctx.select(OCPP_TAG.PARENT_ID_TAG)
                .from(OCPP_TAG)
                .where(OCPP_TAG.ID_TAG.eq(idTag))
                .fetchOne()
                .value1();
    }

    @Override
    public void addOcppTagList(List<String> idTagList) {
        var batch =
                idTagList.stream().map(s -> ctx.newRecord(OCPP_TAG).setIdTag(s)).toList();

        ctx.batchInsert(batch).execute();
    }

    @Override
    public int addOcppTag(OcppTagForm u) {
        try {
            return ctx.insertInto(OCPP_TAG)
                    .set(OCPP_TAG.ID_TAG, u.getIdTag())
                    .set(OCPP_TAG.PARENT_ID_TAG, u.getParentIdTag())
                    .set(OCPP_TAG.EXPIRY_DATE, toLocalDateTime(u.getExpiryDate()))
                    .set(OCPP_TAG.MAX_ACTIVE_TRANSACTION_COUNT, u.getMaxActiveTransactionCount())
                    .set(OCPP_TAG.NOTE, u.getNote())
                    .returning(OCPP_TAG.OCPP_TAG_PK)
                    .fetchOne()
                    .getOcppTagPk();

        } catch (DataAccessException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new SteveException.AlreadyExists(
                        "An OCPP tag with id '%s' already exists.".formatted(u.getIdTag()), e);
            } else {
                throw new SteveException.InternalError(
                        "Execution of addOcppTag for idTag '%s' FAILED.".formatted(u.getIdTag()), e);
            }
        }
    }

    @Override
    public void updateOcppTag(OcppTagForm u) {
        try {
            ctx.update(OCPP_TAG)
                    .set(OCPP_TAG.PARENT_ID_TAG, u.getParentIdTag())
                    .set(OCPP_TAG.EXPIRY_DATE, toLocalDateTime(u.getExpiryDate()))
                    .set(OCPP_TAG.MAX_ACTIVE_TRANSACTION_COUNT, u.getMaxActiveTransactionCount())
                    .set(OCPP_TAG.NOTE, u.getNote())
                    .where(OCPP_TAG.OCPP_TAG_PK.equal(u.getOcppTagPk()))
                    .execute();
        } catch (DataAccessException e) {
            throw new SteveException.InternalError(
                    "Execution of updateOcppTag for idTag '%s' FAILED.".formatted(u.getIdTag()), e);
        }
    }

    @Override
    public void deleteOcppTag(int ocppTagPk) {
        try {
            ctx.delete(OCPP_TAG).where(OCPP_TAG.OCPP_TAG_PK.equal(ocppTagPk)).execute();
        } catch (DataAccessException e) {
            throw new SteveException.InternalError("Execution of deleteOcppTag for idTag FAILED.", e);
        }
    }

    private void processBooleanType(
            SelectQuery selectQuery,
            TableField<OcppTagActivityRecord, Boolean> field,
            OcppTagQueryForm.BooleanType type) {
        if (type != OcppTagQueryForm.BooleanType.ALL) {
            selectQuery.addConditions(field.eq(type.getBoolValue()));
        }
    }

    private static class OcppTagOverviewMapper
            implements RecordMapper<
                    Record11<
                            Integer,
                            Integer,
                            String,
                            String,
                            LocalDateTime,
                            Boolean,
                            Boolean,
                            Integer,
                            Long,
                            String,
                            Integer>,
                    OcppTagOverview> {
        @Override
        public OcppTagOverview map(
                Record11<
                                Integer,
                                Integer,
                                String,
                                String,
                                LocalDateTime,
                                Boolean,
                                Boolean,
                                Integer,
                                Long,
                                String,
                                Integer>
                        r) {
            return OcppTagOverview.builder()
                    .ocppTagPk(r.value1())
                    .parentOcppTagPk(r.value2())
                    .idTag(r.value3())
                    .parentIdTag(r.value4())
                    .expiryDate(toInstant(r.value5()))
                    .expiryDateFormatted(humanize(r.value5()))
                    .inTransaction(r.value6())
                    .blocked(r.value7())
                    .maxActiveTransactionCount(r.value8())
                    .activeTransactionCount(r.value9())
                    .note(r.value10())
                    .userPk(r.value11())
                    .build();
        }
    }
}
