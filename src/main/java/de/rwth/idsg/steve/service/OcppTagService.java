package de.rwth.idsg.steve.service;

import ocpp.cp._2012._06.AuthorisationData;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
public interface OcppTagService {
    ocpp.cs._2010._08.IdTagInfo getIdTagInfoV12(String idTag);
    ocpp.cs._2012._06.IdTagInfo getIdTagInfoV15(String idTag);

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
}
