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

import de.rwth.idsg.steve.gateway.config.GatewayProperties;
import de.rwth.idsg.steve.gateway.ocpi.model.AuthMethod;
import de.rwth.idsg.steve.gateway.ocpi.model.AuthorizationInfo;
import de.rwth.idsg.steve.gateway.ocpi.model.CDR;
import de.rwth.idsg.steve.gateway.ocpi.model.CdrLocation;
import de.rwth.idsg.steve.gateway.ocpi.model.CdrToken;
import de.rwth.idsg.steve.gateway.ocpi.model.ChargingPeriod;
import de.rwth.idsg.steve.gateway.ocpi.model.Connector;
import de.rwth.idsg.steve.gateway.ocpi.model.EVSE;
import de.rwth.idsg.steve.gateway.ocpi.model.GeoLocation;
import de.rwth.idsg.steve.gateway.ocpi.model.Location;
import de.rwth.idsg.steve.gateway.ocpi.model.LocationReferences;
import de.rwth.idsg.steve.gateway.ocpi.model.Price;
import de.rwth.idsg.steve.gateway.ocpi.model.Session;
import de.rwth.idsg.steve.gateway.ocpi.model.SessionStatus;
import de.rwth.idsg.steve.gateway.ocpi.model.TokenType;
import de.rwth.idsg.steve.gateway.repository.GatewayCdrMappingRepository;
import de.rwth.idsg.steve.gateway.repository.GatewaySessionMappingRepository;
import de.rwth.idsg.steve.gateway.service.CurrencyConversionService;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import jooq.steve.db.enums.GatewayCdrMappingProtocol;
import jooq.steve.db.enums.GatewaySessionMappingProtocol;
import jooq.steve.db.tables.records.GatewayCdrMappingRecord;
import jooq.steve.db.tables.records.GatewaySessionMappingRecord;
import org.joda.time.DateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter to convert OCPP data to OCPI format
 * Maps Steve's internal OCPP data structures to OCPI v2.2 format
 *
 * @author Steve Community
 */
@Slf4j
@Service
public class OcppToOcpiAdapter {

    private final ChargePointRepository chargePointRepository;
    private final TransactionRepository transactionRepository;
    private final GatewaySessionMappingRepository sessionMappingRepository;
    private final GatewayCdrMappingRepository cdrMappingRepository;
    private final GatewayProperties gatewayProperties;

    @Autowired(required = false)
    private CurrencyConversionService currencyConversionService;

    public OcppToOcpiAdapter(
        ChargePointRepository chargePointRepository,
        TransactionRepository transactionRepository,
        GatewaySessionMappingRepository sessionMappingRepository,
        GatewayCdrMappingRepository cdrMappingRepository,
        GatewayProperties gatewayProperties
    ) {
        this.chargePointRepository = chargePointRepository;
        this.transactionRepository = transactionRepository;
        this.sessionMappingRepository = sessionMappingRepository;
        this.cdrMappingRepository = cdrMappingRepository;
        this.gatewayProperties = gatewayProperties;
    }

    // Location related methods
    public List<Location> getLocations(String dateFrom, String dateTo, int offset, int limit) {
        log.debug("Converting charge points to OCPI locations with offset={}, limit={}", offset, limit);

        ChargePointQueryForm form = new ChargePointQueryForm();
        List<ChargePoint.Overview> chargePoints = chargePointRepository.getOverview(form);
        List<Location> allLocations = new ArrayList<>();

        for (ChargePoint.Overview cp : chargePoints) {
            Location location = convertChargePointToLocation(cp);
            if (location != null) {
                allLocations.add(location);
            }
        }

        int totalSize = allLocations.size();
        int fromIndex = Math.min(offset, totalSize);
        int toIndex = Math.min(offset + limit, totalSize);

        List<Location> paginatedLocations = allLocations.subList(fromIndex, toIndex);
        log.debug("Returning {} locations out of {} total (offset={}, limit={})",
            paginatedLocations.size(), totalSize, offset, limit);

        return paginatedLocations;
    }

    public Location getLocation(String locationId) {
        log.debug("Getting location for id: {}", locationId);

        // TODO: Implement location lookup by ID
        // This would typically involve finding a charge point by some identifier
        return null;
    }

    public EVSE getEvse(String locationId, String evseUid) {
        log.debug("Getting EVSE for location: {}, evse: {}", locationId, evseUid);

        // TODO: Implement EVSE lookup
        return null;
    }

    public Connector getConnector(String locationId, String evseUid, String connectorId) {
        log.debug("Getting connector for location: {}, evse: {}, connector: {}", locationId, evseUid, connectorId);

        // TODO: Implement connector lookup
        return null;
    }

    // Session related methods
    public List<Session> getSessions(String dateFrom, String dateTo, int offset, int limit) {
        log.debug("Converting transactions to OCPI sessions with offset={}, limit={}", offset, limit);

        TransactionQueryForm form = new TransactionQueryForm();
        List<Transaction> transactions = transactionRepository.getTransactions(form);

        List<Session> allSessions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            String sessionId = getOrCreateSessionId(transaction.getId());
            Session session = convertTransactionToSession(transaction, sessionId);
            if (session != null) {
                allSessions.add(session);
            }
        }

        int totalSize = allSessions.size();
        int fromIndex = Math.min(offset, totalSize);
        int toIndex = Math.min(offset + limit, totalSize);

        List<Session> paginatedSessions = allSessions.subList(fromIndex, toIndex);
        log.debug("Returning {} sessions out of {} total (offset={}, limit={})",
            paginatedSessions.size(), totalSize, offset, limit);

        return paginatedSessions;
    }

    public Session getSession(String sessionId) {
        log.debug("Getting session for id: {}", sessionId);

        Optional<GatewaySessionMappingRecord> mappingOpt = sessionMappingRepository.findBySessionId(sessionId);
        if (mappingOpt.isEmpty()) {
            log.warn("Session mapping not found for session ID: {}", sessionId);
            return null;
        }

        GatewaySessionMappingRecord mapping = mappingOpt.get();
        Integer transactionPk = mapping.getTransactionPk();

        TransactionDetails details = transactionRepository.getDetails(transactionPk);
        if (details == null) {
            log.warn("Transaction not found for transaction PK: {}", transactionPk);
            return null;
        }

        return convertTransactionToSession(details.getTransaction(), sessionId);
    }

    // CDR related methods
    public List<CDR> getCDRs(String dateFrom, String dateTo, int offset, int limit) {
        log.debug("Converting completed transactions to OCPI CDRs");

        TransactionQueryForm form = new TransactionQueryForm();
        List<Transaction> transactions = transactionRepository.getTransactions(form);

        List<CDR> cdrs = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getStopTimestamp() == null) {
                continue;
            }

            String cdrId = getOrCreateCdrId(transaction.getId());
            CDR cdr = convertTransactionToCDR(transaction, cdrId);
            if (cdr != null) {
                cdrs.add(cdr);
            }
        }

        return cdrs;
    }

    public CDR getCDR(String cdrId) {
        log.debug("Getting CDR for id: {}", cdrId);

        Optional<GatewayCdrMappingRecord> mappingOpt = cdrMappingRepository.findByCdrId(cdrId);
        if (mappingOpt.isEmpty()) {
            log.warn("CDR mapping not found for CDR ID: {}", cdrId);
            return null;
        }

        GatewayCdrMappingRecord mapping = mappingOpt.get();
        Integer transactionPk = mapping.getTransactionPk();

        TransactionDetails details = transactionRepository.getDetails(transactionPk);
        if (details == null) {
            log.warn("Transaction not found for transaction PK: {}", transactionPk);
            return null;
        }

        if (details.getTransaction().getStopTimestamp() == null) {
            log.warn("Transaction {} is not completed, cannot create CDR", transactionPk);
            return null;
        }

        return convertTransactionToCDR(details.getTransaction(), cdrId);
    }

    // Token authorization
    public AuthorizationInfo authorizeToken(LocationReferences locationReferences) {
        log.debug("Authorizing token: {}", locationReferences);

        // TODO: Implement token authorization logic
        // This would involve checking if the token is valid for the specified location
        return AuthorizationInfo.builder()
            .allowed("Accepted")
            .build();
    }

    // Private helper methods for conversion
    private Location convertChargePointToLocation(ChargePoint.Overview chargePoint) {
        log.debug("Converting charge point {} to OCPI location", chargePoint.getChargeBoxId());

        ChargePoint.Details details = chargePointRepository.getDetails(chargePoint.getChargeBoxPk());

        Location location = new Location();
        location.setCountryCode("DE");
        location.setPartyId("STE");
        location.setId(chargePoint.getChargeBoxId());

        if (details != null) {
            location.setName(details.getChargeBox().getDescription());

            if (details.getAddress() != null) {
                location.setAddress(details.getAddress().getStreet());
                location.setCity(details.getAddress().getCity());
                location.setPostalCode(details.getAddress().getZipCode());
                location.setCountry(details.getAddress().getCountry());
            }

            if (details.getChargeBox().getLocationLatitude() != null &&
                details.getChargeBox().getLocationLongitude() != null) {
                GeoLocation coords = new GeoLocation();
                coords.setLatitude(details.getChargeBox().getLocationLatitude().toString());
                coords.setLongitude(details.getChargeBox().getLocationLongitude().toString());
                location.setCoordinates(coords);
            }
        }

        List<EVSE> evses = new ArrayList<>();
        List<Integer> connectorIds = chargePointRepository.getNonZeroConnectorIds(chargePoint.getChargeBoxId());

        for (Integer connectorId : connectorIds) {
            EVSE evse = new EVSE();
            evse.setUid(chargePoint.getChargeBoxId() + "-" + connectorId);
            evse.setEvseId(chargePoint.getChargeBoxId() + "-" + connectorId);
            evse.setStatus(de.rwth.idsg.steve.gateway.ocpi.model.StatusType.AVAILABLE);

            Connector connector = new Connector();
            connector.setId(String.valueOf(connectorId));
            connector.setStandard(de.rwth.idsg.steve.gateway.ocpi.model.ConnectorType.IEC_62196_T2);
            connector.setFormat(de.rwth.idsg.steve.gateway.ocpi.model.ConnectorFormat.SOCKET);
            connector.setPowerType(de.rwth.idsg.steve.gateway.ocpi.model.PowerType.AC_3_PHASE);
            connector.setMaxVoltage(230);
            connector.setMaxAmperage(32);
            connector.setMaxElectricPower(22000);

            DateTime lastUpdated = chargePoint.getLastHeartbeatTimestampDT();
            if (lastUpdated == null) {
                lastUpdated = DateTime.now();
            }
            connector.setLastUpdated(lastUpdated);

            evse.setConnectors(List.of(connector));
            evse.setLastUpdated(lastUpdated);
            evses.add(evse);
        }

        location.setEvses(evses);

        DateTime lastUpdated = chargePoint.getLastHeartbeatTimestampDT();
        if (lastUpdated == null) {
            lastUpdated = DateTime.now();
        }
        location.setLastUpdated(lastUpdated);

        return location;
    }

    private String getOrCreateSessionId(Integer transactionPk) {
        Optional<GatewaySessionMappingRecord> existingMapping = sessionMappingRepository.findByTransactionPk(transactionPk);

        if (existingMapping.isPresent()) {
            return existingMapping.get().getSessionId();
        }

        String sessionId = UUID.randomUUID().toString();
        sessionMappingRepository.createMapping(transactionPk, GatewaySessionMappingProtocol.OCPI, sessionId, null);
        return sessionId;
    }

    private String getOrCreateCdrId(Integer transactionPk) {
        Optional<GatewayCdrMappingRecord> existingMapping = cdrMappingRepository.findByTransactionPk(transactionPk);

        if (existingMapping.isPresent()) {
            return existingMapping.get().getCdrId();
        }

        String cdrId = UUID.randomUUID().toString();
        cdrMappingRepository.createMapping(transactionPk, GatewayCdrMappingProtocol.OCPI, cdrId, null);
        return cdrId;
    }

    private Session convertTransactionToSession(Transaction transaction, String sessionId) {
        ChargePoint.Details chargePointDetails = chargePointRepository.getDetails(transaction.getChargeBoxPk());
        if (chargePointDetails == null) {
            log.warn("Charge point details not found for transaction {}", transaction.getId());
            return null;
        }

        BigDecimal kwhValue = null;
        if (transaction.getStartValue() != null && transaction.getStopValue() != null) {
            try {
                BigDecimal startWh = new BigDecimal(transaction.getStartValue());
                BigDecimal stopWh = new BigDecimal(transaction.getStopValue());
                kwhValue = stopWh.subtract(startWh).divide(new BigDecimal("1000"), 3, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                log.warn("Unable to parse meter values for transaction {}", transaction.getId(), e);
            }
        }

        CdrToken cdrToken = CdrToken.builder()
            .uid(transaction.getOcppIdTag())
            .type(TokenType.RFID)
            .contractId(transaction.getOcppIdTag())
            .build();

        SessionStatus status = transaction.getStopTimestamp() != null
            ? SessionStatus.COMPLETED
            : SessionStatus.ACTIVE;

        DateTime lastUpdated = transaction.getStopTimestamp() != null
            ? transaction.getStopTimestamp()
            : DateTime.now();

        return Session.builder()
            .countryCode("DE")
            .partyId("STE")
            .id(sessionId)
            .startDateTime(transaction.getStartTimestamp())
            .endDateTime(transaction.getStopTimestamp())
            .kwh(kwhValue)
            .cdrToken(cdrToken)
            .authMethod(AuthMethod.AUTH_REQUEST)
            .locationId(transaction.getChargeBoxId())
            .evseUid(transaction.getChargeBoxId() + "-" + transaction.getConnectorId())
            .connectorId(String.valueOf(transaction.getConnectorId()))
            .currency(gatewayProperties.getOcpi().getCurrency())
            .status(status)
            .lastUpdated(lastUpdated)
            .build();
    }

    private CDR convertTransactionToCDR(Transaction transaction, String cdrId) {
        ChargePoint.Details chargePointDetails = chargePointRepository.getDetails(transaction.getChargeBoxPk());
        if (chargePointDetails == null) {
            log.warn("Charge point details not found for transaction {}", transaction.getId());
            return null;
        }

        String sessionId = getOrCreateSessionId(transaction.getId());

        BigDecimal totalEnergy = null;
        if (transaction.getStartValue() != null && transaction.getStopValue() != null) {
            try {
                BigDecimal startWh = new BigDecimal(transaction.getStartValue());
                BigDecimal stopWh = new BigDecimal(transaction.getStopValue());
                totalEnergy = stopWh.subtract(startWh).divide(new BigDecimal("1000"), 3, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                log.warn("Unable to parse meter values for transaction {}", transaction.getId(), e);
            }
        }

        BigDecimal totalTime = null;
        if (transaction.getStartTimestamp() != null && transaction.getStopTimestamp() != null) {
            long durationSeconds = (transaction.getStopTimestamp().getMillis() - transaction.getStartTimestamp().getMillis()) / 1000;
            totalTime = new BigDecimal(durationSeconds).divide(new BigDecimal("3600"), 2, RoundingMode.HALF_UP);
        }

        CdrToken cdrToken = CdrToken.builder()
            .uid(transaction.getOcppIdTag())
            .type(TokenType.RFID)
            .contractId(transaction.getOcppIdTag())
            .build();

        CdrLocation cdrLocation = CdrLocation.builder()
            .id(transaction.getChargeBoxId())
            .name(chargePointDetails.getChargeBox().getDescription())
            .evseUid(transaction.getChargeBoxId() + "-" + transaction.getConnectorId())
            .connectorId(String.valueOf(transaction.getConnectorId()))
            .build();

        if (chargePointDetails.getAddress() != null) {
            cdrLocation.setAddress(chargePointDetails.getAddress().getStreet());
            cdrLocation.setCity(chargePointDetails.getAddress().getCity());
            cdrLocation.setPostalCode(chargePointDetails.getAddress().getZipCode());
            cdrLocation.setCountry(chargePointDetails.getAddress().getCountry());
        }

        if (chargePointDetails.getChargeBox().getLocationLatitude() != null &&
            chargePointDetails.getChargeBox().getLocationLongitude() != null) {
            GeoLocation coords = new GeoLocation();
            coords.setLatitude(chargePointDetails.getChargeBox().getLocationLatitude().toString());
            coords.setLongitude(chargePointDetails.getChargeBox().getLocationLongitude().toString());
            cdrLocation.setCoordinates(coords);
        }

        return CDR.builder()
            .countryCode("DE")
            .partyId("STE")
            .id(cdrId)
            .startDateTime(transaction.getStartTimestamp())
            .endDateTime(transaction.getStopTimestamp())
            .sessionId(sessionId)
            .cdrToken(cdrToken)
            .authMethod(AuthMethod.AUTH_REQUEST)
            .cdrLocation(cdrLocation)
            .currency(gatewayProperties.getOcpi().getCurrency())
            .totalEnergy(totalEnergy)
            .totalTime(totalTime)
            .lastUpdated(transaction.getStopTimestamp() != null ? transaction.getStopTimestamp() : DateTime.now())
            .build();
    }
}