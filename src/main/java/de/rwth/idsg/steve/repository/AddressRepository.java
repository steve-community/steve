package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.web.dto.Address;
import jooq.steve.db.tables.records.AddressRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.11.2015
 */
public interface AddressRepository {
    AddressRecord get(DSLContext ctx, Integer addressPk);
    Integer updateOrInsert(DSLContext ctx, Address address);
    void delete(DSLContext ctx, SelectConditionStep<Record1<Integer>> addressPkSelect);
}
