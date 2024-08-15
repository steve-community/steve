/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
import jooq.steve.db.tables.records.WebUserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.springframework.stereotype.Repository;

import static jooq.steve.db.Tables.WEB_USER;
import static org.jooq.impl.DSL.condition;
import static org.jooq.impl.DSL.count;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.08.2024
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class WebUserRepositoryImpl implements WebUserRepository {

    private final DSLContext ctx;

    @Override
    public void createUser(WebUserRecord user) {
        ctx.insertInto(WEB_USER)
            .set(WEB_USER.USERNAME, user.getUsername())
            .set(WEB_USER.PASSWORD, user.getPassword())
            .set(WEB_USER.ENABLED, user.getEnabled())
            .set(WEB_USER.AUTHORITIES, user.getAuthorities())
            .execute();
    }

    @Override
    public void updateUser(WebUserRecord user) {
        ctx.update(WEB_USER)
            .set(WEB_USER.PASSWORD, user.getPassword())
            .set(WEB_USER.ENABLED, user.getEnabled())
            .set(WEB_USER.AUTHORITIES, user.getAuthorities())
            .where(WEB_USER.USERNAME.eq(user.getUsername()))
            .execute();
    }

    @Override
    public void deleteUser(String username) {
        ctx.delete(WEB_USER)
            .where(WEB_USER.USERNAME.eq(username))
            .execute();
    }

    @Override
    public void deleteUser(int webUserPk) {
        ctx.delete(WEB_USER)
            .where(WEB_USER.WEB_USER_PK.eq(webUserPk))
            .execute();
    }

    @Override
    public void changeStatusOfUser(String username, boolean enabled) {
        ctx.update(WEB_USER)
            .set(WEB_USER.ENABLED, enabled)
            .where(WEB_USER.USERNAME.eq(username))
            .execute();
    }

    @Override
    public Integer getUserCountWithAuthority(String authority) {
        JSON authValue = JSON.json("\"" + authority + "\"");
        return ctx.selectCount()
            .from(WEB_USER)
            .where(condition("json_contains({0}, {1})", WEB_USER.AUTHORITIES, authValue))
            .fetchOne(count());
    }

    @Override
    public void changePassword(String username, String newPassword) {
        ctx.update(WEB_USER)
            .set(WEB_USER.PASSWORD, newPassword)
            .where(WEB_USER.USERNAME.eq(username))
            .execute();
    }

    @Override
    public boolean userExists(String username) {
        return ctx.selectOne()
            .from(WEB_USER)
            .where(WEB_USER.USERNAME.eq(username))
            .fetchOptional()
            .isPresent();
    }

    @Override
    public WebUserRecord loadUserByUsername(String username) {
        return ctx.selectFrom(WEB_USER)
            .where(WEB_USER.USERNAME.eq(username))
            .fetchOne();
    }
}
