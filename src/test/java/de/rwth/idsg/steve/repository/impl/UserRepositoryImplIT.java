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

import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.web.dto.Address;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static jooq.steve.db.tables.User.USER;
import static org.jooq.impl.DSL.max;

/**
 * Created with assistance from GPT-5.3-Codex
 */
public class UserRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private UserRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void getOverview() {
        var rows = assertNoDatabaseException(() -> repository.getOverview(new UserQueryForm()));
        Assertions.assertNotNull(rows);
    }

    @Test
    public void getDetails() {
        repository.add(userForm());
        Integer pk = dslContext.select(max(USER.USER_PK)).from(USER).fetchOne(0, int.class);

        var details = assertNoDatabaseException(() -> repository.getDetails(pk));
        Assertions.assertNotNull(details);
        Assertions.assertEquals("Repo", details.getUserRecord().getFirstName());
    }

    @Test
    public void add() {
        repository.add(userForm());
        Integer count = dslContext.selectCount()
            .from(USER)
            .where(USER.FIRST_NAME.eq("Repo"))
            .and(USER.LAST_NAME.eq("IT"))
            .fetchOne(0, int.class);
        Assertions.assertTrue(count >= 1);
    }

    @Test
    public void update() {
        repository.add(userForm());
        Integer pk = dslContext.select(max(USER.USER_PK)).from(USER).fetchOne(0, int.class);

        var form = userForm();
        form.setUserPk(pk);
        form.setFirstName("Updated");
        repository.update(form);

        String firstName = dslContext.select(USER.FIRST_NAME)
            .from(USER)
            .where(USER.USER_PK.eq(pk))
            .fetchOne(USER.FIRST_NAME);
        Assertions.assertEquals("Updated", firstName);
    }

    @Test
    public void delete() {
        repository.add(userForm());
        Integer pk = dslContext.select(max(USER.USER_PK)).from(USER).fetchOne(0, int.class);

        repository.delete(pk);

        Integer count = dslContext.selectCount()
            .from(USER)
            .where(USER.USER_PK.eq(pk))
            .fetchOne(0, int.class);
        Assertions.assertEquals(0, count);
    }

    private static UserForm userForm() {
        var form = new UserForm();
        form.setFirstName("Repo");
        form.setLastName("IT");
        form.setAddress(new Address());
        form.setIdTagList(java.util.List.of(KNOWN_OCPP_TAG));
        return form;
    }
}
