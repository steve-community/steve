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
package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.OcppTag;
import de.rwth.idsg.steve.repository.dto.OcppTagActivity;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;

import java.util.List;
import java.util.Optional;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.08.2014
 */
public interface OcppTagRepository {
    List<OcppTag.OcppTagOverview> getOverview(OcppTagQueryForm form);

    List<OcppTagActivity> getRecords();

    List<OcppTagActivity> getRecords(List<String> idTagList);

    Optional<OcppTagActivity> getRecord(String idTag);

    Optional<OcppTagActivity> getRecord(int ocppTagPk);

    List<String> getIdTags();

    List<String> getIdTagsWithoutUser();

    List<String> getActiveIdTags();

    List<String> getParentIdTags();

    String getParentIdTag(String idTag);

    void addOcppTagList(List<String> idTagList);

    int addOcppTag(OcppTagForm form);

    void updateOcppTag(OcppTagForm form);

    void deleteOcppTag(int ocppTagPk);
}
