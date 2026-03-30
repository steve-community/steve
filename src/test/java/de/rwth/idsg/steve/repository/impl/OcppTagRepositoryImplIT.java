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

import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import jooq.steve.db.tables.records.OcppTagRecord;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static jooq.steve.db.tables.OcppTag.OCPP_TAG;

/**
 * Created with assistance from GPT-5.3-Codex
 */
public class OcppTagRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private OcppTagRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void getOverview() {
        var rows = assertNoDatabaseException(() -> repository.getOverview(new OcppTagQueryForm()));
        Assertions.assertNotNull(rows);
    }

    @Test
    public void getRecords() {
        var rows = assertNoDatabaseException(() -> repository.getRecords());
        Assertions.assertNotNull(rows);
    }

    @Test
    public void getRecordsByIdTagList() {
        var rows = assertNoDatabaseException(() -> repository.getRecords(java.util.List.of(KNOWN_OCPP_TAG)));
        Assertions.assertNotNull(rows);
    }

    @Test
    public void getRecordByIdTag() {
        var row = assertNoDatabaseException(() -> repository.getRecord(KNOWN_OCPP_TAG));
        Assertions.assertNotNull(row);
        Assertions.assertEquals(KNOWN_OCPP_TAG, row.getIdTag());
    }

    @Test
    public void getRecordByPk() {
        Integer pk = dslContext.select(OCPP_TAG.OCPP_TAG_PK)
            .from(OCPP_TAG)
            .where(OCPP_TAG.ID_TAG.eq(KNOWN_OCPP_TAG))
            .fetchOne(OCPP_TAG.OCPP_TAG_PK);
        Assertions.assertNotNull(pk);

        var row = assertNoDatabaseException(() -> repository.getRecord(pk));
        Assertions.assertNotNull(row);
        Assertions.assertEquals(KNOWN_OCPP_TAG, row.getIdTag());
    }

    @Test
    public void getIdTags() {
        var tags = assertNoDatabaseException(() -> repository.getIdTags());
        Assertions.assertTrue(tags.contains(KNOWN_OCPP_TAG));
    }

    @Test
    public void getIdTagsByIdTagList() {
        var tags = assertNoDatabaseException(() -> repository.getIdTags(java.util.List.of(KNOWN_OCPP_TAG)));
        Assertions.assertTrue(tags.contains(KNOWN_OCPP_TAG));
    }

    @Test
    public void getIdTagsWithoutUser() {
        var tags = assertNoDatabaseException(repository::getIdTagsWithoutUser);
        Assertions.assertNotNull(tags);
    }

    @Test
    public void getActiveIdTags() {
        var tags = assertNoDatabaseException(repository::getActiveIdTags);
        Assertions.assertNotNull(tags);
    }

    @Test
    public void getParentIdTags() {
        var tags = assertNoDatabaseException(repository::getParentIdTags);
        Assertions.assertNotNull(tags);
    }

    @Test
    public void getParentIdtag() {
        assertNoDatabaseException(() -> repository.getParentIdtag(KNOWN_OCPP_TAG));
    }

    @Test
    public void addOcppTagList() {
        String idTag = uniqueId("tag");
        assertNoDatabaseException(() -> repository.addOcppTagList(java.util.List.of(idTag)));

        Integer count = dslContext.selectCount()
            .from(OCPP_TAG)
            .where(OCPP_TAG.ID_TAG.eq(idTag))
            .fetchOne(0, int.class);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void addOcppTag() {
        var form = new OcppTagForm();
        form.setIdTag(uniqueId("tag"));
        Integer pk = assertNoDatabaseException(() -> repository.addOcppTag(form));
        Assertions.assertNotNull(pk);
    }

    @Test
    public void updateOcppTag() {
        String initial = uniqueId("tag");
        Integer pk = dslContext.insertInto(OCPP_TAG)
            .set(OCPP_TAG.ID_TAG, initial)
            .returning(OCPP_TAG.OCPP_TAG_PK)
            .fetchOne()
            .getOcppTagPk();

        var form = new OcppTagForm();
        form.setOcppTagPk(pk);
        form.setNote("new note");
        assertNoDatabaseException(() -> repository.updateOcppTag(form));

        OcppTagRecord updated = dslContext.selectFrom(OCPP_TAG)
            .where(OCPP_TAG.OCPP_TAG_PK.eq(pk))
            .fetchOne();
        Assertions.assertEquals(form.getNote(), updated.getNote());
    }

    @Test
    public void deleteOcppTag() {
        String idTag = uniqueId("tag");
        Integer pk = dslContext.insertInto(OCPP_TAG)
            .set(OCPP_TAG.ID_TAG, idTag)
            .returning(OCPP_TAG.OCPP_TAG_PK)
            .fetchOne()
            .getOcppTagPk();

        assertNoDatabaseException(() -> repository.deleteOcppTag(pk));

        Integer count = dslContext.selectCount()
            .from(OCPP_TAG)
            .where(OCPP_TAG.OCPP_TAG_PK.eq(pk))
            .fetchOne(0, int.class);
        Assertions.assertEquals(0, count);
    }
}
