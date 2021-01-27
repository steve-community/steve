package net.parkl.ocpp.service.cs;

import java.util.List;

import de.rwth.idsg.steve.repository.dto.OcppTag.Overview;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import net.parkl.ocpp.entities.OcppTag;

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

}