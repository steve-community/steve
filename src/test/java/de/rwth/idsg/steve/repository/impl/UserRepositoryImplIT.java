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
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.web.dto.Address;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
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

    @ParameterizedTest
    @EnumSource(UserQueryForm.OcppTagFilter.class)
    public void getOverviewFiltersUsersByTagPresence(UserQueryForm.OcppTagFilter filter) {
        addOverviewUser("Tagged", "tagged@example.org", List.of("match-one", "other"));
        addOverviewUser("Untagged", "untagged@example.org", List.of());

        var form = new UserQueryForm();
        form.setOcppTagFilter(filter);
        var rows = repository.getOverview(form);

        Set<String> expectedNames = switch (filter) {
            case All -> Set.of("Tagged IT", "Untagged IT");
            case OnlyUsersWithTags -> Set.of("Tagged IT");
            case OnlyUsersWithoutTags -> Set.of("Untagged IT");
        };
        Assertions.assertEquals(expectedNames.size(), rows.size());
        Assertions.assertEquals(expectedNames, rows.stream().map(User.Overview::getName).collect(Collectors.toSet()));
        for (var row : rows) {
            var expectedTags = row.getName().equals("Tagged IT") ? Set.of("match-one", "other") : Set.of();
            Assertions.assertEquals(expectedTags, row.getOcppTagEntries().stream()
                .map(User.OcppTagEntry::getIdTag).collect(Collectors.toSet()));
        }
    }

    @ParameterizedTest
    @EnumSource(UserQueryForm.OcppTagFilter.class)
    public void getOverviewCombinesTagSearchAndPresenceFilter(UserQueryForm.OcppTagFilter filter) {
        addOverviewUser("Tagged", "tagged@example.org", List.of("match-one", "match-two", "other"));
        addOverviewUser("Excluded", "excluded@example.org", List.of("unrelated"));
        addOverviewUser("Untagged", "untagged@example.org", List.of());

        var form = new UserQueryForm();
        form.setOcppTagFilter(filter);
        form.setOcppIdTag("match-");
        var rows = repository.getOverview(form);

        if (filter == UserQueryForm.OcppTagFilter.OnlyUsersWithoutTags) {
            Assertions.assertTrue(rows.isEmpty());
        } else {
            Assertions.assertEquals(1, rows.size());
            Assertions.assertEquals("Tagged IT", rows.getFirst().getName());
            Assertions.assertEquals(Set.of("match-one", "match-two"), rows.getFirst().getOcppTagEntries().stream()
                .map(User.OcppTagEntry::getIdTag).collect(Collectors.toSet()));
        }

        form.setOcppIdTag("missing");
        Assertions.assertTrue(repository.getOverview(form).isEmpty());
    }

    @Test
    public void getOverviewCombinesNameEmailAndUserFilters() {
        addOverviewUser("Selected", "selected@example.org", List.of("selected-tag"));
        addOverviewUser("Excluded", "excluded@example.org", List.of("excluded-tag"));

        var form = new UserQueryForm();
        form.setName("Selected");
        form.setEmail("selected@");
        form.setOcppTagFilter(UserQueryForm.OcppTagFilter.OnlyUsersWithTags);
        var rows = repository.getOverview(form);
        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals("Selected IT", rows.getFirst().getName());
        Assertions.assertEquals(List.of("selected-tag"), rows.getFirst().getOcppTagEntries().stream()
            .map(User.OcppTagEntry::getIdTag).toList());

        form.setUserPk(rows.getFirst().getUserPk());
        Assertions.assertEquals(1, repository.getOverview(form).size());
        form.setEmail("excluded@");
        Assertions.assertTrue(repository.getOverview(form).isEmpty());
    }

    private void addOverviewUser(String name, String email, List<String> tags) {
        for (var tag : tags) {
            dslContext.insertInto(OCPP_TAG).set(OCPP_TAG.ID_TAG, tag).execute();
        }
        var form = userForm();
        form.setFirstName(name);
        form.setEmail(email);
        form.setIdTagList(tags);
        repository.add(form);
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

    @Test
    public void auditTimestamps() {
        Integer pk = dslContext.insertInto(USER)
            .set(USER.FIRST_NAME, "Audit")
            .returning(USER.USER_PK)
            .fetchOne()
            .getUserPk();

        var before = dslContext.select(USER.CREATED_AT, USER.UPDATED_AT)
            .from(USER)
            .where(USER.USER_PK.eq(pk))
            .fetchOne();
        assertAuditTimestampsAreSet(before.value1(), before.value2());

        waitForTimestampTick();

        dslContext.update(USER)
            .set(USER.LAST_NAME, "Updated")
            .where(USER.USER_PK.eq(pk))
            .execute();

        var after = dslContext.select(USER.CREATED_AT, USER.UPDATED_AT)
            .from(USER)
            .where(USER.USER_PK.eq(pk))
            .fetchOne();
        assertAuditTimestampsAfterUpdate(before.value1(), before.value2(), after.value1(), after.value2());
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
