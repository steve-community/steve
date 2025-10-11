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
package de.rwth.idsg.steve.gateway.oicp.adapter;

import de.rwth.idsg.steve.gateway.oicp.model.cpo.AddressIso19773;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAcknowledgment;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizationStart;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizationStop;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizationStop.AuthorizationStatusEnum;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizeStart;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizeStop;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingChargeDetailRecord;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingChargingNotificationEnd;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingChargingNotificationError;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingChargingNotificationProgress;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingChargingNotificationStart;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingChargingNotificationsV11Request;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingEVSEData;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingEVSEStatusByID;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingEVSEStatusByIDEVSEStatusRecords;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingEVSEStatusEvseStatusesOperatorEvseStatusInnerEvseStatusRecordInner;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingPullEVSEData;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingPullEVSEStatus;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingPullEVSEStatusByID;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingPullEVSEStatusByOperatorID;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingPullEvseStatusV21200Response;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingPullEvseStatusV21Request;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.EvseStatus;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.GeoCoordinates;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.GeoCoordinatesDecimalDegree;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.InfoTextType;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.PullEvseDataRecord;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

import static de.rwth.idsg.steve.gateway.oicp.OicpResponse.successCode;
import static de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAuthorizationStart.AuthorizationStatusEnum.*;

/**
 * Adapter to convert OCPP data to OICP format
 * Maps Steve's internal OCPP data structures to OICP v2.3 format
 *
 * @author Steve Community
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OcppToOicpAdapter {

    private final ChargePointRepository chargePointRepository;
    private final OcppTagRepository ocppTagRepository;

    // EVSE Data methods
    public ERoamingEVSEData getEVSEData(String providerID, ERoamingPullEVSEData request) {
        log.debug("Converting charge points to OICP EVSE data for provider: {}", providerID);

        var form = new ChargePointQueryForm();
        // TODO update form based on request filters

        var evseDataList = chargePointRepository.getOverview(form).stream()
                .map(this::convertChargePointToEVSEData)
                .filter(Objects::nonNull)
                .toList();

        log.debug("Converted {} charge point overview to OICP EVSE data records", evseDataList.size());
        return ERoamingEVSEData.builder()
                .statusCode(successCode())
                .content(evseDataList)
                .size(evseDataList.size())
                .numberOfElements(evseDataList.size())
                .first(true)
                .last(true)
                .number(0)
                .totalElements(evseDataList.size())
                .totalPages(1)
                .build();
    }

    public ERoamingPullEvseStatusV21200Response getEVSEStatus(
            String providerID, ERoamingPullEvseStatusV21Request request) {
        log.debug("Getting EVSE status for provider: {}", providerID);

        var form = new ConnectorStatusForm();
        // TODO update form based on request filters
        switch (request) {
            case ERoamingPullEVSEStatus status -> {
                // Handle ERoamingPullEVSEStatus specific logic
                // For now, no specific filters are applied
            }
            case ERoamingPullEVSEStatusByID byId -> {
                // Handle ERoamingPullEVSEStatusByID specific logic
                // For now, no specific filters are applied
            }
            case ERoamingPullEVSEStatusByOperatorID byOperatorId -> {
                // Handle ERoamingPullEVSEStatusByOperatorID specific logic
                // For now, no specific filters are applied
            }
            default -> throw new IllegalArgumentException("Unknown request type: " + request.getClass());
        }

        var statusRecords = chargePointRepository.getChargePointConnectorStatus(form).stream()
                .map(OcppToOicpAdapter::convertConnectorStatusToEVSEStatus)
                .filter(Objects::nonNull)
                .toList();

        log.debug("Converted {} connector statuses to OICP EVSE status records", statusRecords.size());
        return ERoamingEVSEStatusByID.builder()
                .statusCode(successCode())
                .evSEStatusRecords(ERoamingEVSEStatusByIDEVSEStatusRecords.builder()
                        .evseStatusRecord(statusRecords)
                        .build())
                .build();
    }

    // Authorization methods
    public ERoamingAuthorizationStart authorizeStart(ERoamingAuthorizeStart request) {
        log.debug("Processing authorization start request for EVSE: {}", request.getEvseID());

        try {
            var identification = request.getIdentification();
            var idTag = identification != null ? identification.getRfIDIdentification() : null;

            if (idTag == null || idTag.getUID().isBlank()) {
                log.warn("Authorization failed: No RFID identification provided");
                return ERoamingAuthorizationStart.builder()
                        .sessionID(request.getSessionID())
                        .statusCode(successCode())
                        .authorizationStatus(NOT_AUTHORIZED)
                        .build();
            }

            var tagRecord = ocppTagRepository.getRecord(idTag.getUID()).orElse(null);
            if (tagRecord == null) {
                log.warn("Authorization failed: Unknown RFID tag {}", idTag);
                return ERoamingAuthorizationStart.builder()
                        .sessionID(request.getSessionID())
                        .statusCode(successCode())
                        .authorizationStatus(NOT_AUTHORIZED)
                        .build();
            }

            if (tagRecord.isBlocked()) {
                log.warn("Authorization failed: RFID tag {} is blocked", idTag);
                return ERoamingAuthorizationStart.builder()
                        .sessionID(request.getSessionID())
                        .statusCode(successCode())
                        .authorizationStatus(NOT_AUTHORIZED)
                        .build();
            }

            if (tagRecord.getExpiryDate() != null && tagRecord.getExpiryDate().isBefore(Instant.now())) {
                log.warn("Authorization failed: RFID tag {} has expired", idTag);
                return ERoamingAuthorizationStart.builder()
                        .sessionID(request.getSessionID())
                        .statusCode(successCode())
                        .authorizationStatus(NOT_AUTHORIZED)
                        .build();
            }

            log.info("Authorization successful for RFID tag {}", idTag);
            return ERoamingAuthorizationStart.builder()
                    .sessionID(request.getSessionID())
                    .statusCode(successCode())
                    .authorizationStatus(AUTHORIZED)
                    .build();
        } catch (Exception e) {
            log.error("Error during authorization for EVSE: {}", request.getEvseID(), e);
            return ERoamingAuthorizationStart.builder()
                    .sessionID(request.getSessionID())
                    .statusCode(successCode())
                    .authorizationStatus(NOT_AUTHORIZED)
                    .build();
        }
    }

    public ERoamingAuthorizationStop authorizeStop(ERoamingAuthorizeStop request) {
        log.debug("Processing authorization stop request for session: {}", request.getSessionID());

        log.info("Authorization stop successful for session {}", request.getSessionID());
        return ERoamingAuthorizationStop.builder()
                .sessionID(request.getSessionID())
                .statusCode(successCode())
                .authorizationStatus(AuthorizationStatusEnum.AUTHORIZED)
                .build();
    }

    // Charging notification methods
    public ERoamingAcknowledgment processChargingNotification(ERoamingChargingNotificationsV11Request notification) {
        var sessionId =
                switch (notification) {
                    case ERoamingChargingNotificationStart start -> start.getSessionID();
                    case ERoamingChargingNotificationEnd stop -> stop.getSessionID();
                    case ERoamingChargingNotificationProgress progress -> progress.getSessionID();
                    case ERoamingChargingNotificationError error -> error.getSessionID();
                    default ->
                        throw new IllegalArgumentException("Unknown notification type: " + notification.getClass());
                };
        log.info("Processing {} for session: {}", notification.getClass(), sessionId);

        // TOOD handle different notification types accordingly

        return ERoamingAcknowledgment.builder()
                .sessionID(sessionId)
                .statusCode(successCode())
                .result(true)
                .build();
    }

    public ERoamingAcknowledgment processChargeDetailRecord(ERoamingChargeDetailRecord cdr) {
        log.debug("Processing charge detail record for session: {}", cdr.getSessionID());

        log.info(
                "CDR processed: sessionId={}, chargingStart={}, chargingEnd={}",
                cdr.getSessionID(),
                cdr.getChargingStart(),
                cdr.getChargingEnd());

        return ERoamingAcknowledgment.builder()
                .sessionID(cdr.getSessionID())
                .statusCode(successCode())
                .result(true)
                .build();
    }

    // Private helper methods for conversion
    private PullEvseDataRecord convertChargePointToEVSEData(ChargePoint.Overview chargePoint) {
        log.debug("Converting charge point {} to OICP EVSE data", chargePoint.getChargeBoxId());

        var details =
                chargePointRepository.getDetails(chargePoint.getChargeBoxPk()).orElseThrow();

        return PullEvseDataRecord.builder()
                .evseID(chargePoint.getChargeBoxId())
                .chargingStationId(chargePoint.getChargeBoxId())
                .chargingStationNames(List.of(InfoTextType.builder()
                        .lang("en")
                        .value(
                                chargePoint.getDescription() != null
                                        ? chargePoint.getDescription()
                                        : chargePoint.getChargeBoxId())
                        .build()))
                .address(convertAddress(details))
                .geoCoordinates(convertGeoCoordinates(details))
                .isOpen24Hours(true)
                .isHubjectCompatible(true)
                .lastUpdate(
                        details.getLastHeartbeatTimestamp() == null
                                ? null
                                : details.getLastHeartbeatTimestamp().atOffset(ZoneOffset.UTC))
                .build();
    }

    private static @Nullable AddressIso19773 convertAddress(ChargePoint.@Nullable Details addressRecord) {
        if (addressRecord == null) {
            return null;
        }

        return AddressIso19773.builder()
                .country(addressRecord.getCountry().getName())
                .city(addressRecord.getCity())
                .street(addressRecord.getStreet())
                .postalCode(addressRecord.getZipCode())
                .build();
    }

    private static @Nullable GeoCoordinates convertGeoCoordinates(ChargePoint.@Nullable Details chargeBoxRecord) {
        if (chargeBoxRecord == null) {
            return null;
        }

        var latitude = chargeBoxRecord.getLocationLatitude();
        var longitude = chargeBoxRecord.getLocationLongitude();

        if (latitude == null || longitude == null) {
            return null;
        }

        return GeoCoordinates.builder()
                .decimalDegree(GeoCoordinatesDecimalDegree.builder()
                        .latitude(latitude.toPlainString())
                        .longitude(longitude.toPlainString())
                        .build())
                .build();
    }

    private static @Nullable ERoamingEVSEStatusEvseStatusesOperatorEvseStatusInnerEvseStatusRecordInner
            convertConnectorStatusToEVSEStatus(@Nullable ConnectorStatus status) {
        if (status == null) {
            return null;
        }

        var oicpStatus = mapOcppStatusToOicp(status.getStatus());

        return ERoamingEVSEStatusEvseStatusesOperatorEvseStatusInnerEvseStatusRecordInner.builder()
                .evseID(status.getChargeBoxId() + "-" + status.getConnectorId())
                .evseStatus(oicpStatus)
                .build();
    }

    private static EvseStatus mapOcppStatusToOicp(String ocppStatus) {
        if (ocppStatus == null) {
            return EvseStatus.UNKNOWN;
        }

        return switch (ocppStatus.toLowerCase()) {
            case "available" -> EvseStatus.AVAILABLE;
            case "occupied", "charging" -> EvseStatus.OCCUPIED;
            case "reserved" -> EvseStatus.RESERVED;
            case "unavailable", "faulted" -> EvseStatus.OUT_OF_SERVICE;
            default -> EvseStatus.UNKNOWN;
        };
    }
}
