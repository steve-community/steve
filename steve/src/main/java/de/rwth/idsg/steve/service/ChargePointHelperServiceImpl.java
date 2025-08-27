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
package de.rwth.idsg.steve.service;

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
import de.rwth.idsg.steve.utils.ConnectorStatusCountFilter;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import de.rwth.idsg.steve.web.dto.Statistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.RegistrationStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static de.rwth.idsg.steve.utils.DateTimeUtils.humanize;
import static de.rwth.idsg.steve.utils.DateTimeUtils.timeElapsed;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 24.03.2015
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChargePointHelperServiceImpl implements ChargePointHelperService {

    private final GenericRepository genericRepository;

    // SOAP-based charge points are stored in DB with an endpoint address
    private final ChargePointRepository chargePointRepository;

    // For WebSocket-based charge points, the active sessions are stored in memory
    private final Ocpp12WebSocketEndpoint ocpp12WebSocketEndpoint;
    private final Ocpp15WebSocketEndpoint ocpp15WebSocketEndpoint;
    private final Ocpp16WebSocketEndpoint ocpp16WebSocketEndpoint;

    @Override
    public Statistics getStats() {
        var stats = genericRepository.getStats();
        stats.setNumOcpp12JChargeBoxes(ocpp12WebSocketEndpoint.getNumberOfChargeBoxes());
        stats.setNumOcpp15JChargeBoxes(ocpp15WebSocketEndpoint.getNumberOfChargeBoxes());
        stats.setNumOcpp16JChargeBoxes(ocpp16WebSocketEndpoint.getNumberOfChargeBoxes());

        var latestList = chargePointRepository.getChargePointConnectorStatus();
        stats.setStatusCountMap(ConnectorStatusCountFilter.getStatusCountMap(latestList));

        return stats;
    }

    @Override
    public List<ConnectorStatus> getChargePointConnectorStatus(ConnectorStatusForm params) {
        var ocpp12Map = ocpp12WebSocketEndpoint.getACopy();
        var ocpp15Map = ocpp15WebSocketEndpoint.getACopy();
        var ocpp16Map = ocpp16WebSocketEndpoint.getACopy();

        var connectedJsonChargeBoxIds = new HashSet<>(extractIds(Arrays.asList(ocpp12Map, ocpp15Map, ocpp16Map)));

        var latestList = chargePointRepository.getChargePointConnectorStatus(params);

        // iterate over JSON stations and mark disconnected ones
        // https://github.com/steve-community/steve/issues/355
        //
        for (var status : latestList) {
            var protocol = status.getOcppProtocol();
            if (protocol != null && protocol.getTransport() == OcppTransport.JSON) {
                status.setJsonAndDisconnected(!connectedJsonChargeBoxIds.contains(status.getChargeBoxId()));
            }
        }

        return latestList;
    }

    @Override
    public List<OcppJsonStatus> getOcppJsonStatus(ZoneId timeZone) {
        var ocpp12Map = ocpp12WebSocketEndpoint.getACopy();
        var ocpp15Map = ocpp15WebSocketEndpoint.getACopy();
        var ocpp16Map = ocpp16WebSocketEndpoint.getACopy();

        var idList = extractIds(Arrays.asList(ocpp12Map, ocpp15Map, ocpp16Map));
        var primaryKeyLookup = chargePointRepository.getChargeBoxIdPkPair(idList);

        var now = Instant.now();
        var returnList = new ArrayList<OcppJsonStatus>();

        appendList(ocpp12Map, returnList, now, timeZone, OcppVersion.V_12, primaryKeyLookup);
        appendList(ocpp15Map, returnList, now, timeZone, OcppVersion.V_15, primaryKeyLookup);
        appendList(ocpp16Map, returnList, now, timeZone, OcppVersion.V_16, primaryKeyLookup);
        return returnList;
    }

    @Override
    public List<ChargePointSelect> getChargePoints(OcppVersion version) {
        return getChargePoints(
                version, Collections.singletonList(RegistrationStatus.ACCEPTED), Collections.emptyList());
    }

    @Override
    public List<ChargePointSelect> getChargePoints(OcppVersion version, List<RegistrationStatus> inStatusFilter) {
        return getChargePoints(version, inStatusFilter, Collections.emptyList());
    }

    public List<ChargePointSelect> getChargePointsWithIds(OcppVersion version, List<String> chargeBoxIdFilter) {
        return getChargePoints(version, Collections.singletonList(RegistrationStatus.ACCEPTED), chargeBoxIdFilter);
    }

    public List<ChargePointSelect> getChargePoints(
            OcppVersion version, List<RegistrationStatus> inStatusFilter, List<String> chargeBoxIdFilter) {
        return switch (version) {
            case V_12 ->
                getChargePoints(OcppProtocol.V_12_SOAP, inStatusFilter, chargeBoxIdFilter, ocpp12WebSocketEndpoint);
            case V_15 ->
                getChargePoints(OcppProtocol.V_15_SOAP, inStatusFilter, chargeBoxIdFilter, ocpp15WebSocketEndpoint);
            case V_16 ->
                getChargePoints(OcppProtocol.V_16_SOAP, inStatusFilter, chargeBoxIdFilter, ocpp16WebSocketEndpoint);
        };
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private List<ChargePointSelect> getChargePoints(
            OcppProtocol protocol,
            List<RegistrationStatus> inStatusFilter,
            List<String> chargeBoxIdFilter,
            AbstractWebSocketEndpoint jsonEndpoint) {
        // soap stations
        //
        var statusFilter =
                inStatusFilter.stream().map(RegistrationStatus::value).toList();

        var returnList = chargePointRepository.getChargePointSelect(protocol, statusFilter, chargeBoxIdFilter);

        // json stations
        //
        var chargeBoxIdList = CollectionUtils.isEmpty(chargeBoxIdFilter)
                ? jsonEndpoint.getChargeBoxIdList()
                : jsonEndpoint.getChargeBoxIdList().stream()
                        .filter(chargeBoxIdFilter::contains)
                        .toList();

        var jsonProtocol = OcppProtocol.from(jsonEndpoint.getVersion(), OcppTransport.JSON);

        for (var chargeBoxId : chargeBoxIdList) {
            returnList.add(new ChargePointSelect(jsonProtocol, chargeBoxId));
        }

        return returnList;
    }

    private static List<String> extractIds(List<Map<String, Deque<SessionContext>>> ocppMaps) {
        return ocppMaps.stream().map(Map::keySet).flatMap(Collection::stream).toList();
    }

    private static void appendList(
            Map<String, Deque<SessionContext>> map,
            List<OcppJsonStatus> returnList,
            Instant now,
            ZoneId timeZone,
            OcppVersion version,
            Map<String, Integer> primaryKeyLookup) {

        for (var entry : map.entrySet()) {
            var chargeBoxId = entry.getKey();
            var endpointDeque = entry.getValue();

            for (var ctx : endpointDeque) {
                var openSince = ctx.getOpenSince();

                var status = OcppJsonStatus.builder()
                        .chargeBoxPk(primaryKeyLookup.get(chargeBoxId))
                        .chargeBoxId(chargeBoxId)
                        .connectedSinceDT(openSince)
                        .connectedSince(humanize(openSince, timeZone))
                        .connectionDuration(timeElapsed(openSince, now))
                        .version(version)
                        .build();

                returnList.add(status);
            }
        }
    }
}
