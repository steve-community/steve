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

import de.rwth.idsg.steve.repository.AddressRepository;
import de.rwth.idsg.steve.web.dto.Address;
import jooq.steve.db.tables.records.AddressRecord;
import org.jooq.DSLContext;
import org.jooq.SelectConditionStep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static jooq.steve.db.tables.Address.ADDRESS;

/**
 * Created with assistance from GPT-5.3-Codex
 */
public class AddressRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private AddressRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void get() {
        Integer pk = dslContext.insertInto(ADDRESS)
            .set(ADDRESS.CITY, "Aachen")
            .returning(ADDRESS.ADDRESS_PK)
            .fetchOne()
            .getAddressPk();

        AddressRecord record = assertNoDatabaseException(() -> repository.get(dslContext, pk));
        Assertions.assertNotNull(record);
        Assertions.assertEquals("Aachen", record.getCity());
    }

    @Test
    public void updateOrInsert() {
        var address = new Address();
        address.setCity("Cologne");
        address.setStreet("Main");

        Integer pk = assertNoDatabaseException(() -> repository.updateOrInsert(dslContext, address));
        Assertions.assertNotNull(pk);

        String city = dslContext.select(ADDRESS.CITY)
            .from(ADDRESS)
            .where(ADDRESS.ADDRESS_PK.eq(pk))
            .fetchOne(ADDRESS.CITY);
        Assertions.assertEquals("Cologne", city);
    }

    @Test
    public void delete() {
        Integer pk = dslContext.insertInto(ADDRESS)
            .set(ADDRESS.CITY, "Berlin")
            .returning(ADDRESS.ADDRESS_PK)
            .fetchOne()
            .getAddressPk();

        SelectConditionStep<?> select = dslContext.select(ADDRESS.ADDRESS_PK).from(ADDRESS).where(ADDRESS.ADDRESS_PK.ge(0));
        assertNoDatabaseException(() -> repository.delete(dslContext, (SelectConditionStep) select));

        Integer count = dslContext.selectCount()
            .from(ADDRESS)
            .where(ADDRESS.ADDRESS_PK.eq(pk))
            .fetchOne(0, int.class);
        Assertions.assertEquals(0, count);
    }
}
