/*
 * This file is generated by jOOQ.
 */
package jooq.steve.db.tables.records;


import javax.annotation.processing.Generated;

import jooq.steve.db.tables.OcppTagActivity;

import org.joda.time.DateTime;
import org.jooq.Field;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.TableRecordImpl;


/**
 * VIEW
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.15.1",
        "schema version:1.0.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OcppTagActivityRecord extends TableRecordImpl<OcppTagActivityRecord> implements Record9<Integer, String, String, DateTime, Integer, String, Long, Boolean, Boolean> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>stevedb.ocpp_tag_activity.ocpp_tag_pk</code>.
     */
    public OcppTagActivityRecord setOcppTagPk(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>stevedb.ocpp_tag_activity.ocpp_tag_pk</code>.
     */
    public Integer getOcppTagPk() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>stevedb.ocpp_tag_activity.id_tag</code>.
     */
    public OcppTagActivityRecord setIdTag(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>stevedb.ocpp_tag_activity.id_tag</code>.
     */
    public String getIdTag() {
        return (String) get(1);
    }

    /**
     * Setter for <code>stevedb.ocpp_tag_activity.parent_id_tag</code>.
     */
    public OcppTagActivityRecord setParentIdTag(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>stevedb.ocpp_tag_activity.parent_id_tag</code>.
     */
    public String getParentIdTag() {
        return (String) get(2);
    }

    /**
     * Setter for <code>stevedb.ocpp_tag_activity.expiry_date</code>.
     */
    public OcppTagActivityRecord setExpiryDate(DateTime value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>stevedb.ocpp_tag_activity.expiry_date</code>.
     */
    public DateTime getExpiryDate() {
        return (DateTime) get(3);
    }

    /**
     * Setter for
     * <code>stevedb.ocpp_tag_activity.max_active_transaction_count</code>.
     */
    public OcppTagActivityRecord setMaxActiveTransactionCount(Integer value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for
     * <code>stevedb.ocpp_tag_activity.max_active_transaction_count</code>.
     */
    public Integer getMaxActiveTransactionCount() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>stevedb.ocpp_tag_activity.note</code>.
     */
    public OcppTagActivityRecord setNote(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>stevedb.ocpp_tag_activity.note</code>.
     */
    public String getNote() {
        return (String) get(5);
    }

    /**
     * Setter for
     * <code>stevedb.ocpp_tag_activity.active_transaction_count</code>.
     */
    public OcppTagActivityRecord setActiveTransactionCount(Long value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for
     * <code>stevedb.ocpp_tag_activity.active_transaction_count</code>.
     */
    public Long getActiveTransactionCount() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>stevedb.ocpp_tag_activity.in_transaction</code>.
     */
    public OcppTagActivityRecord setInTransaction(Boolean value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>stevedb.ocpp_tag_activity.in_transaction</code>.
     */
    public Boolean getInTransaction() {
        return (Boolean) get(7);
    }

    /**
     * Setter for <code>stevedb.ocpp_tag_activity.blocked</code>.
     */
    public OcppTagActivityRecord setBlocked(Boolean value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>stevedb.ocpp_tag_activity.blocked</code>.
     */
    public Boolean getBlocked() {
        return (Boolean) get(8);
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row9<Integer, String, String, DateTime, Integer, String, Long, Boolean, Boolean> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<Integer, String, String, DateTime, Integer, String, Long, Boolean, Boolean> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return OcppTagActivity.OCPP_TAG_ACTIVITY.OCPP_TAG_PK;
    }

    @Override
    public Field<String> field2() {
        return OcppTagActivity.OCPP_TAG_ACTIVITY.ID_TAG;
    }

    @Override
    public Field<String> field3() {
        return OcppTagActivity.OCPP_TAG_ACTIVITY.PARENT_ID_TAG;
    }

    @Override
    public Field<DateTime> field4() {
        return OcppTagActivity.OCPP_TAG_ACTIVITY.EXPIRY_DATE;
    }

    @Override
    public Field<Integer> field5() {
        return OcppTagActivity.OCPP_TAG_ACTIVITY.MAX_ACTIVE_TRANSACTION_COUNT;
    }

    @Override
    public Field<String> field6() {
        return OcppTagActivity.OCPP_TAG_ACTIVITY.NOTE;
    }

    @Override
    public Field<Long> field7() {
        return OcppTagActivity.OCPP_TAG_ACTIVITY.ACTIVE_TRANSACTION_COUNT;
    }

    @Override
    public Field<Boolean> field8() {
        return OcppTagActivity.OCPP_TAG_ACTIVITY.IN_TRANSACTION;
    }

    @Override
    public Field<Boolean> field9() {
        return OcppTagActivity.OCPP_TAG_ACTIVITY.BLOCKED;
    }

    @Override
    public Integer component1() {
        return getOcppTagPk();
    }

    @Override
    public String component2() {
        return getIdTag();
    }

    @Override
    public String component3() {
        return getParentIdTag();
    }

    @Override
    public DateTime component4() {
        return getExpiryDate();
    }

    @Override
    public Integer component5() {
        return getMaxActiveTransactionCount();
    }

    @Override
    public String component6() {
        return getNote();
    }

    @Override
    public Long component7() {
        return getActiveTransactionCount();
    }

    @Override
    public Boolean component8() {
        return getInTransaction();
    }

    @Override
    public Boolean component9() {
        return getBlocked();
    }

    @Override
    public Integer value1() {
        return getOcppTagPk();
    }

    @Override
    public String value2() {
        return getIdTag();
    }

    @Override
    public String value3() {
        return getParentIdTag();
    }

    @Override
    public DateTime value4() {
        return getExpiryDate();
    }

    @Override
    public Integer value5() {
        return getMaxActiveTransactionCount();
    }

    @Override
    public String value6() {
        return getNote();
    }

    @Override
    public Long value7() {
        return getActiveTransactionCount();
    }

    @Override
    public Boolean value8() {
        return getInTransaction();
    }

    @Override
    public Boolean value9() {
        return getBlocked();
    }

    @Override
    public OcppTagActivityRecord value1(Integer value) {
        setOcppTagPk(value);
        return this;
    }

    @Override
    public OcppTagActivityRecord value2(String value) {
        setIdTag(value);
        return this;
    }

    @Override
    public OcppTagActivityRecord value3(String value) {
        setParentIdTag(value);
        return this;
    }

    @Override
    public OcppTagActivityRecord value4(DateTime value) {
        setExpiryDate(value);
        return this;
    }

    @Override
    public OcppTagActivityRecord value5(Integer value) {
        setMaxActiveTransactionCount(value);
        return this;
    }

    @Override
    public OcppTagActivityRecord value6(String value) {
        setNote(value);
        return this;
    }

    @Override
    public OcppTagActivityRecord value7(Long value) {
        setActiveTransactionCount(value);
        return this;
    }

    @Override
    public OcppTagActivityRecord value8(Boolean value) {
        setInTransaction(value);
        return this;
    }

    @Override
    public OcppTagActivityRecord value9(Boolean value) {
        setBlocked(value);
        return this;
    }

    @Override
    public OcppTagActivityRecord values(Integer value1, String value2, String value3, DateTime value4, Integer value5, String value6, Long value7, Boolean value8, Boolean value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OcppTagActivityRecord
     */
    public OcppTagActivityRecord() {
        super(OcppTagActivity.OCPP_TAG_ACTIVITY);
    }

    /**
     * Create a detached, initialised OcppTagActivityRecord
     */
    public OcppTagActivityRecord(Integer ocppTagPk, String idTag, String parentIdTag, DateTime expiryDate, Integer maxActiveTransactionCount, String note, Long activeTransactionCount, Boolean inTransaction, Boolean blocked) {
        super(OcppTagActivity.OCPP_TAG_ACTIVITY);

        setOcppTagPk(ocppTagPk);
        setIdTag(idTag);
        setParentIdTag(parentIdTag);
        setExpiryDate(expiryDate);
        setMaxActiveTransactionCount(maxActiveTransactionCount);
        setNote(note);
        setActiveTransactionCount(activeTransactionCount);
        setInTransaction(inTransaction);
        setBlocked(blocked);
    }
}
