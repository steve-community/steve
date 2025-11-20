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
import de.rwth.idsg.steve.ocpp.ws.SessionContextStoreHolder;
import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.GenericRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointRegistration;
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
import org.springframework.security.core.Authentication;
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

    public void updateCpoName(String chargeBoxId, String cpoName) {
        try {
            chargePointRepository.updateCpoName(chargeBoxId, cpoName);
        } catch (Exception e) {
            log.error("Failed during updateCpoName, because: {}", e.getMessage(), e);
        }
    }

    public void addChargePointList(List<String> chargeBoxIdList) {
        chargePointRepository.addChargePointList(chargeBoxIdList);
    }

    public int addChargePoint(ChargePointForm form) {
        encodePasswordIfNeeded(form);
        return chargePointRepository.addChargePoint(form);
    }

    public void updateChargePoint(ChargePointForm form) {
        encodePasswordIfNeeded(form);
        chargePointRepository.updateChargePoint(form);
    }

    public void deleteChargePoint(int chargeBoxPk) {
        var details = getDetails(chargeBoxPk);
        var chargeBoxId = details.getChargeBox().getChargeBoxId();

        chargePointRepository.deleteChargePoint(chargeBoxPk);
        log.info("Deleted charge point with chargeBoxPk={} and chargeBoxId={}", chargeBoxPk, chargeBoxId);

        // https://github.com/steve-community/steve/issues/1871
        var version = OcppProtocol.fromCompositeValue(details.getChargeBox().getOcppProtocol()).getVersion();
        log.info("Closing all WebSocket sessions of chargeBoxPk={} and chargeBoxId={}", chargeBoxPk, chargeBoxId);
        sessionContextStoreHolder.getOrCreate(version).closeSessions(chargeBoxId);
    }

    private void encodePasswordIfNeeded(ChargePointForm form) {
        String encodedPwd = StringUtils.isEmpty(form.getAuthPassword())
            ? null
            : passwordEncoder.encode(form.getAuthPassword());

        form.setAuthPassword(encodedPwd);
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

    public boolean validateBasicAuth(ChargePointRegistration registration, Authentication authFromRequest) {
        String chargeBoxId = registration.chargeBoxId();
        String encodedPassword = registration.hashedAuthPassword();

        if (authFromRequest == null) {
            log.warn("Failed to find username and password in Basic Authorization header for ChargeBoxId '{}'", chargeBoxId);
            return false;
        }

        // if no password in DB, we have a big configuration problem.
        if (StringUtils.isEmpty(encodedPassword)) {
            log.error("No password configured for ChargeBoxId '{}' - authentication misconfiguration", chargeBoxId);
            return false;
        }

        var username = (String) authFromRequest.getPrincipal();
        var rawPassword = (String) authFromRequest.getCredentials();

        if (!chargeBoxId.equals(username)) {
            log.warn("The username '{}' (in Basic Auth) is not matching the ChargeBoxId '{}'", username, chargeBoxId);
            return false;
        }

        // the station provided no password
        if (StringUtils.isEmpty(rawPassword)) {
            log.warn("Empty password provided for ChargeBoxId '{}'", chargeBoxId);
            return false;
        }

        try {
            var matches = passwordEncoder.matches(rawPassword, encodedPassword);
            if (!matches) {
                log.warn("Invalid password attempt for ChargeBoxId '{}'", chargeBoxId);
            }
            return matches;
        } catch (Exception e) {
            log.error("Exception while checking password for ChargeBoxId '{}'", chargeBoxId, e);
            return false;
        }
    }

    public Optional<ChargePointRegistration> getRegistrationDirect(String chargeBoxId) {
        return chargePointRepository.getRegistration(chargeBoxId);
    }

    public Optional<RegistrationStatus> getRegistrationStatus(String chargeBoxId) {
        return chargePointRepository.getRegistration(chargeBoxId)
            .map(it -> RegistrationStatus.fromValue(it.registrationStatus()));
    }

    public Optional<ChargePointRegistration> getRegistration(String chargeBoxId) {
        Lock l = isRegisteredLocks.get(chargeBoxId);
        l.lock();
        try {
            Optional<ChargePointRegistration> entry = getRegistrationInternal(chargeBoxId);
            if (entry.isEmpty()) {
                unknownChargePointService.processNewUnidentified(chargeBoxId);
            }
            return entry;
        } finally {
            l.unlock();
        }
    }

    private Optional<ChargePointRegistration> getRegistrationInternal(String chargeBoxId) {
        // 1. exit if already registered
        var entry = chargePointRepository.getRegistration(chargeBoxId);
        if (entry.isPresent()) {
            return entry;
        }

        // 2. ok, this chargeBoxId is unknown. exit if auto-register is disabled
        if (!steveProperties.getOcpp().isAutoRegisterUnknownStations()) {
            return Optional.empty();
        }

        // 3. chargeBoxId is unknown and auto-register is enabled. insert chargeBoxId
        try {
            this.addChargePointList(Collections.singletonList(chargeBoxId));
            log.warn("Auto-registered unknown chargebox '{}'", chargeBoxId);
            return chargePointRepository.getRegistration(chargeBoxId);
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

        var latestList = chargePointRepository.getChargePointConnectorStatus(null);
        stats.setStatusCountMap(ConnectorStatusCountFilter.getStatusCountMap(latestList));

        return stats;
    }

    public List<ConnectorStatus> getChargePointConnectorStatus(ConnectorStatusForm params) {
        var connectedJsonChargeBoxIds = Arrays.stream(OcppVersion.values())
            .map(version -> sessionContextStoreHolder.getOrCreate(version).getChargeBoxIdList())
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

        List<ConnectorStatus> latestList = chargePointRepository.getChargePointConnectorStatus(params);

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

    public List<OcppJsonStatus> getOcppJsonStatus() {
        var ocpp12Map = sessionContextStoreHolder.getOrCreate(OcppVersion.V_12).getACopy();
        var ocpp15Map = sessionContextStoreHolder.getOrCreate(OcppVersion.V_15).getACopy();
        var ocpp16Map = sessionContextStoreHolder.getOrCreate(OcppVersion.V_16).getACopy();

        var connectedJsonChargeBoxIds = Stream.of(ocpp12Map, ocpp15Map, ocpp16Map)
            .map(Map::keySet)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        var primaryKeyLookup = chargePointRepository.getChargeBoxIdPkPair(connectedJsonChargeBoxIds);

        var now = DateTime.now();
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

    public List<ChargePointSelect> getChargePoints(OcppProtocol protocol,
                                                   List<RegistrationStatus> inStatusFilter,
                                                   List<String> chargeBoxIdFilter) {
        var version = protocol.getVersion();
        var transport = protocol.getTransport();

        switch (transport) {
            case SOAP -> {
                var statusFilter = inStatusFilter.stream()
                    .map(RegistrationStatus::value)
                    .collect(Collectors.toList());

                var soapProtocol = version.toProtocol(OcppTransport.SOAP);
                return chargePointRepository.getChargePointSelect(soapProtocol, statusFilter, chargeBoxIdFilter);
            }
            case JSON -> {
                var sessionStore = sessionContextStoreHolder.getOrCreate(version);

                var chargeBoxIdList = CollectionUtils.isEmpty(chargeBoxIdFilter)
                    ? sessionStore.getChargeBoxIdList()
                    : sessionStore.getChargeBoxIdList().stream().filter(chargeBoxIdFilter::contains).toList();

                var jsonProtocol = version.toProtocol(OcppTransport.JSON);

                return chargeBoxIdList.stream()
                    .map(chargeBoxId -> new ChargePointSelect(jsonProtocol, chargeBoxId))
                    .toList();
            }
            default -> throw new IllegalStateException("Unexpected value: " + transport);
        }
    }

    private List<ChargePointSelect> getChargePoints(OcppVersion version,
                                                    List<RegistrationStatus> inStatusFilter,
                                                    List<String> chargeBoxIdFilter) {
        List<ChargePointSelect> returnList =  new ArrayList<>();
        returnList.addAll(getChargePoints(version.toProtocol(OcppTransport.SOAP), inStatusFilter, chargeBoxIdFilter));
        returnList.addAll(getChargePoints(version.toProtocol(OcppTransport.JSON), inStatusFilter, chargeBoxIdFilter));
        return returnList;
    }

    private static void appendList(Map<String, Deque<SessionContext>> map, List<OcppJsonStatus> returnList,
                                   DateTime now, OcppVersion version, Map<String, Integer> primaryKeyLookup) {

        for (var entry : map.entrySet()) {
            var chargeBoxId = entry.getKey();
            var endpointDeque = entry.getValue();

            for (var ctx : endpointDeque) {
                DateTime openSince = ctx.getOpenSince();

                Integer chargeBoxPk = primaryKeyLookup.get(chargeBoxId);
                if (chargeBoxPk == null) {
                    log.warn("Could not find chargeBoxPk for chargeBoxId={}", chargeBoxId);
                }

                OcppJsonStatus status = OcppJsonStatus.builder()
                    .chargeBoxPk(chargeBoxPk)
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
