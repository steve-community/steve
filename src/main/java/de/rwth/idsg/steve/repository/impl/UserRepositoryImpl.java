/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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

import com.google.common.base.Strings;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.AddressRepository;
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import jooq.steve.db.tables.records.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.rwth.idsg.steve.utils.CustomDSL.includes;
import static jooq.steve.db.Tables.USER_OCPP_TAG;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.User.USER;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 25.11.2015
 */
@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired private DSLContext ctx;
    @Autowired private AddressRepository addressRepository;

    @Override
    public List<User.Overview> getOverview(UserQueryForm form) {
        var ocppTagsPerUser = getOcppTagsInternal(form.getUserPk(), form.getOcppIdTag());
        var userResults = getOverviewInternal(form);

        List<User.Overview> userOverviews = new ArrayList<>();
        for (var r : userResults) {
            var tags = ocppTagsPerUser.getOrDefault(r.value1(), List.of());

            var user = User.Overview.builder()
                .userPk(r.value1())
                .name(r.value2() + " " + r.value3())
                .phone(r.value4())
                .email(r.value5())
                .ocppTagEntries(tags)
                .build();

            // TODO: Improve later. This is not efficient, because we filter after fetching all results. However, this
            //       should be acceptable since the number of users (and tags) are usually not very high, and this
            //       overview query will probably not be in the hot path.
            switch (form.getOcppTagFilter()) {
                case OnlyUsersWithTags -> {
                    if (!tags.isEmpty()) {
                        userOverviews.add(user);
                    }
                }
                case OnlyUsersWithoutTags -> {
                    if (tags.isEmpty()) {
                        userOverviews.add(user);
                    }
                }
                default -> userOverviews.add(user);
            }
        }

        return userOverviews;
    }

    @Override
    public User.Details getDetails(int userPk) {
        UserRecord ur = ctx.selectFrom(USER)
                           .where(USER.USER_PK.equal(userPk))
                           .fetchOne();

        if (ur == null) {
            throw new SteveException("There is no user with id '%s'", userPk);
        }

        return User.Details.builder()
                           .userRecord(ur)
                           .address(addressRepository.get(ctx, ur.getAddressPk()))
                           .ocppTagEntries(getOcppTagsInternal(userPk, null).getOrDefault(userPk, List.of()))
                           .build();
    }

    @Override
    public void add(UserForm form) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                Integer addressId = addressRepository.updateOrInsert(ctx, form.getAddress());
                Integer userPk = addInternal(ctx, form, addressId);
                refreshOcppTagsInternal(ctx, form, userPk);

            } catch (DataAccessException e) {
                throw new SteveException("Failed to add the user", e);
            }
        });
    }

    @Override
    public void update(UserForm form) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                Integer addressId = addressRepository.updateOrInsert(ctx, form.getAddress());
                updateInternal(ctx, form, addressId);
                refreshOcppTagsInternal(ctx, form, form.getUserPk());

            } catch (DataAccessException e) {
                throw new SteveException("Failed to update the user", e);
            }
        });
    }

    @Override
    public void delete(int userPk) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                addressRepository.delete(ctx, selectAddressId(userPk));
                deleteInternal(ctx, userPk);

            } catch (DataAccessException e) {
                throw new SteveException("Failed to delete the user", e);
            }
        });
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Result<Record5<Integer, String, String, String, String>> getOverviewInternal(UserQueryForm form) {
        List<Condition> conditions = new ArrayList<>();

        if (form.isSetUserPk()) {
            conditions.add(USER.USER_PK.eq(form.getUserPk()));
        }

        if (form.isSetEmail()) {
            conditions.add(includes(USER.E_MAIL, form.getEmail()));
        }

        if (form.isSetOcppIdTag()) {
            conditions.add(DSL.exists(
                DSL.selectOne()
                    .from(USER_OCPP_TAG)
                    .join(OCPP_TAG).on(USER_OCPP_TAG.OCPP_TAG_PK.eq(OCPP_TAG.OCPP_TAG_PK))
                    .where(USER_OCPP_TAG.USER_PK.eq(USER.USER_PK))
                    .and(includes(OCPP_TAG.ID_TAG, form.getOcppIdTag()))
            ));
        }

        if (form.isSetName()) {
            // Concatenate the two columns and search within the resulting representation
            // for flexibility, since the user can search by first or last name, or both.
            Field<String> joinedField = DSL.concat(USER.FIRST_NAME, USER.LAST_NAME);

            // Find a matching sequence anywhere within the concatenated representation
            conditions.add(includes(joinedField, form.getName()));
        }

        return ctx.select(
                USER.USER_PK,
                USER.FIRST_NAME,
                USER.LAST_NAME,
                USER.PHONE,
                USER.E_MAIL)
            .from(USER)
            .where(conditions)
            .fetch();
    }

    private Map<Integer, List<User.OcppTagEntry>> getOcppTagsInternal(Integer userPk, String ocppIdTag) {
        List<Condition> conditions = new ArrayList<>();

        if (userPk != null) {
            conditions.add(USER_OCPP_TAG.USER_PK.eq(userPk));
        }

        if (!Strings.isNullOrEmpty(ocppIdTag)) {
            conditions.add(includes(OCPP_TAG.ID_TAG, ocppIdTag));
        }

        var results = ctx.select(
                USER_OCPP_TAG.USER_PK,
                OCPP_TAG.OCPP_TAG_PK,
                OCPP_TAG.ID_TAG)
            .from(USER_OCPP_TAG)
            .join(OCPP_TAG).on(USER_OCPP_TAG.OCPP_TAG_PK.eq(OCPP_TAG.OCPP_TAG_PK))
            .where(conditions)
            .fetch();

        Map<Integer, List<User.OcppTagEntry>> map = new HashMap<>();
        for (var entry : results) {
            map.computeIfAbsent(entry.value1(), k -> new ArrayList<>())
                .add(new User.OcppTagEntry(entry.value2(), entry.value3()));
        }
        return map;
    }

    private SelectConditionStep<Record1<Integer>> selectAddressId(int userPk) {
        return ctx.select(USER.ADDRESS_PK)
                  .from(USER)
                  .where(USER.USER_PK.eq(userPk));
    }

    private SelectConditionStep<Record1<Integer>> selectOcppTagPk(String ocppIdTag) {
        return ctx.select(OCPP_TAG.OCPP_TAG_PK)
                  .from(OCPP_TAG)
                  .where(OCPP_TAG.ID_TAG.eq(ocppIdTag));
    }

    private Integer addInternal(DSLContext ctx, UserForm form, Integer addressPk) {
        try {
            return ctx.insertInto(USER)
                      .set(USER.FIRST_NAME, form.getFirstName())
                      .set(USER.LAST_NAME, form.getLastName())
                      .set(USER.BIRTH_DAY, form.getBirthDay())
                      .set(USER.SEX, form.getSex().getDatabaseValue())
                      .set(USER.PHONE, form.getPhone())
                      .set(USER.E_MAIL, form.getEMail())
                      .set(USER.NOTE, form.getNote())
                      .set(USER.ADDRESS_PK, addressPk)
                      .returning(USER.USER_PK)
                      .fetchOne()
                      .getUserPk();
        } catch (DataAccessException e) {
            throw new SteveException("Failed to insert the user", e);
        }
    }

    private void updateInternal(DSLContext ctx, UserForm form, Integer addressPk) {
        ctx.update(USER)
           .set(USER.FIRST_NAME, form.getFirstName())
           .set(USER.LAST_NAME, form.getLastName())
           .set(USER.BIRTH_DAY, form.getBirthDay())
           .set(USER.SEX, form.getSex().getDatabaseValue())
           .set(USER.PHONE, form.getPhone())
           .set(USER.E_MAIL, form.getEMail())
           .set(USER.NOTE, form.getNote())
           .set(USER.ADDRESS_PK, addressPk)
           .where(USER.USER_PK.eq(form.getUserPk()))
           .execute();
    }

    private void deleteInternal(DSLContext ctx, int userPk) {
        ctx.delete(USER)
           .where(USER.USER_PK.equal(userPk))
           .execute();
    }

    /**
     * Refresh the full the OCPP tag associations for a user:
     * - 1. Delete existing OCPP tags that are not in the form.
     * - 2. Insert new OCPP tags from the form that do not already exist for the user.
     */
    private void refreshOcppTagsInternal(DSLContext ctx, UserForm form, Integer userPk) {
        List<Integer> wantedOcppTagPks = CollectionUtils.isEmpty(form.getIdTagList())
            ? List.of() // This user wants no OCPP tags
            : ctx.select(OCPP_TAG.OCPP_TAG_PK)
            .from(OCPP_TAG)
            .where(OCPP_TAG.ID_TAG.in(form.getIdTagList()))
            .fetch(OCPP_TAG.OCPP_TAG_PK);

        // Optimization: Execute the delete query only if we are processing an existing user.
        // A new user will not have any existing OCPP tags, so no delete is needed.
        //
        // 1. Delete entries that are not in the wanted entries
        if (form.getUserPk() != null) {
            ctx.deleteFrom(USER_OCPP_TAG)
                .where(USER_OCPP_TAG.USER_PK.eq(userPk))
                .and(USER_OCPP_TAG.OCPP_TAG_PK.notIn(wantedOcppTagPks))
                .execute();
        }

        // 2. Insert new entries that are not already present
        if (!wantedOcppTagPks.isEmpty()) {
            ctx.insertInto(USER_OCPP_TAG, USER_OCPP_TAG.USER_PK, USER_OCPP_TAG.OCPP_TAG_PK)
                .valuesOfRows(wantedOcppTagPks.stream().map(pk -> DSL.row(userPk, pk)).toList())
                .onDuplicateKeyIgnore() // Ignore if already exists
                .execute();
        }
    }
}
