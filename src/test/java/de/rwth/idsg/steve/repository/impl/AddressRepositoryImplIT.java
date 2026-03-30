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
import org.jooq.DSLContext;
import org.jooq.SelectConditionStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static jooq.steve.db.tables.Address.ADDRESS;

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
        assertNoDatabaseException(() -> repository.get(dslContext, null));
    }

    @Test
    public void updateOrInsert() {
        assertNoDatabaseException(() -> repository.updateOrInsert(dslContext, new Address()));
    }

    @Test
    public void delete() {
        SelectConditionStep<?> select = dslContext.select(ADDRESS.ADDRESS_PK).from(ADDRESS).where(ADDRESS.ADDRESS_PK.ge(0));
        assertNoDatabaseException(() -> repository.delete(dslContext, (SelectConditionStep) select));
    }
}
