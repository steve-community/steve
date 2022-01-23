/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.Ocpp15AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.service.OcppTagService;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListParams;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListUpdateType;
import ocpp.cp._2015._10.AuthorizationData;

import javax.xml.ws.AsyncHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class SendLocalListTask extends Ocpp15AndAboveTask<SendLocalListParams, String> {

    private final ocpp.cp._2015._10.SendLocalListRequest request;

    public SendLocalListTask(OcppVersion ocppVersion, SendLocalListParams params, OcppTagService ocppTagService) {
        super(ocppVersion, params);
        this.request = createOcpp16Request(ocppTagService);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ocpp.cp._2012._06.SendLocalListRequest getOcpp15Request() {
        ocpp.cp._2015._10.SendLocalListRequest ocpp16Request = getOcpp16Request();

        return new ocpp.cp._2012._06.SendLocalListRequest()
                .withListVersion(ocpp16Request.getListVersion())
                .withUpdateType(ocpp.cp._2012._06.UpdateType.fromValue(ocpp16Request.getUpdateType().value()))
                .withLocalAuthorisationList(toOcpp15(ocpp16Request.getLocalAuthorizationList()));
    }

    @Override
    public ocpp.cp._2015._10.SendLocalListRequest getOcpp16Request() {
        return request;
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.SendLocalListResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.SendLocalListResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private ocpp.cp._2015._10.SendLocalListRequest createOcpp16Request(OcppTagService ocppTagService) {
        // DIFFERENTIAL update
        if (params.getUpdateType() == SendLocalListUpdateType.DIFFERENTIAL) {
            List<ocpp.cp._2015._10.AuthorizationData> auths = new ArrayList<>();

            // Step 1: For the idTags to be deleted, insert only the idTag
            for (String idTag : params.getDeleteList()) {
                auths.add(new ocpp.cp._2015._10.AuthorizationData().withIdTag(idTag));
            }

            // Step 2: For the idTags to be added or updated, insert them with their IdTagInfos
            auths.addAll(ocppTagService.getAuthData(params.getAddUpdateList()));

            return new ocpp.cp._2015._10.SendLocalListRequest()
                    .withListVersion(params.getListVersion())
                    .withUpdateType(ocpp.cp._2015._10.UpdateType.DIFFERENTIAL)
                    .withLocalAuthorizationList(auths);

            // FULL update
        } else {
            List<AuthorizationData> values = Collections.emptyList();

            if (Boolean.FALSE.equals(params.getSendEmptyListWhenFull())) {
                values = ocppTagService.getAuthDataOfAllTags();
            }

            return new ocpp.cp._2015._10.SendLocalListRequest()
                    .withListVersion(params.getListVersion())
                    .withUpdateType(ocpp.cp._2015._10.UpdateType.FULL)
                    .withLocalAuthorizationList(values);
        }
    }

    private static List<ocpp.cp._2012._06.AuthorisationData> toOcpp15(
            List<ocpp.cp._2015._10.AuthorizationData> ocpp16) {
        return ocpp16.stream()
                     .map(k -> new ocpp.cp._2012._06.AuthorisationData().withIdTag(k.getIdTag())
                                                                        .withIdTagInfo(toOcpp15(k.getIdTagInfo())))
                     .collect(Collectors.toList());
    }

    private static ocpp.cp._2012._06.IdTagInfo toOcpp15(ocpp.cp._2015._10.IdTagInfo ocpp16) {
        return new ocpp.cp._2012._06.IdTagInfo()
                .withParentIdTag(ocpp16.getParentIdTag())
                .withExpiryDate(ocpp16.getExpiryDate())
                .withStatus(ocpp.cp._2012._06.AuthorizationStatus.fromValue(ocpp16.getStatus().value()));
    }
}
