package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.utils.CustomDSL;
import de.rwth.idsg.steve.web.dto.Address;
import jooq.steve.db.tables.records.AddressRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;

import static jooq.steve.db.tables.Address.ADDRESS;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.11.2015
 */
@Slf4j
@Repository
public class AddressRepositoryImpl implements AddressRepository {

    @Override
    public AddressRecord get(DSLContext ctx, Integer addressPk) {
        if (addressPk == null) {
            return null;
        }

        AddressRecord ar = ctx.selectFrom(ADDRESS)
                              .where(ADDRESS.ADDRESS_PK.equal(addressPk))
                              .fetchOne();

        if (ar != null) {
            ar.detach();
        }

        return ar;
    }

    /**
     * The call site does not care about the database internal specifics,
     * but only wants to save the address. We return
     *
     *  - null, if address was empty (no field set)
     *  - a new address_pk, if we have to do an INSERT
     *  - address_pk from the input parameter (for convenience)
     *
     * to spare us the decision logic in the call site, so that it can
     * just reference the returned value in parent's table.
     *
     */
    @Override
    public Integer updateOrInsert(DSLContext ctx, Address address) {
        if (address.isEmpty()) {
            return null;

        } else if (address.getAddressPk() == null) {
            return insert(ctx, address);

        } else {
            update(ctx, address);
            return address.getAddressPk();
        }
    }

    @Override
    public void delete(DSLContext ctx, SelectConditionStep<Record1<Integer>> addressPkSelect) {
        ctx.delete(ADDRESS)
           .where(ADDRESS.ADDRESS_PK.eq(addressPkSelect))
           .execute();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Integer insert(DSLContext ctx, Address ad) {
        int count = ctx.insertInto(ADDRESS)
                       .set(ADDRESS.STREET_AND_HOUSE_NUMBER, ad.getStreetAndHouseNumber())
                       .set(ADDRESS.ZIP_CODE, ad.getZipCode())
                       .set(ADDRESS.CITY, ad.getCity())
                       .set(ADDRESS.COUNTRY, ad.getCountry())
                       .execute();

        if (count != 1) {
            throw new SteveException("Failed to insert the address");
        }

        return ctx.select(CustomDSL.lastInsertId())
                  .fetchOne()
                  .value1();
    }

    private void update(DSLContext ctx, Address ad) {
        int count = ctx.update(ADDRESS)
                       .set(ADDRESS.STREET_AND_HOUSE_NUMBER, ad.getStreetAndHouseNumber())
                       .set(ADDRESS.ZIP_CODE, ad.getZipCode())
                       .set(ADDRESS.CITY, ad.getCity())
                       .set(ADDRESS.COUNTRY, ad.getCountry())
                       .where(ADDRESS.ADDRESS_PK.eq(ad.getAddressPk()))
                       .execute();

        if (count != 1) {
            throw new SteveException("Failed to update the address");
        }
    }
}
