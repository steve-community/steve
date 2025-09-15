/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.impl.OcppVersionHandler;
import de.rwth.idsg.steve.ocpp.task.impl.TaskDefinition;
import de.rwth.idsg.steve.service.OcppTagsService;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListParams;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListUpdateType;
import ocpp.cp._2015._10.AuthorizationData;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class SendLocalListTask extends CommunicationTask<SendLocalListParams, String> {

    private final OcppTagsService ocppTagsService;

    private static final TaskDefinition<SendLocalListParams, String> TASK_DEFINITION =
            TaskDefinition.<SendLocalListParams, String>builder()
                    .versionHandler(
                            OcppVersion.V_15,
                            new OcppVersionHandler<>(
                                    task -> {
                                        SendLocalListTask t = (SendLocalListTask) task;
                                        ocpp.cp._2015._10.SendLocalListRequest ocpp16Request =
                                                t.createOcpp16Request(t.ocppTagsService);
                                        return new ocpp.cp._2012._06.SendLocalListRequest()
                                                .withListVersion(ocpp16Request.getListVersion())
                                                .withUpdateType(ocpp.cp._2012._06.UpdateType.fromValue(ocpp16Request
                                                        .getUpdateType()
                                                        .value()))
                                                .withLocalAuthorisationList(
                                                        toOcpp15(ocpp16Request.getLocalAuthorizationList()));
                                    },
                                    (ocpp.cp._2012._06.SendLocalListResponse r) ->
                                            r.getStatus().value()))
                    .versionHandler(
                            OcppVersion.V_16,
                            new OcppVersionHandler<>(
                                    task -> {
                                        SendLocalListTask t = (SendLocalListTask) task;
                                        return t.createOcpp16Request(t.ocppTagsService);
                                    },
                                    (ocpp.cp._2015._10.SendLocalListResponse r) ->
                                            r.getStatus().value()))
                    .build();

    public SendLocalListTask(SendLocalListParams params, OcppTagsService ocppTagsService, String caller) {
        super(TASK_DEFINITION, params, caller);
        this.ocppTagsService = ocppTagsService;
    }

    public SendLocalListTask(SendLocalListParams params, OcppTagsService ocppTagsService) {
        this(params, ocppTagsService, "SteVe");
    }

    private ocpp.cp._2015._10.SendLocalListRequest createOcpp16Request(OcppTagsService ocppTagsService) {
        if (getParams().getUpdateType() == SendLocalListUpdateType.DIFFERENTIAL) {
            var auths = new ArrayList<ocpp.cp._2015._10.AuthorizationData>();
            for (var idTag : getParams().getDeleteList()) {
                auths.add(new ocpp.cp._2015._10.AuthorizationData().withIdTag(idTag));
            }
            auths.addAll(ocppTagsService.getAuthData(getParams().getAddUpdateList()));
            return new ocpp.cp._2015._10.SendLocalListRequest()
                    .withListVersion(getParams().getListVersion())
                    .withUpdateType(ocpp.cp._2015._10.UpdateType.DIFFERENTIAL)
                    .withLocalAuthorizationList(auths);
        } else {
            var values = Collections.<AuthorizationData>emptyList();
            if (Boolean.FALSE.equals(getParams().getSendEmptyListWhenFull())) {
                values = ocppTagsService.getAuthDataOfAllTags();
            }
            return new ocpp.cp._2015._10.SendLocalListRequest()
                    .withListVersion(getParams().getListVersion())
                    .withUpdateType(ocpp.cp._2015._10.UpdateType.FULL)
                    .withLocalAuthorizationList(values);
        }
    }

    private static List<ocpp.cp._2012._06.AuthorisationData> toOcpp15(
            List<ocpp.cp._2015._10.AuthorizationData> ocpp16) {
        return ocpp16.stream()
                .map(k -> new ocpp.cp._2012._06.AuthorisationData()
                        .withIdTag(k.getIdTag())
                        .withIdTagInfo(toOcpp15(k.getIdTagInfo())))
                .toList();
    }

    private static ocpp.cp._2012._06.@Nullable IdTagInfo toOcpp15(ocpp.cp._2015._10.@Nullable IdTagInfo ocpp16) {
        if (ocpp16 == null) {
            return null;
        }
        return new ocpp.cp._2012._06.IdTagInfo()
                .withParentIdTag(ocpp16.getParentIdTag())
                .withExpiryDate(ocpp16.getExpiryDate())
                .withStatus(ocpp.cp._2012._06.AuthorizationStatus.fromValue(
                        ocpp16.getStatus().value()));
    }
}
