/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2019 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import ocpp.cp._2015._10.AuthorizationData;
import ocpp.cs._2015._10.IdTagInfo;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 03.01.2015
 */
public interface OcppTagService {
    IdTagInfo getIdTagInfo(String idTag, boolean isStartTransactionReqContext,String askingChargeBoxId);
    IdTagInfo getIdTagInfo(String idTag, boolean isStartTransactionReqContext,String askingChargeBoxId, Supplier<IdTagInfo> supplierWhenException);
    List<AuthorizationData> getAuthDataOfAllTags();
    List<AuthorizationData> getAuthData(List<String> idTagList);
    List<UnidentifiedIncomingObject> getUnknownOcppTags();
    void removeUnknown(List<String> idTagList);
}
