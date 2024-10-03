/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppTag;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.service.config.IntegratedIdTagProvider;
import net.parkl.ocpp.service.cs.SettingsService;
import net.parkl.ocpp.service.middleware.OcppChargingMiddleware;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.IdTagInfo;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTagServiceRemote implements AuthTagService {

    private final OcppChargingMiddleware chargingMiddleware;
    private final AdvancedChargeBoxConfiguration config;
    private final IntegratedIdTagProvider integratedIdTagProvider;


    @Override
    public IdTagInfo decideStatus(String idTag, boolean isStartTransactionReqContext,
                                  @Nullable String chargeBoxId, @Nullable Integer connectorId) {

        if (config.isUsingIntegratedTag(chargeBoxId)
                && integratedIdTagProvider.integratedTags().stream().noneMatch(idTag::equalsIgnoreCase)) {
            return new IdTagInfo().withStatus(AuthorizationStatus.INVALID);
        } else {
            if (!chargingMiddleware.checkRfidTag(idTag, chargeBoxId)) {
                log.error("The user with idTag '{}' is INVALID (validation failed on Parkl backend).", idTag);
                return new IdTagInfo().withStatus(AuthorizationStatus.INVALID);
            }
        }



        log.debug("The user with idTag '{}' is ACCEPTED.", idTag);
        return new IdTagInfo()
            .withStatus(AuthorizationStatus.ACCEPTED);
    }


}
