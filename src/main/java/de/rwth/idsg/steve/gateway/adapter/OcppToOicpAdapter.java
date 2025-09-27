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
package de.rwth.idsg.steve.gateway.adapter;

import de.rwth.idsg.steve.gateway.oicp.model.AuthorizationStart;
import de.rwth.idsg.steve.gateway.oicp.model.AuthorizationStartResponse;
import de.rwth.idsg.steve.gateway.oicp.model.AuthorizationStop;
import de.rwth.idsg.steve.gateway.oicp.model.AuthorizationStopResponse;
import de.rwth.idsg.steve.gateway.oicp.model.ChargeDetailRecord;
import de.rwth.idsg.steve.gateway.oicp.model.ChargingNotification;
import de.rwth.idsg.steve.gateway.oicp.model.ChargingNotificationResponse;
import de.rwth.idsg.steve.gateway.oicp.model.EVSEData;
import de.rwth.idsg.steve.gateway.oicp.model.EVSEDataRequest;
import de.rwth.idsg.steve.gateway.oicp.model.EVSEStatusRecord;
import de.rwth.idsg.steve.gateway.oicp.model.EVSEStatusRequest;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    private final TransactionRepository transactionRepository;
    private final OcppTagRepository ocppTagRepository;

    // EVSE Data methods
    public List<EVSEData> getEVSEData(String operatorId, EVSEDataRequest request) {
        log.debug("Converting charge points to OICP EVSE data for operator: {}", operatorId);

        ChargePointQueryForm form = new ChargePointQueryForm();
        List<ChargePoint.Overview> chargePoints = chargePointRepository.getOverview(form);
        List<EVSEData> evseDataList = new ArrayList<>();

        for (ChargePoint.Overview cp : chargePoints) {
            EVSEData evseData = convertChargePointToEVSEData(cp);
            if (evseData != null) {
                evseDataList.add(evseData);
            }
        }

        return evseDataList;
    }

    public List<EVSEStatusRecord> getEVSEStatus(String operatorId, EVSEStatusRequest request) {
        log.debug("Getting EVSE status for operator: {}", operatorId);

        ConnectorStatusForm form = new ConnectorStatusForm();
        List<ConnectorStatus> connectorStatuses = chargePointRepository.getChargePointConnectorStatus(form);
        List<EVSEStatusRecord> statusRecords = new ArrayList<>();

        for (ConnectorStatus status : connectorStatuses) {
            EVSEStatusRecord record = convertConnectorStatusToEVSEStatus(status);
            if (record != null) {
                statusRecords.add(record);
            }
        }

        log.debug("Converted {} connector statuses to OICP EVSE status records", statusRecords.size());
        return statusRecords;
    }

    // Authorization methods
    public AuthorizationStartResponse authorizeStart(AuthorizationStart request) {
        log.debug("Processing authorization start request for EVSE: {}", request.getEvseId());

        try {
            de.rwth.idsg.steve.gateway.oicp.model.Identification identification = request.getIdentification();
            String idTag = identification != null ? identification.getRfidId() : null;

            if (idTag == null || idTag.isBlank()) {
                log.warn("Authorization failed: No RFID identification provided");
                return AuthorizationStartResponse.builder()
                    .sessionId(request.getSessionId())
                    .authorizationStatus("NotAuthorized")
                    .build();
            }

            OcppTagActivityRecord tagRecord = ocppTagRepository.getRecord(idTag);

            if (tagRecord == null) {
                log.warn("Authorization failed: Unknown RFID tag {}", idTag);
                return AuthorizationStartResponse.builder()
                    .sessionId(request.getSessionId())
                    .authorizationStatus("NotAuthorized")
                    .build();
            }

            if (tagRecord.getBlocked() != null && tagRecord.getBlocked()) {
                log.warn("Authorization failed: RFID tag {} is blocked", idTag);
                return AuthorizationStartResponse.builder()
                    .sessionId(request.getSessionId())
                    .authorizationStatus("Blocked")
                    .build();
            }

            if (tagRecord.getExpiryDate() != null && tagRecord.getExpiryDate().isBeforeNow()) {
                log.warn("Authorization failed: RFID tag {} has expired", idTag);
                return AuthorizationStartResponse.builder()
                    .sessionId(request.getSessionId())
                    .authorizationStatus("Expired")
                    .build();
            }

            log.info("Authorization successful for RFID tag {}", idTag);
            return AuthorizationStartResponse.builder()
                .sessionId(request.getSessionId())
                .authorizationStatus("Authorized")
                .build();
        } catch (Exception e) {
            log.error("Error during authorization for EVSE: {}", request.getEvseId(), e);
            return AuthorizationStartResponse.builder()
                .sessionId(request.getSessionId())
                .authorizationStatus("NotAuthorized")
                .build();
        }
    }

    public AuthorizationStopResponse authorizeStop(AuthorizationStop request) {
        log.debug("Processing authorization stop request for session: {}", request.getSessionId());

        log.info("Authorization stop successful for session {}", request.getSessionId());
        return AuthorizationStopResponse.builder()
            .sessionId(request.getSessionId())
            .authorizationStatus("Authorized")
            .build();
    }

    // Charging notification methods
    public ChargingNotificationResponse processChargingNotification(ChargingNotification notification) {
        log.debug("Processing charging notification: {}", notification.getType());

        log.info("Charging notification processed: type={}, sessionId={}",
            notification.getType(), notification.getSessionId());
        return ChargingNotificationResponse.builder()
            .result(true)
            .build();
    }

    public boolean processChargeDetailRecord(ChargeDetailRecord cdr) {
        log.debug("Processing charge detail record for session: {}", cdr.getSessionId());

        log.info("CDR processed: sessionId={}, chargingStart={}, chargingEnd={}",
            cdr.getSessionId(), cdr.getChargingStart(), cdr.getChargingEnd());
        return true;
    }

    // Private helper methods for conversion
    private EVSEData convertChargePointToEVSEData(ChargePoint.Overview chargePoint) {
        log.debug("Converting charge point {} to OICP EVSE data", chargePoint.getChargeBoxId());

        ChargePoint.Details details = chargePointRepository.getDetails(chargePoint.getChargeBoxPk());

        return EVSEData.builder()
            .evseId(chargePoint.getChargeBoxId())
            .chargingStationId(chargePoint.getChargeBoxId())
            .chargingStationName(chargePoint.getDescription() != null ? chargePoint.getDescription() : chargePoint.getChargeBoxId())
            .address(convertAddress(details.getAddress()))
            .geoCoordinates(convertGeoCoordinates(details.getChargeBox()))
            .isOpen24Hours(true)
            .isHubjectCompatible(true)
            .lastUpdate(details.getChargeBox().getLastHeartbeatTimestamp())
            .build();
    }

    private de.rwth.idsg.steve.gateway.oicp.model.Address convertAddress(jooq.steve.db.tables.records.AddressRecord addressRecord) {
        if (addressRecord == null) {
            return null;
        }

        return de.rwth.idsg.steve.gateway.oicp.model.Address.builder()
            .country(addressRecord.getCountry())
            .city(addressRecord.getCity())
            .street(addressRecord.getStreet())
            .postalCode(addressRecord.getZipCode())
            .build();
    }

    private de.rwth.idsg.steve.gateway.oicp.model.GeoCoordinates convertGeoCoordinates(jooq.steve.db.tables.records.ChargeBoxRecord chargeBoxRecord) {
        if (chargeBoxRecord == null) {
            return null;
        }

        java.math.BigDecimal latitude = chargeBoxRecord.getLocationLatitude();
        java.math.BigDecimal longitude = chargeBoxRecord.getLocationLongitude();

        if (latitude == null || longitude == null) {
            return null;
        }

        return de.rwth.idsg.steve.gateway.oicp.model.GeoCoordinates.builder()
            .latitude(latitude.toPlainString())
            .longitude(longitude.toPlainString())
            .build();
    }

    private EVSEStatusRecord convertConnectorStatusToEVSEStatus(ConnectorStatus status) {
        if (status == null) {
            return null;
        }

        String oicpStatus = mapOcppStatusToOicp(status.getStatus());

        return EVSEStatusRecord.builder()
            .evseId(status.getChargeBoxId() + "-" + status.getConnectorId())
            .evseStatus(oicpStatus)
            .statusChangeTimestamp(status.getStatusTimestamp() != null ?
                java.time.LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(status.getStatusTimestamp().getMillis()),
                    java.time.ZoneId.systemDefault()) : null)
            .build();
    }

    private String mapOcppStatusToOicp(String ocppStatus) {
        if (ocppStatus == null) {
            return "Unknown";
        }

        return switch (ocppStatus.toLowerCase()) {
            case "available" -> "Available";
            case "occupied", "charging" -> "Occupied";
            case "reserved" -> "Reserved";
            case "unavailable", "faulted" -> "OutOfService";
            default -> "Unknown";
        };
    }
}