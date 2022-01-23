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
import de.rwth.idsg.steve.repository.AddressRepository;
import de.rwth.idsg.steve.web.dto.Address;
import jooq.steve.db.tables.records.AddressRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;

import org.jetbrains.annotations.Nullable;

import static jooq.steve.db.tables.Address.ADDRESS;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 24.11.2015
 */
@Slf4j
@Repository
public class AddressRepositoryImpl implements AddressRepository {

    @Override
    @Nullable
    public AddressRecord get(DSLContext ctx, Integer addressPk) {
        if (addressPk == null) {
            return null;
        }

        return ctx.selectFrom(ADDRESS)
                  .where(ADDRESS.ADDRESS_PK.equal(addressPk))
                  .fetchOne();
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
    @Nullable
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
        try {
            return ctx.insertInto(ADDRESS)
                      .set(ADDRESS.STREET, ad.getStreet())
                      .set(ADDRESS.HOUSE_NUMBER, ad.getHouseNumber())
                      .set(ADDRESS.ZIP_CODE, ad.getZipCode())
                      .set(ADDRESS.CITY, ad.getCity())
                      .set(ADDRESS.COUNTRY, ad.getCountryAlpha2OrNull())
                      .returning(ADDRESS.ADDRESS_PK)
                      .fetchOne()
                      .getAddressPk();
        } catch (DataAccessException e) {
            throw new SteveException("Failed to insert the address");
        }
    }

    private void update(DSLContext ctx, Address ad) {
        int count = ctx.update(ADDRESS)
                       .set(ADDRESS.STREET, ad.getStreet())
                       .set(ADDRESS.HOUSE_NUMBER, ad.getHouseNumber())
                       .set(ADDRESS.ZIP_CODE, ad.getZipCode())
                       .set(ADDRESS.CITY, ad.getCity())
                       .set(ADDRESS.COUNTRY, ad.getCountryAlpha2OrNull())
                       .where(ADDRESS.ADDRESS_PK.eq(ad.getAddressPk()))
                       .execute();

        if (count != 1) {
            throw new SteveException("Failed to update the address");
        }
    }
}
