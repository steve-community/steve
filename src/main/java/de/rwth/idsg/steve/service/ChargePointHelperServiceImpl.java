package de.rwth.idsg.steve.service;

import com.google.common.util.concurrent.Striped;
import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16WebSocketEndpoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import de.rwth.idsg.steve.web.dto.Statistics;
import net.parkl.ocpp.service.cs.ChargePointService;
import net.parkl.ocpp.service.cs.GenericService;
import ocpp.cs._2015._10.RegistrationStatus;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;


/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.03.2015
 */
@Service
public class ChargePointHelperServiceImpl implements ChargePointHelperService {
	private static final Logger log=LoggerFactory.getLogger(ChargePointHelperServiceImpl.class);

    //private final boolean autoRegisterUnknownStations = CONFIG.getOcpp().isAutoRegisterUnknownStations();
	@Autowired
	private SteveConfiguration config;

    private final Striped<Lock> isRegisteredLocks = Striped.lock(16);

    @Autowired private GenericService genericService;

    // SOAP-based charge points are stored in DB with an endpoint address
    @Autowired private ChargePointService chargePointService;

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
        Statistics stats = genericService.getStats();
        stats.setNumOcpp12JChargeBoxes(ocpp12WebSocketEndpoint.getNumberOfChargeBoxes());
        stats.setNumOcpp15JChargeBoxes(ocpp15WebSocketEndpoint.getNumberOfChargeBoxes());
        stats.setNumOcpp16JChargeBoxes(ocpp16WebSocketEndpoint.getNumberOfChargeBoxes());

        /*List<ConnectorStatus> latestList = chargePointService.getChargePointConnectorStatus();
        stats.setStatusCountMap(ConnectorStatusCountFilter.getStatusCountMap(latestList));*/

        return stats;
    }

    public List<OcppJsonStatus> getOcppJsonStatus() {
        Map<String, Deque<SessionContext>> ocpp12Map = ocpp12WebSocketEndpoint.getACopy();
        Map<String, Deque<SessionContext>> ocpp15Map = ocpp15WebSocketEndpoint.getACopy();
        Map<String, Deque<SessionContext>> ocpp16Map = ocpp16WebSocketEndpoint.getACopy();

        List<String> idList = extractIds(Arrays.asList(ocpp12Map, ocpp15Map, ocpp16Map));
        Map<String, Integer> primaryKeyLookup = chargePointService.getChargeBoxIdPkPair(idList);

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
        Optional<String> status = chargePointService.getRegistrationStatus(chargeBoxId);
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
        if (!config.isAutoRegisterUnknownStations()) {
            return Optional.empty();
        }

        // 3. chargeBoxId is unknown and auto-register is enabled. insert chargeBoxId
        try {
            chargePointService.addChargePointList(Collections.singletonList(chargeBoxId));
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

        List<ChargePointSelect> returnList = chargePointService.getChargePointSelect(protocol, statusFilter);
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
