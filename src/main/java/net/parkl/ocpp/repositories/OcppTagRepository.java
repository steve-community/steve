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
package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.OcppTag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OcppTagRepository extends CrudRepository<OcppTag, Integer>{

	List<OcppTag> findAllByOrderByOcppTagPkAsc();

	List<OcppTag> findByIdTagInOrderByOcppTagPkAsc(List<String> idTagList);

	OcppTag findByIdTag(String idTag);

	@Query("SELECT t.idTag FROM OcppTag AS t")
	List<String> findIdTagsAll();

	@Query("SELECT t.idTag FROM OcppTag AS t WHERE t.maxActiveTransactionCount>0 AND (t.expiryDate IS NULL OR t.expiryDate>?1)")
	List<String> findIdTagsActive(Date now);
	
	@Query("SELECT t.parentIdTag FROM OcppTag AS t WHERE t.idTag=?1")
	String findParentIdTagByIdTag(String idTag);

	@Query("SELECT DISTINCT t.parentIdTag FROM OcppTag AS t WHERE t.parentIdTag IS NOT NULL")
	List<String> findParentIdTags();

	@Query("SELECT tag FROM OcppTag AS tag")
	List<OcppTag> findTags();
}
