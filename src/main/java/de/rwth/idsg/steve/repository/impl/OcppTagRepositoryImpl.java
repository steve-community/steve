package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.dto.OcppTag.Overview;
import de.rwth.idsg.steve.utils.CustomDSL;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import jooq.steve.db.tables.OcppTag;
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

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
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
        selectQuery.addFrom(OCPP_TAG);

        OcppTag parentTable = OCPP_TAG.as("parent");

        selectQuery.addSelect(
                OCPP_TAG.OCPP_TAG_PK,
                parentTable.OCPP_TAG_PK,
                OCPP_TAG.ID_TAG,
                OCPP_TAG.PARENT_ID_TAG,
                OCPP_TAG.EXPIRY_DATE,
                OCPP_TAG.IN_TRANSACTION,
                OCPP_TAG.BLOCKED
        );

        selectQuery.addJoin(parentTable, JoinType.LEFT_OUTER_JOIN, parentTable.ID_TAG.eq(OCPP_TAG.PARENT_ID_TAG));

        if (form.isIdTagSet()) {
            selectQuery.addConditions(OCPP_TAG.ID_TAG.eq(form.getIdTag()));
        }

        if (form.isParentIdTagSet()) {
            selectQuery.addConditions(OCPP_TAG.PARENT_ID_TAG.eq(form.getParentIdTag()));
        }

        switch (form.getExpired()) {
            case ALL:
                break;

            case TRUE:
                selectQuery.addConditions(OCPP_TAG.EXPIRY_DATE.lessOrEqual(DateTime.now()));
                break;

            case FALSE:
                selectQuery.addConditions(
                        OCPP_TAG.EXPIRY_DATE.isNull().or(OCPP_TAG.EXPIRY_DATE.greaterThan(DateTime.now()))
                );
                break;

            default:
                throw new SteveException("Unknown enum type");
        }

        processBooleanType(selectQuery, OCPP_TAG.IN_TRANSACTION, form.getInTransaction());
        processBooleanType(selectQuery, OCPP_TAG.BLOCKED, form.getBlocked());

        return selectQuery.fetch().map(new UserMapper());
    }

    @Override
    public Result<OcppTagRecord> getRecords() {
        return ctx.selectFrom(OCPP_TAG)
                  .fetch();
    }

    @Override
    public Result<OcppTagRecord> getRecords(List<String> idTagList) {
        return ctx.selectFrom(OCPP_TAG)
                  .where(OCPP_TAG.ID_TAG.in(idTagList))
                  .fetch();
    }

    @Override
    public OcppTagRecord getRecord(String idTag) {
        return ctx.selectFrom(OCPP_TAG)
                  .where(OCPP_TAG.ID_TAG.equal(idTag))
                  .fetchOne();
    }

    @Override
    public OcppTagRecord getRecord(int ocppTagPk) {
        return ctx.selectFrom(OCPP_TAG)
                  .where(OCPP_TAG.OCPP_TAG_PK.equal(ocppTagPk))
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
        return ctx.select(OCPP_TAG.ID_TAG)
                  .from(OCPP_TAG)
                  .where(OCPP_TAG.IN_TRANSACTION.isFalse())
                    .and(OCPP_TAG.BLOCKED.isFalse())
                    .and(OCPP_TAG.EXPIRY_DATE.isNull().or(OCPP_TAG.EXPIRY_DATE.greaterThan(DateTime.now())))
                  .fetch(OCPP_TAG.ID_TAG);
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
                                                          .setIdTag(s)
                                                          .setBlocked(false)
                                                          .setInTransaction(false))
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
                      .set(OCPP_TAG.NOTE, u.getNote())
                      .set(OCPP_TAG.BLOCKED, false)
                      .set(OCPP_TAG.IN_TRANSACTION, false)
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
               .set(OCPP_TAG.NOTE, u.getNote())
               .set(OCPP_TAG.BLOCKED, u.getBlocked())
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
                                    TableField<OcppTagRecord, Boolean> field,
                                    OcppTagQueryForm.BooleanType type) {
        switch (type) {
            case ALL:
                break;

            default:
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
