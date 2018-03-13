package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.service.dto.InvalidOcppTag;
import ocpp.cp._2012._06.AuthorisationData;
import ocpp.cs._2015._10.IdTagInfo;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
public interface OcppTagService {

    IdTagInfo getIdTagInfo(String idTag);

    /**
     * For OCPP 1.5: Helper method to read ALL idTag details
     * from the DB for the operation SendLocalList.
     *
     */
    List<AuthorisationData> getAuthDataOfAllTags();

    /**
     * For OCPP 1.5: Helper method to read details of GIVEN idTags
     * from the DB for the operation SendLocalList.
     *
     */
    List<AuthorisationData> getAuthData(List<String> idTagList);

    List<InvalidOcppTag> getInvalidOcppTags();
}
