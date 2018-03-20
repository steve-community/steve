package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import ocpp.cp._2015._10.AuthorizationData;
import ocpp.cs._2015._10.IdTagInfo;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
public interface OcppTagService {

    IdTagInfo getIdTagInfo(String idTag);

    List<AuthorizationData> getAuthDataOfAllTags();

    List<AuthorizationData> getAuthData(List<String> idTagList);

    List<UnidentifiedIncomingObject> getUnknownOcppTags();
}
