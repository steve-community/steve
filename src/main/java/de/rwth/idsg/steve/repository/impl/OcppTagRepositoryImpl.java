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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.dto.OcppTag.Overview;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import jooq.steve.db.tables.OcppTagActivity;
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import jooq.steve.db.tables.records.OcppTagRecord;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.JoinType;
import org.jooq.Record7;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.utils.DateTimeUtils.humanize;
import static de.rwth.idsg.steve.utils.DateTimeUtils.toDateTime;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.OcppTagActivity.OCPP_TAG_ACTIVITY;

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
    @SuppressWarnings("unchecked")
    public List<Overview> getOverview(OcppTagQueryForm form) {
        SelectQuery selectQuery = ctx.selectQuery();
        selectQuery.addFrom(OCPP_TAG_ACTIVITY);

        OcppTagActivity parentTable = OCPP_TAG_ACTIVITY.as("parent");

        selectQuery.addSelect(
                OCPP_TAG_ACTIVITY.OCPP_TAG_PK,
                parentTable.OCPP_TAG_PK,
                OCPP_TAG_ACTIVITY.ID_TAG,
                OCPP_TAG_ACTIVITY.PARENT_ID_TAG,
                OCPP_TAG_ACTIVITY.EXPIRY_DATE,
                OCPP_TAG_ACTIVITY.IN_TRANSACTION,
                OCPP_TAG_ACTIVITY.BLOCKED
        );

        selectQuery.addJoin(parentTable, JoinType.LEFT_OUTER_JOIN, parentTable.ID_TAG.eq(OCPP_TAG_ACTIVITY.PARENT_ID_TAG));

        if (form.isIdTagSet()) {
            selectQuery.addConditions(OCPP_TAG_ACTIVITY.ID_TAG.eq(form.getIdTag()));
        }

        if (form.isParentIdTagSet()) {
            selectQuery.addConditions(OCPP_TAG_ACTIVITY.PARENT_ID_TAG.eq(form.getParentIdTag()));
        }

        switch (form.getExpired()) {
            case ALL:
                break;

            case TRUE:
                selectQuery.addConditions(OCPP_TAG_ACTIVITY.EXPIRY_DATE.lessOrEqual(DateTime.now()));
                break;

            case FALSE:
                selectQuery.addConditions(
                        OCPP_TAG_ACTIVITY.EXPIRY_DATE.isNull().or(OCPP_TAG_ACTIVITY.EXPIRY_DATE.greaterThan(DateTime.now()))
                );
                break;

            default:
                throw new SteveException("Unknown enum type");
        }

        processBooleanType(selectQuery, OCPP_TAG_ACTIVITY.IN_TRANSACTION, form.getInTransaction());
        processBooleanType(selectQuery, OCPP_TAG_ACTIVITY.BLOCKED, form.getBlocked());

        return selectQuery.fetch().map(new UserMapper());
    }

    @Override
    public Result<OcppTagActivityRecord> getRecords() {
        return ctx.selectFrom(OCPP_TAG_ACTIVITY)
                  .fetch();
    }

    @Override
    public Result<OcppTagActivityRecord> getRecords(List<String> idTagList) {
        return ctx.selectFrom(OCPP_TAG_ACTIVITY)
                  .where(OCPP_TAG_ACTIVITY.ID_TAG.in(idTagList))
                  .fetch();
    }

    @Override
    public OcppTagActivityRecord getRecord(String idTag) {
        return ctx.selectFrom(OCPP_TAG_ACTIVITY)
                  .where(OCPP_TAG_ACTIVITY.ID_TAG.equal(idTag))
                  .fetchOne();
    }

    @Override
    public OcppTagActivityRecord getRecord(int ocppTagPk) {
        return ctx.selectFrom(OCPP_TAG_ACTIVITY)
                  .where(OCPP_TAG_ACTIVITY.OCPP_TAG_PK.equal(ocppTagPk))
                  .fetchOne();
    }

    @Override
    public List<String> getIdTags() {
        return ctx.select(OCPP_TAG.ID_TAG)
                  .from(OCPP_TAG)
                  .fetch(OCPP_TAG.ID_TAG);
    }

    @Override
    public List<String> getActiveIdTags() {
        return ctx.select(OCPP_TAG_ACTIVITY.ID_TAG)
                  .from(OCPP_TAG_ACTIVITY)
                  .where(OCPP_TAG_ACTIVITY.IN_TRANSACTION.isFalse())
                    .and(OCPP_TAG_ACTIVITY.BLOCKED.isFalse())
                    .and(OCPP_TAG_ACTIVITY.EXPIRY_DATE.isNull().or(OCPP_TAG_ACTIVITY.EXPIRY_DATE.greaterThan(DateTime.now())))
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
    public String getParentIdtag(String idTag) {
        return ctx.select(OCPP_TAG.PARENT_ID_TAG)
                  .from(OCPP_TAG)
                  .where(OCPP_TAG.ID_TAG.eq(idTag))
                  .fetchOne()
                  .value1();
    }

    @Override
    public void addOcppTagList(List<String> idTagList) {
        List<OcppTagRecord> batch = idTagList.stream()
                                             .map(s -> ctx.newRecord(OCPP_TAG)
                                                          .setIdTag(s))
                                             .collect(Collectors.toList());

        ctx.batchInsert(batch).execute();
    }

    @Override
    public int addOcppTag(OcppTagForm u) {
        try {
            return ctx.insertInto(OCPP_TAG)
                      .set(OCPP_TAG.ID_TAG, u.getIdTag())
                      .set(OCPP_TAG.PARENT_ID_TAG, u.getParentIdTag())
                      .set(OCPP_TAG.EXPIRY_DATE, toDateTime(u.getExpiration()))
                      .set(OCPP_TAG.MAX_ACTIVE_TRANSACTION_COUNT, u.getMaxActiveTransactionCount())
                      .set(OCPP_TAG.NOTE, u.getNote())
                      .returning(OCPP_TAG.OCPP_TAG_PK)
                      .fetchOne()
                      .getOcppTagPk();

        } catch (DataAccessException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new SteveException("A user with idTag '%s' already exists.", u.getIdTag());
            } else {
                throw new SteveException("Execution of addOcppTag for idTag '%s' FAILED.", u.getIdTag(), e);
            }
        }
    }

    @Override
    public void updateOcppTag(OcppTagForm u) {
        try {
            ctx.update(OCPP_TAG)
               .set(OCPP_TAG.PARENT_ID_TAG, u.getParentIdTag())
               .set(OCPP_TAG.EXPIRY_DATE, toDateTime(u.getExpiration()))
               .set(OCPP_TAG.MAX_ACTIVE_TRANSACTION_COUNT, u.getMaxActiveTransactionCount())
               .set(OCPP_TAG.NOTE, u.getNote())
               .where(OCPP_TAG.OCPP_TAG_PK.equal(u.getOcppTagPk()))
               .execute();
        } catch (DataAccessException e) {
            throw new SteveException("Execution of updateOcppTag for idTag '%s' FAILED.", u.getIdTag(), e);
        }
    }

    @Override
    public void deleteOcppTag(int ocppTagPk) {
        try {
            ctx.delete(OCPP_TAG)
               .where(OCPP_TAG.OCPP_TAG_PK.equal(ocppTagPk))
               .execute();
        } catch (DataAccessException e) {
            throw new SteveException("Execution of deleteOcppTag for idTag FAILED.", e);
        }
    }

    private void processBooleanType(SelectQuery selectQuery,
                                    TableField<OcppTagActivityRecord, Boolean> field,
                                    OcppTagQueryForm.BooleanType type) {
        if (type != OcppTagQueryForm.BooleanType.ALL) {
            selectQuery.addConditions(field.eq(type.getBoolValue()));
        }
    }

    private static class UserMapper
            implements RecordMapper<Record7<Integer, Integer, String, String, DateTime, Boolean, Boolean>, Overview> {
        @Override
        public Overview map(Record7<Integer, Integer, String, String, DateTime, Boolean, Boolean> r) {
            return Overview.builder()
                          .ocppTagPk(r.value1())
                          .parentOcppTagPk(r.value2())
                          .idTag(r.value3())
                          .parentIdTag(r.value4())
                          .expiryDateDT(r.value5())
                          .expiryDate(humanize(r.value5()))
                          .inTransaction(r.value6())
                          .blocked(r.value7())
                          .build();
        }
    }
}
