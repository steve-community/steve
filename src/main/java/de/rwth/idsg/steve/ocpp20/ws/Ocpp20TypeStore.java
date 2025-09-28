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
package de.rwth.idsg.steve.ocpp20.ws;

import de.rwth.idsg.steve.ocpp20.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Ocpp20TypeStore {

    private static final Map<String, Class<?>> REQUEST_MAP = new HashMap<>();
    private static final Map<String, Class<?>> RESPONSE_MAP = new HashMap<>();

    static {
        REQUEST_MAP.put("BootNotification", BootNotificationRequest.class);
        REQUEST_MAP.put("Authorize", AuthorizeRequest.class);
        REQUEST_MAP.put("TransactionEvent", TransactionEventRequest.class);
        REQUEST_MAP.put("StatusNotification", StatusNotificationRequest.class);
        REQUEST_MAP.put("Heartbeat", HeartbeatRequest.class);
        REQUEST_MAP.put("MeterValues", MeterValuesRequest.class);
        REQUEST_MAP.put("ClearedChargingLimit", ClearedChargingLimitRequest.class);
        REQUEST_MAP.put("FirmwareStatusNotification", FirmwareStatusNotificationRequest.class);
        REQUEST_MAP.put("LogStatusNotification", LogStatusNotificationRequest.class);
        REQUEST_MAP.put("NotifyChargingLimit", NotifyChargingLimitRequest.class);
        REQUEST_MAP.put("NotifyCustomerInformation", NotifyCustomerInformationRequest.class);
        REQUEST_MAP.put("NotifyDisplayMessages", NotifyDisplayMessagesRequest.class);
        REQUEST_MAP.put("NotifyEVChargingNeeds", NotifyEVChargingNeedsRequest.class);
        REQUEST_MAP.put("NotifyEVChargingSchedule", NotifyEVChargingScheduleRequest.class);
        REQUEST_MAP.put("NotifyEvent", NotifyEventRequest.class);
        REQUEST_MAP.put("NotifyMonitoringReport", NotifyMonitoringReportRequest.class);
        REQUEST_MAP.put("NotifyReport", NotifyReportRequest.class);
        REQUEST_MAP.put("PublishFirmwareStatusNotification", PublishFirmwareStatusNotificationRequest.class);
        REQUEST_MAP.put("ReportChargingProfiles", ReportChargingProfilesRequest.class);
        REQUEST_MAP.put("ReservationStatusUpdate", ReservationStatusUpdateRequest.class);
        REQUEST_MAP.put("SecurityEventNotification", SecurityEventNotificationRequest.class);
        REQUEST_MAP.put("SignCertificate", SignCertificateRequest.class);

        RESPONSE_MAP.put("BootNotification", BootNotificationResponse.class);
        RESPONSE_MAP.put("Authorize", AuthorizeResponse.class);
        RESPONSE_MAP.put("TransactionEvent", TransactionEventResponse.class);
        RESPONSE_MAP.put("StatusNotification", StatusNotificationResponse.class);
        RESPONSE_MAP.put("Heartbeat", HeartbeatResponse.class);
        RESPONSE_MAP.put("MeterValues", MeterValuesResponse.class);
        RESPONSE_MAP.put("ClearedChargingLimit", ClearedChargingLimitResponse.class);
        RESPONSE_MAP.put("FirmwareStatusNotification", FirmwareStatusNotificationResponse.class);
        RESPONSE_MAP.put("LogStatusNotification", LogStatusNotificationResponse.class);
        RESPONSE_MAP.put("NotifyChargingLimit", NotifyChargingLimitResponse.class);
        RESPONSE_MAP.put("NotifyCustomerInformation", NotifyCustomerInformationResponse.class);
        RESPONSE_MAP.put("NotifyDisplayMessages", NotifyDisplayMessagesResponse.class);
        RESPONSE_MAP.put("NotifyEVChargingNeeds", NotifyEVChargingNeedsResponse.class);
        RESPONSE_MAP.put("NotifyEVChargingSchedule", NotifyEVChargingScheduleResponse.class);
        RESPONSE_MAP.put("NotifyEvent", NotifyEventResponse.class);
        RESPONSE_MAP.put("NotifyMonitoringReport", NotifyMonitoringReportResponse.class);
        RESPONSE_MAP.put("NotifyReport", NotifyReportResponse.class);
        RESPONSE_MAP.put("PublishFirmwareStatusNotification", PublishFirmwareStatusNotificationResponse.class);
        RESPONSE_MAP.put("ReportChargingProfiles", ReportChargingProfilesResponse.class);
        RESPONSE_MAP.put("ReservationStatusUpdate", ReservationStatusUpdateResponse.class);
        RESPONSE_MAP.put("SecurityEventNotification", SecurityEventNotificationResponse.class);
        RESPONSE_MAP.put("SignCertificate", SignCertificateResponse.class);
    }

    public static Class<?> getRequestClass(String action) {
        Class<?> clazz = REQUEST_MAP.get(action);
        if (clazz == null) {
            log.warn("Unknown OCPP 2.0 action: {}", action);
            throw new IllegalArgumentException("Unknown action: " + action);
        }
        return clazz;
    }

    public static Class<?> getResponseClass(String action) {
        Class<?> clazz = RESPONSE_MAP.get(action);
        if (clazz == null) {
            log.warn("Unknown OCPP 2.0 action: {}", action);
            throw new IllegalArgumentException("Unknown action: " + action);
        }
        return clazz;
    }

    public static boolean isValidAction(String action) {
        return REQUEST_MAP.containsKey(action);
    }
}