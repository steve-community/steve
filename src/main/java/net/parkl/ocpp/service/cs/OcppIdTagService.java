/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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
package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.OcppTag.Overview;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import net.parkl.ocpp.entities.OcppTag;

import java.util.List;
import java.util.Map;

public interface OcppIdTagService {

	String getParentIdtag(String idTag);

	List<OcppTag> getRecords();

	List<OcppTag> getRecords(List<String> idTagList);

	OcppTag getRecord(String idTag);

	List<String> getActiveIdTags();

	List<String> getIdTags();

	OcppTag getRecord(int ocppTagPk);

	void addOcppTag(OcppTagForm ocppTagForm);

	void addOcppTagList(List<String> idList);

	List<String> getParentIdTags();

	List<Overview> getOverview(OcppTagQueryForm params);

	void deleteOcppTag(int ocppTagPk);

	void updateOcppTag(OcppTagForm ocppTagForm);

	void addRfidTagIfNotExists(String idTag);

	void createTagWithoutActiveTransactionIfNotExists(String idTag);

	List<OcppTag> findTags();

	Map<String, OcppTag> getRfidTagOcppTagMap();
}
