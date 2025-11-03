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

import com.google.common.util.concurrent.Striped;
import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStore;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStoreHolder;
import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.GenericRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.utils.ConnectorStatusCountFilter;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import de.rwth.idsg.steve.web.dto.Statistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.RegistrationStatus;
import org.joda.time.DateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.10.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChargePointService {

    private final UnidentifiedIncomingObjectService unknownChargePointService = new UnidentifiedIncomingObjectService(100);
    private final Striped<Lock> isRegisteredLocks = Striped.lock(16);

    private final ChargePointRepository chargePointRepository;
    private final GenericRepository genericRepository;
    private final SteveProperties steveProperties;
    private final PasswordEncoder passwordEncoder;

    // SOAP-based charge points are stored in DB with an endpoint address.
    // But, for WebSocket-based charge points, the active sessions are stored in memory.
    private final SessionContextStoreHolder sessionContextStoreHolder;

    public List<String> getChargeBoxIds() {
        return chargePointRepository.getChargeBoxIds();
    }

    public List<ChargePoint.Overview> getOverview(ChargePointQueryForm form) {
        return chargePointRepository.getOverview(form);
    }

    public ChargePoint.Details getDetails(int chargeBoxPk) {
        return chargePointRepository.getDetails(chargeBoxPk);
    }

    public List<Integer> getNonZeroConnectorIds(String chargeBoxId) {
        return chargePointRepository.getNonZeroConnectorIds(chargeBoxId);
    }

    public void addChargePointList(List<String> chargeBoxIdList) {
        chargePointRepository.addChargePointList(chargeBoxIdList);
    }

    public int addChargePoint(ChargePointForm form) {
        return chargePointRepository.addChargePoint(form);
    }

    public void updateChargePoint(ChargePointForm form) {
        chargePointRepository.updateChargePoint(form);
    }

    public void deleteChargePoint(int chargeBoxPk) {
        chargePointRepository.deleteChargePoint(chargeBoxPk);
    }

    // -------------------------------------------------------------------------
    // Unknown stations
    // -------------------------------------------------------------------------

    public List<UnidentifiedIncomingObject> getUnknownChargePoints() {
        return unknownChargePointService.getObjects();
    }

    public void removeUnknown(List<String> chargeBoxIdList) {
        unknownChargePointService.removeAll(chargeBoxIdList);
    }

    // -------------------------------------------------------------------------
    // Registration status
    // -------------------------------------------------------------------------

    public boolean validatePassword(String chargeBoxId, String passwordFromHeaders, String storedHashedPassword) {
        // if no password in DB, this station needs no validation
        if (StringUtils.isEmpty(storedHashedPassword)) {
            log.info("No password configured for charge point '{}' - authentication disabled", chargeBoxId);
            return true;
        }

        // there is a password in DB, but the station provided no password
        if (StringUtils.isEmpty(passwordFromHeaders)) {
            log.warn("Empty password provided for charge point '{}'", chargeBoxId);
            return false;
        }

        try {
            var matches = passwordEncoder.matches(passwordFromHeaders, storedHashedPassword);
            if (!matches) {
                log.warn("Invalid password attempt for charge point '{}'", chargeBoxId);
            }
            return matches;
        } catch (Exception e) {
            log.error("Exception while checking password for charge point '{}'", chargeBoxId, e);
            return false;
        }
    }

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

    private Optional<RegistrationStatus> getRegistrationStatusInternal(String chargeBoxId) {
        // 1. exit if already registered
        var status = chargePointRepository.getRegistration(chargeBoxId);
        if (status.isPresent()) {
            try {
                return Optional.ofNullable(RegistrationStatus.fromValue(status.get().registrationStatus()));
            } catch (Exception e) {
                // in cases where the database entry (string) is altered, and therefore cannot be converted to enum
                log.error("Exception happened", e);
                return Optional.empty();
            }
        }

        // 2. ok, this chargeBoxId is unknown. exit if auto-register is disabled
        if (!steveProperties.getOcpp().isAutoRegisterUnknownStations()) {
            return Optional.empty();
        }

        // 3. chargeBoxId is unknown and auto-register is enabled. insert chargeBoxId
        try {
            this.addChargePointList(Collections.singletonList(chargeBoxId));
            log.warn("Auto-registered unknown chargebox '{}'", chargeBoxId);
            return Optional.of(RegistrationStatus.ACCEPTED); // default db value is accepted
        } catch (Exception e) {
            log.error("Failed to auto-register unknown chargebox '{}'", chargeBoxId, e);
            return Optional.empty();
        }
    }

    // -------------------------------------------------------------------------
    // Connector stats and status
    // -------------------------------------------------------------------------

    public Statistics getStats() {
        Statistics stats = genericRepository.getStats();
        stats.setNumOcpp12JChargeBoxes(sessionContextStoreHolder.getOrCreate(OcppVersion.V_12).getNumberOfChargeBoxes());
        stats.setNumOcpp15JChargeBoxes(sessionContextStoreHolder.getOrCreate(OcppVersion.V_15).getNumberOfChargeBoxes());
        stats.setNumOcpp16JChargeBoxes(sessionContextStoreHolder.getOrCreate(OcppVersion.V_16).getNumberOfChargeBoxes());

        List<ConnectorStatus> latestList = chargePointRepository.getChargePointConnectorStatus(null);
        stats.setStatusCountMap(ConnectorStatusCountFilter.getStatusCountMap(latestList));

        return stats;
    }

    public List<ConnectorStatus> getChargePointConnectorStatus(ConnectorStatusForm params) {
        Set<String> connectedJsonChargeBoxIds = Arrays.stream(OcppVersion.values())
            .map(version -> sessionContextStoreHolder.getOrCreate(version).getChargeBoxIdList())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

        List<ConnectorStatus> latestList = chargePointRepository.getChargePointConnectorStatus(params);

        // iterate over JSON stations and mark disconnected ones
        // https://github.com/steve-community/steve/issues/355
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
        Map<String, Deque<SessionContext>> ocpp12Map = sessionContextStoreHolder.getOrCreate(OcppVersion.V_12).getACopy();
        Map<String, Deque<SessionContext>> ocpp15Map = sessionContextStoreHolder.getOrCreate(OcppVersion.V_15).getACopy();
        Map<String, Deque<SessionContext>> ocpp16Map = sessionContextStoreHolder.getOrCreate(OcppVersion.V_16).getACopy();

        List<String> connectedJsonChargeBoxIds = Stream.of(ocpp12Map, ocpp15Map, ocpp16Map)
            .map(Map::keySet)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        Map<String, Integer> primaryKeyLookup = chargePointRepository.getChargeBoxIdPkPair(connectedJsonChargeBoxIds);

        DateTime now = DateTime.now();
        List<OcppJsonStatus> returnList = new ArrayList<>();

        appendList(ocpp12Map, returnList, now, OcppVersion.V_12, primaryKeyLookup);
        appendList(ocpp15Map, returnList, now, OcppVersion.V_15, primaryKeyLookup);
        appendList(ocpp16Map, returnList, now, OcppVersion.V_16, primaryKeyLookup);
        return returnList;
    }

    public List<ChargePointSelect> getChargePoints(OcppVersion version) {
        return getChargePoints(version, Collections.singletonList(RegistrationStatus.ACCEPTED), Collections.emptyList());
    }

    public List<ChargePointSelect> getChargePoints(OcppVersion version, List<RegistrationStatus> inStatusFilter) {
        return getChargePoints(version, inStatusFilter, Collections.emptyList());
    }

    public List<ChargePointSelect> getChargePointsWithIds(OcppVersion version, List<String> chargeBoxIdFilter) {
        return getChargePoints(version, Collections.singletonList(RegistrationStatus.ACCEPTED), chargeBoxIdFilter);
    }

    private List<ChargePointSelect> getChargePoints(OcppVersion version, List<RegistrationStatus> inStatusFilter,
                                                    List<String> chargeBoxIdFilter) {
        List<ChargePointSelect> returnList =  new ArrayList<>();

        // soap stations
        {
            List<String> statusFilter = inStatusFilter.stream()
                .map(RegistrationStatus::value)
                .collect(Collectors.toList());

            var soapProtocol = version.toProtocol(OcppTransport.SOAP);

            returnList.addAll(chargePointRepository.getChargePointSelect(soapProtocol, statusFilter, chargeBoxIdFilter));
        }

        // json stations
        {
            SessionContextStore sessionStore = sessionContextStoreHolder.getOrCreate(version);

            List<String> chargeBoxIdList = CollectionUtils.isEmpty(chargeBoxIdFilter)
                ? sessionStore.getChargeBoxIdList()
                : sessionStore.getChargeBoxIdList().stream().filter(chargeBoxIdFilter::contains).toList();

            var jsonProtocol = version.toProtocol(OcppTransport.JSON);

            chargeBoxIdList.forEach(chargeBoxId -> returnList.add(new ChargePointSelect(jsonProtocol, chargeBoxId)));
        }

        return returnList;
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
