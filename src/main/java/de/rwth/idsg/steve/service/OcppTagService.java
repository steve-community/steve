package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.service.dto.InvalidOcppTag;
import ocpp.cp._2012._06.AuthorisationData;
import ocpp.cp._2015._10.AuthorizationData;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
public interface OcppTagService {
    ocpp.cs._2010._08.IdTagInfo getIdTagInfoV12(String idTag);
    ocpp.cs._2012._06.IdTagInfo getIdTagInfoV15(String idTag);
    ocpp.cs._2015._10.IdTagInfo getIdTagInfoV16(String idTag);

    /**
     * For OCPP 1.5: Helper method to read ALL idTag details
     * from the DB for the operation SendLocalList.
     *
     */
    //1.5
    List<AuthorisationData> getAuthDataOfAllTags();

    //1.6
    List<AuthorizationData> getAuthDataOfAllTags16();

    /**
     * For OCPP 1.5: Helper method to read details of GIVEN idTags
     * from the DB for the operation SendLocalList.
     *
     */
    //1.5
    List<AuthorisationData> getAuthData(List<String> idTagList);

    //All
    List<InvalidOcppTag> getInvalidOcppTags();

    //1.6
    List<AuthorizationData> getAuthData16(List<String> idTagList);
}
