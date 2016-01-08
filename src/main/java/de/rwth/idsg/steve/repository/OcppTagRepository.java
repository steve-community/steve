package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.OcppTag;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import jooq.steve.db.tables.records.OcppTagRecord;
import org.jooq.Result;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface OcppTagRepository {
    List<OcppTag.Overview> getOverview(OcppTagQueryForm form);

    Result<OcppTagRecord> getRecords();
    Result<OcppTagRecord> getRecords(List<String> idTagList);
    OcppTagRecord getRecord(String idTag);
    OcppTagRecord getRecord(int ocppTagPk);

    List<String> getIdTags();
    List<String> getActiveIdTags();

    List<String> getParentIdTags();
    String getParentIdtag(String idTag);

    int addOcppTag(OcppTagForm form);
    void updateOcppTag(OcppTagForm form);
    void deleteOcppTag(int ocppTagPk);
}
