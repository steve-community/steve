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

}
