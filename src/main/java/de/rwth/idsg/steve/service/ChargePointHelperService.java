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
package de.rwth.idsg.steve.service;

import com.google.common.util.concurrent.Striped;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16WebSocketEndpoint;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.GenericRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.utils.ConnectorStatusCountFilter;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import de.rwth.idsg.steve.web.dto.Statistics;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.RegistrationStatus;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 24.03.2015
 */
@Slf4j
@Service
public class ChargePointHelperService {

    private final boolean autoRegisterUnknownStations = CONFIG.getOcpp().isAutoRegisterUnknownStations();
    private final Striped<Lock> isRegisteredLocks = Striped.lock(16);

    @Autowired private GenericRepository genericRepository;

    // SOAP-based charge points are stored in DB with an endpoint address
    @Autowired private ChargePointRepository chargePointRepository;

    // For WebSocket-based charge points, the active sessions are stored in memory
    @Autowired private Ocpp12WebSocketEndpoint ocpp12WebSocketEndpoint;
    @Autowired private Ocpp15WebSocketEndpoint ocpp15WebSocketEndpoint;
    @Autowired private Ocpp16WebSocketEndpoint ocpp16WebSocketEndpoint;

    private final UnidentifiedIncomingObjectService unknownChargePointService = new UnidentifiedIncomingObjectService(100);

    public Optional<RegistrationStatus> getRegistrationStatus(String chargeBoxId) {
        Lock l = isRegisteredLocks.get(chargeBoxId);
        l.lock();
        try {
            Optional<RegistrationStatus> status = getRegistrationStatusInternal(chargeBoxId);
            if (status.isEmpty()) {
                unknownChargePointService.processNewUnidentified(chargeBoxId);
            }
            return status;
        } finally {
            l.unlock();
        }
    }

    public Statistics getStats() {
        Statistics stats = genericRepository.getStats();
        stats.setNumOcpp12JChargeBoxes(ocpp12WebSocketEndpoint.getNumberOfChargeBoxes());
        stats.setNumOcpp15JChargeBoxes(ocpp15WebSocketEndpoint.getNumberOfChargeBoxes());
        stats.setNumOcpp16JChargeBoxes(ocpp16WebSocketEndpoint.getNumberOfChargeBoxes());

        List<ConnectorStatus> latestList = chargePointRepository.getChargePointConnectorStatus();
        stats.setStatusCountMap(ConnectorStatusCountFilter.getStatusCountMap(latestList));

        return stats;
    }

    public List<ConnectorStatus> getChargePointConnectorStatus(ConnectorStatusForm params) {
        Map<String, Deque<SessionContext>> ocpp12Map = ocpp12WebSocketEndpoint.getACopy();
        Map<String, Deque<SessionContext>> ocpp15Map = ocpp15WebSocketEndpoint.getACopy();
        Map<String, Deque<SessionContext>> ocpp16Map = ocpp16WebSocketEndpoint.getACopy();

        Set<String> connectedJsonChargeBoxIds = new HashSet<>(extractIds(Arrays.asList(ocpp12Map, ocpp15Map, ocpp16Map)));

        List<ConnectorStatus> latestList = chargePointRepository.getChargePointConnectorStatus(params);

        // iterate over JSON stations and mark disconnected ones
        // https://github.com/RWTH-i5-IDSG/steve/issues/355
        //
        for (ConnectorStatus status : latestList) {
            OcppProtocol protocol = status.getOcppProtocol();
            if (protocol != null && protocol.getTransport() == OcppTransport.JSON) {
                status.setJsonAndDisconnected(!connectedJsonChargeBoxIds.contains(status.getChargeBoxId()));
            }
        }

        return latestList;
    }

    public List<OcppJsonStatus> getOcppJsonStatus() {
        Map<String, Deque<SessionContext>> ocpp12Map = ocpp12WebSocketEndpoint.getACopy();
        Map<String, Deque<SessionContext>> ocpp15Map = ocpp15WebSocketEndpoint.getACopy();
        Map<String, Deque<SessionContext>> ocpp16Map = ocpp16WebSocketEndpoint.getACopy();

        List<String> idList = extractIds(Arrays.asList(ocpp12Map, ocpp15Map, ocpp16Map));
        Map<String, Integer> primaryKeyLookup = chargePointRepository.getChargeBoxIdPkPair(idList);

        DateTime now = DateTime.now();
        List<OcppJsonStatus> returnList = new ArrayList<>();

        appendList(ocpp12Map, returnList, now, OcppVersion.V_12, primaryKeyLookup);
        appendList(ocpp15Map, returnList, now, OcppVersion.V_15, primaryKeyLookup);
        appendList(ocpp16Map, returnList, now, OcppVersion.V_16, primaryKeyLookup);
        return returnList;
    }

    public List<ChargePointSelect> getChargePoints(OcppVersion version) {
        return getChargePoints(version, Collections.singletonList(RegistrationStatus.ACCEPTED));
    }

    public List<ChargePointSelect> getChargePoints(OcppVersion version, List<RegistrationStatus> inStatusFilter) {
        switch (version) {
            case V_12:
                return getChargePoints(OcppProtocol.V_12_SOAP, inStatusFilter, ocpp12WebSocketEndpoint);
            case V_15:
                return getChargePoints(OcppProtocol.V_15_SOAP, inStatusFilter, ocpp15WebSocketEndpoint);
            case V_16:
                return getChargePoints(OcppProtocol.V_16_SOAP, inStatusFilter, ocpp16WebSocketEndpoint);
            default:
                throw new IllegalArgumentException("Unknown OCPP version: " + version);
        }
    }

    public List<UnidentifiedIncomingObject> getUnknownChargePoints() {
        return unknownChargePointService.getObjects();
    }

    public void removeUnknown(String chargeBoxId) {
        unknownChargePointService.remove(chargeBoxId);
    }

    public void removeUnknown(List<String> chargeBoxIdList) {
        unknownChargePointService.removeAll(chargeBoxIdList);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Optional<RegistrationStatus> getRegistrationStatusInternal(String chargeBoxId) {
        // 1. exit if already registered
        Optional<String> status = chargePointRepository.getRegistrationStatus(chargeBoxId);
        if (status.isPresent()) {
            try {
                return Optional.ofNullable(RegistrationStatus.fromValue(status.get()));
            } catch (Exception e) {
                // in cases where the database entry (string) is altered, and therefore cannot be converted to enum
                log.error("Exception happened", e);
                return Optional.empty();
            }
        }

        // 2. ok, this chargeBoxId is unknown. exit if auto-register is disabled
        if (!autoRegisterUnknownStations) {
            return Optional.empty();
        }

        // 3. chargeBoxId is unknown and auto-register is enabled. insert chargeBoxId
        try {
            chargePointRepository.addChargePointList(Collections.singletonList(chargeBoxId));
            log.warn("Auto-registered unknown chargebox '{}'", chargeBoxId);
            return Optional.of(RegistrationStatus.ACCEPTED); // default db value is accepted
        } catch (Exception e) {
            log.error("Failed to auto-register unknown chargebox '" + chargeBoxId + "'", e);
            return Optional.empty();
        }
    }

    private List<ChargePointSelect> getChargePoints(OcppProtocol protocol, List<RegistrationStatus> inStatusFilter,
                                                    AbstractWebSocketEndpoint jsonEndpoint) {
        List<String> statusFilter = inStatusFilter.stream()
                                                  .map(RegistrationStatus::value)
                                                  .collect(Collectors.toList());

        List<ChargePointSelect> returnList = chargePointRepository.getChargePointSelect(protocol, statusFilter);
        for (String chargeBoxId : jsonEndpoint.getChargeBoxIdList()) {
            returnList.add(new ChargePointSelect(OcppTransport.JSON, chargeBoxId));
        }
        return returnList;
    }

    private static List<String> extractIds(List<Map<String, Deque<SessionContext>>> ocppMaps) {
        return ocppMaps.stream()
                       .map(Map::keySet)
                       .flatMap(Collection::stream)
                       .collect(Collectors.toList());
    }

    private static void appendList(Map<String, Deque<SessionContext>> map, List<OcppJsonStatus> returnList,
                                   DateTime now, OcppVersion version, Map<String, Integer> primaryKeyLookup) {

        for (Map.Entry<String, Deque<SessionContext>> entry : map.entrySet()) {
            String chargeBoxId = entry.getKey();
            Deque<SessionContext> endpointDeque = entry.getValue();

            for (SessionContext ctx : endpointDeque) {
                DateTime openSince = ctx.getOpenSince();

                OcppJsonStatus status = OcppJsonStatus.builder()
                                                      .chargeBoxPk(primaryKeyLookup.get(chargeBoxId))
                                                      .chargeBoxId(chargeBoxId)
                                                      .connectedSinceDT(openSince)
                                                      .connectedSince(DateTimeUtils.humanize(openSince))
                                                      .connectionDuration(DateTimeUtils.timeElapsed(openSince, now))
                                                      .version(version)
                                                      .build();

                returnList.add(status);
            }
        }
    }
}
