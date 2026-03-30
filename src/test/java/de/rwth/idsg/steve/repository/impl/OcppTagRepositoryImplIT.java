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
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        assertNoDatabaseException(() -> repository.getOverview(new OcppTagQueryForm()));
    }

    @Test
    public void getRecords() {
        assertNoDatabaseException(repository::getRecords);
    }

    @Test
    public void getRecordsByIdTagList() {
        assertNoDatabaseException(() -> repository.getRecords(java.util.List.of(KNOWN_OCPP_TAG)));
    }

    @Test
    public void getRecordByIdTag() {
        assertNoDatabaseException(() -> repository.getRecord(KNOWN_OCPP_TAG));
    }

    @Test
    public void getRecordByPk() {
        assertNoDatabaseException(() -> repository.getRecord(1));
    }

    @Test
    public void getIdTags() {
        assertNoDatabaseException(repository::getIdTags);
    }

    @Test
    public void getIdTagsByIdTagList() {
        assertNoDatabaseException(() -> repository.getIdTags(java.util.List.of(KNOWN_OCPP_TAG)));
    }

    @Test
    public void getIdTagsWithoutUser() {
        assertNoDatabaseException(repository::getIdTagsWithoutUser);
    }

    @Test
    public void getActiveIdTags() {
        assertNoDatabaseException(repository::getActiveIdTags);
    }

    @Test
    public void getParentIdTags() {
        assertNoDatabaseException(repository::getParentIdTags);
    }

    @Test
    public void getParentIdtag() {
        assertNoDatabaseException(() -> repository.getParentIdtag(KNOWN_OCPP_TAG));
    }

    @Test
    public void addOcppTagList() {
        assertNoDatabaseException(() -> repository.addOcppTagList(java.util.List.of(uniqueId("tag"))));
    }

    @Test
    public void addOcppTag() {
        var form = new OcppTagForm();
        form.setIdTag(uniqueId("tag"));
        assertNoDatabaseException(() -> repository.addOcppTag(form));
    }

    @Test
    public void updateOcppTag() {
        var form = new OcppTagForm();
        form.setOcppTagPk(1);
        form.setIdTag(KNOWN_OCPP_TAG);
        assertNoDatabaseException(() -> repository.updateOcppTag(form));
    }

    @Test
    public void deleteOcppTag() {
        assertNoDatabaseException(() -> repository.deleteOcppTag(1));
    }
}

