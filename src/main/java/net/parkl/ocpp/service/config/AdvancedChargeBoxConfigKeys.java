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

import net.parkl.ocpp.entities.OcppChargingProcess;

public class AdvancedChargeBoxConfigKeys {
    /**
     * Start timeout enabled for availability change
     */
    public static final String KEY_START_TIMEOUT_ENABLED = "start.timeout.enabled";

    /**
     * Start timeout in seconds
     */
    public static final String KEY_START_TIMEOUT_SECS = "start.timeout.secs";

    /**
     * Preparing timeout enabled
     */
    public static final String KEY_PREPARING_TIMEOUT_ENABLED = "preparing.timeout.enabled";

    /**
     * Preparing timeout in seconds
     */
    public static final String KEY_PREPARING_TIMEOUT_SECS = "preparing.timeout.secs";

    /**
     * Transaction stop value is partial
     */
    public static final String KEY_TRANSACTION_PARTIAL_ENABLED = "transaction.partial.enabled";
    /**
     * Waiting for {@link OcppChargingProcess} at RemoteStarTransaction
     */
    public static final String KEY_WAITING_FOR_CHARGING_PROCESS_ENABLED = "waiting.for.charging.process.enabled";

    /**
     * Using id tags from {@link IntegratedIdTagProvider}
     */
    public static final String KEY_USING_INTEGRATED_IDTAG = "integrated.idtag";

    /**
     * Using id tag first 10 character
     */
    public static final String KEY_IDTAG_MAX10 = "idtag.max10";

    /**
     * Check reservation on StartTransaction
     */
    public static final String KEY_CHECK_RESERVATION = "check.reservation";

    /**
     * Check heartbeat offline alert for chargebox
     */
    public static final String KEY_SKIP_HEARTBEAT_CHECK = "skip.heartbeat.check";


}
