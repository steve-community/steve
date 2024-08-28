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
import de.rwth.idsg.steve.web.dto.WebUserQueryForm;
import jooq.steve.db.tables.records.WebUserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.springframework.stereotype.Repository;

import static jooq.steve.db.Tables.WEB_USER;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SelectQuery;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            .set(WEB_USER.API_PASSWORD, user.getApiPassword())
            .set(WEB_USER.ENABLED, user.getEnabled())
            .set(WEB_USER.AUTHORITIES, user.getAuthorities())
            .execute();
    }

    @Override
    public void updateUser(WebUserRecord user) {
        ctx.update(WEB_USER)
            .set(WEB_USER.PASSWORD, user.getPassword())
            .set(WEB_USER.API_PASSWORD, user.getApiPassword())
            .set(WEB_USER.ENABLED, user.getEnabled())
            .set(WEB_USER.AUTHORITIES, user.getAuthorities())
            .where(WEB_USER.USERNAME.eq(user.getUsername()))
            .execute();
    }

    @Override
    public void updateUserByPk(WebUserRecord user) {
        // To change the password use one of the changePassword methods
        ctx.update(WEB_USER)
            .set(WEB_USER.USERNAME, user.getUsername())
            .set(WEB_USER.ENABLED, user.getEnabled())
            .set(WEB_USER.AUTHORITIES, user.getAuthorities())
            .where(WEB_USER.WEB_USER_PK.eq(user.getWebUserPk()))
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
        return ctx.selectCount()
            .from(WEB_USER)
            .where(conditionsForAuthorities(Collections.singletonList(authority)))
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
    public void changePassword(Integer userPk, String newPassword) {
        ctx.update(WEB_USER)
            .set(WEB_USER.PASSWORD, newPassword)
            .where(WEB_USER.WEB_USER_PK.eq(userPk))
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

    @Override
    public WebUserRecord loadUserByUsePk(Integer webUserPk) {
        return ctx.selectFrom(WEB_USER)
            .where(WEB_USER.WEB_USER_PK.eq(webUserPk))
            .fetchOne();
    }

    @Override
    public Result<Record4<Integer, String, Boolean, JSON>> getOverview(WebUserQueryForm form) {
        SelectQuery selectQuery = ctx.selectQuery();
        selectQuery.addFrom(WEB_USER);
        selectQuery.addSelect(
                WEB_USER.WEB_USER_PK,
                WEB_USER.USERNAME,
                WEB_USER.ENABLED,
                WEB_USER.AUTHORITIES
        );

        if (form.isSetWebUsername()) {
            selectQuery.addConditions(WEB_USER.USERNAME.eq(form.getWebUsername()));
        }

        if (form.isSetEnabled()) {
            selectQuery.addConditions(WEB_USER.ENABLED.eq(form.getEnabled()));
        }

        if (form.isSetRoles()) {
            String[] split = form.getRoles().split(","); //Semicolon seperated String to StringArray
            List<String> roles = Arrays.stream(split).map(String::strip).toList();
            selectQuery.addConditions(conditionsForAuthorities(roles));
        }

        return selectQuery.fetch();
    }

    private static List<Condition> conditionsForAuthorities(List<String> authorities) {
        return authorities.stream()
            .map(it -> JSON.json("\"" + it + "\""))
            .map(it -> condition("json_contains({0}, {1})", WEB_USER.AUTHORITIES, it))
            .toList();
    }
}
