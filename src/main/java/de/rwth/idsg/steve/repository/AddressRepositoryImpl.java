package de.rwth.idsg.steve.repository;

import com.google.common.base.Optional;
import de.rwth.idsg.steve.SteveException;
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
    public Integer insert(DSLContext ctx, Address ad) {
        AddressRecord result =
                ctx.insertInto(ADDRESS)
                   .set(ADDRESS.STREET_AND_HOUSE_NUMBER, ad.getStreetAndHouseNumber())
                   .set(ADDRESS.ZIP_CODE, ad.getZipCode())
                   .set(ADDRESS.CITY, ad.getCity())
                   .set(ADDRESS.COUNTRY, ad.getCountry())
                   .returning(ADDRESS.ADDRESS_ID)
                   .fetchOne();

        if (result == null || result.getAddressId() == null) {
            throw new SteveException("Failed to insert the address");
        } else {
            return result.getAddressId();
        }
    }

    /**
     * When the entity (charge point or user) did not have an address yet (addressId was null), UPDATE will fail.
     * We inform the call site with an Optional, so that it can try an INSERT.
     */
    @Override
    public Optional<Integer> update(DSLContext ctx, SelectConditionStep<Record1<Integer>> addressIdSelect, Address ad) {
        AddressRecord result =
                ctx.update(ADDRESS)
                   .set(ADDRESS.STREET_AND_HOUSE_NUMBER, ad.getStreetAndHouseNumber())
                   .set(ADDRESS.ZIP_CODE, ad.getZipCode())
                   .set(ADDRESS.CITY, ad.getCity())
                   .set(ADDRESS.COUNTRY, ad.getCountry())
                   .where(ADDRESS.ADDRESS_ID.eq(addressIdSelect))
                   .returning(ADDRESS.ADDRESS_ID)
                   .fetchOne();

        if (result == null || result.getAddressId() == null) {
            return Optional.absent();
        } else {
            return Optional.of(result.getAddressId());
        }
    }

    @Override
    public void delete(DSLContext ctx, SelectConditionStep<Record1<Integer>> addressIdSelect) {
        ctx.delete(ADDRESS)
           .where(ADDRESS.ADDRESS_ID.eq(addressIdSelect))
           .execute();
    }
}
