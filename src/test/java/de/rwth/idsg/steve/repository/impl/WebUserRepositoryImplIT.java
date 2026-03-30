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

import de.rwth.idsg.steve.repository.WebUserRepository;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import jooq.steve.db.tables.records.WebUserRecord;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static jooq.steve.db.Tables.WEB_USER;
import static org.jooq.JSON.json;

public class WebUserRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private WebUserRepository webUserRepository;

    @BeforeEach
    public void setup() {
        new __DatabasePreparer__(dslContext).cleanUp();
    }

    @Test
    public void createUser() {
        String username = uniqueUsername();
        var user = new WebUserRecord()
            .setUsername(username)
            .setPassword("pw")
            .setApiPassword("api-pw")
            .setEnabled(true)
            .setAuthorities(json("[\"ROLE_CREATE\"]"));

        Assertions.assertDoesNotThrow(() -> webUserRepository.createUser(user));

        Integer count = dslContext.selectCount()
            .from(WEB_USER)
            .where(WEB_USER.USERNAME.eq(username))
            .fetchOne(0, int.class);

        Assertions.assertEquals(1, count);
    }

    @Test
    public void updateUser() {
        String username = uniqueUsername();
        dslContext.insertInto(WEB_USER)
            .set(WEB_USER.USERNAME, username)
            .set(WEB_USER.PASSWORD, "old-pw")
            .set(WEB_USER.API_PASSWORD, "old-api")
            .set(WEB_USER.ENABLED, true)
            .set(WEB_USER.AUTHORITIES, json("[\"ROLE_OLD\"]"))
            .execute();

        var user = new WebUserRecord()
            .setUsername(username)
            .setPassword("new-pw")
            .setApiPassword("new-api")
            .setEnabled(false)
            .setAuthorities(json("[\"ROLE_NEW\"]"));

        Assertions.assertDoesNotThrow(() -> webUserRepository.updateUser(user));

        var stored = dslContext.selectFrom(WEB_USER)
            .where(WEB_USER.USERNAME.eq(username))
            .fetchOne();

        Assertions.assertNotNull(stored);
        Assertions.assertEquals("new-pw", stored.getPassword());
        Assertions.assertEquals("new-api", stored.getApiPassword());
        Assertions.assertFalse(stored.getEnabled());
        Assertions.assertEquals("[\"ROLE_NEW\"]", stored.getAuthorities().data());
    }

    @Test
    public void deleteUserByUsername() {
        String username = uniqueUsername();
        dslContext.insertInto(WEB_USER)
            .set(WEB_USER.USERNAME, username)
            .set(WEB_USER.PASSWORD, "pw")
            .set(WEB_USER.ENABLED, true)
            .set(WEB_USER.AUTHORITIES, json("[\"ROLE_DELETE\"]"))
            .execute();

        Assertions.assertDoesNotThrow(() -> webUserRepository.deleteUser(username));

        Integer count = dslContext.selectCount()
            .from(WEB_USER)
            .where(WEB_USER.USERNAME.eq(username))
            .fetchOne(0, int.class);

        Assertions.assertEquals(0, count);
    }

    @Test
    public void deleteUserByPk() {
        String username = uniqueUsername();
        dslContext.insertInto(WEB_USER)
            .set(WEB_USER.USERNAME, username)
            .set(WEB_USER.PASSWORD, "pw")
            .set(WEB_USER.ENABLED, true)
            .set(WEB_USER.AUTHORITIES, json("[\"ROLE_DELETE_PK\"]"))
            .execute();

        Integer pk = dslContext.select(WEB_USER.WEB_USER_PK)
            .from(WEB_USER)
            .where(WEB_USER.USERNAME.eq(username))
            .fetchOne(WEB_USER.WEB_USER_PK);

        Assertions.assertNotNull(pk);
        Assertions.assertDoesNotThrow(() -> webUserRepository.deleteUser(pk));

        Integer count = dslContext.selectCount()
            .from(WEB_USER)
            .where(WEB_USER.WEB_USER_PK.eq(pk))
            .fetchOne(0, int.class);

        Assertions.assertEquals(0, count);
    }

    @Test
    public void changeStatusOfUser() {
        String username = uniqueUsername();
        dslContext.insertInto(WEB_USER)
            .set(WEB_USER.USERNAME, username)
            .set(WEB_USER.PASSWORD, "pw")
            .set(WEB_USER.ENABLED, true)
            .set(WEB_USER.AUTHORITIES, json("[\"ROLE_STATUS\"]"))
            .execute();

        Assertions.assertDoesNotThrow(() -> webUserRepository.changeStatusOfUser(username, false));

        Boolean enabled = dslContext.select(WEB_USER.ENABLED)
            .from(WEB_USER)
            .where(WEB_USER.USERNAME.eq(username))
            .fetchOne(WEB_USER.ENABLED);

        Assertions.assertEquals(Boolean.FALSE, enabled);
    }

    @Test
    public void getUserCountWithAuthority() {
        String user1 = uniqueUsername();
        String user2 = uniqueUsername();
        dslContext.insertInto(WEB_USER)
            .set(WEB_USER.USERNAME, user1)
            .set(WEB_USER.PASSWORD, "pw")
            .set(WEB_USER.ENABLED, true)
            .set(WEB_USER.AUTHORITIES, json("[\"ROLE_REPO_IT_TARGET\",\"ROLE_OTHER\"]"))
            .execute();

        dslContext.insertInto(WEB_USER)
            .set(WEB_USER.USERNAME, user2)
            .set(WEB_USER.PASSWORD, "pw")
            .set(WEB_USER.ENABLED, true)
            .set(WEB_USER.AUTHORITIES, json("[\"ROLE_SOMETHING_ELSE\"]"))
            .execute();

        Integer count = Assertions.assertDoesNotThrow(
            () -> webUserRepository.getUserCountWithAuthority("ROLE_REPO_IT_TARGET")
        );
        Assertions.assertEquals(1, count);
    }

    @Test
    public void changePassword() {
        String username = uniqueUsername();
        dslContext.insertInto(WEB_USER)
            .set(WEB_USER.USERNAME, username)
            .set(WEB_USER.PASSWORD, "old-pw")
            .set(WEB_USER.ENABLED, true)
            .set(WEB_USER.AUTHORITIES, json("[\"ROLE_PASSWORD\"]"))
            .execute();

        Assertions.assertDoesNotThrow(() -> webUserRepository.changePassword(username, "new-pw"));

        String password = dslContext.select(WEB_USER.PASSWORD)
            .from(WEB_USER)
            .where(WEB_USER.USERNAME.eq(username))
            .fetchOne(WEB_USER.PASSWORD);

        Assertions.assertEquals("new-pw", password);
    }

    @Test
    public void userExists() {
        String username = uniqueUsername();
        dslContext.insertInto(WEB_USER)
            .set(WEB_USER.USERNAME, username)
            .set(WEB_USER.PASSWORD, "pw")
            .set(WEB_USER.ENABLED, true)
            .set(WEB_USER.AUTHORITIES, json("[\"ROLE_EXISTS\"]"))
            .execute();

        boolean exists = Assertions.assertDoesNotThrow(() -> webUserRepository.userExists(username));
        Assertions.assertTrue(exists);
    }

    @Test
    public void loadUserByUsername() {
        String username = uniqueUsername();
        dslContext.insertInto(WEB_USER)
            .set(WEB_USER.USERNAME, username)
            .set(WEB_USER.PASSWORD, "pw")
            .set(WEB_USER.API_PASSWORD, "api-pw")
            .set(WEB_USER.ENABLED, true)
            .set(WEB_USER.AUTHORITIES, JSON.json("[\"ROLE_LOAD\"]"))
            .execute();

        WebUserRecord loaded = Assertions.assertDoesNotThrow(() -> webUserRepository.loadUserByUsername(username));

        Assertions.assertNotNull(loaded);
        Assertions.assertEquals(username, loaded.getUsername());
        Assertions.assertEquals("pw", loaded.getPassword());
        Assertions.assertEquals("api-pw", loaded.getApiPassword());
        Assertions.assertEquals("[\"ROLE_LOAD\"]", loaded.getAuthorities().data());
    }

    private static String uniqueUsername() {
        return "repo_it_" + UUID.randomUUID().toString().replace("-", "");
    }
}
