package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.web.dto.Address;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.11.2015
 */
public interface AddressRepository {
    Integer updateOrInsert(DSLContext ctx, Address address);
    void delete(DSLContext ctx, SelectConditionStep<Record1<Integer>> addressPkSelect);
}
