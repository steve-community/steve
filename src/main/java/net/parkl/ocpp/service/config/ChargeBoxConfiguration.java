/*
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
package net.parkl.ocpp.service.config;

import java.util.List;

public interface ChargeBoxConfiguration {
    boolean isStartTimeoutEnabled(String chargeBoxId);
    int getStartTimeoutSecs(String chargeBoxId);
    boolean isPreparingTimeoutEnabled(String chargeBoxId);
    int getPreparingTimeoutSecs(String chargeBoxId);
    boolean isTransactionPartialEnabled(String chargeBoxId);
    boolean waitingForChargingProcessEnabled(String chargeBoxId);
    boolean isStartTimeoutEnabledForAny();
    boolean isPreparingTimeoutEnabledForAny();
    boolean isUsingIntegratedTag(String chargeBoxId);
    boolean checkReservationId(String chargeBoxId);
    List<String> getChargeBoxesForAlert();
    boolean skipHeartBeatConfig(String chargeBoxId);

    boolean isIdTagMax10Characters(String chargeBoxId);
    float getWebSocketBufferMultiplier(String chargeBoxId, float defaultMultiplier);
    boolean ignoreConnectorAvailableUntilStopTransaction(String chargeBoxId);
}
