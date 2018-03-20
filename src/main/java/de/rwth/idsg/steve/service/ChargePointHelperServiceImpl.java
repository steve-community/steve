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
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.utils.ConnectorStatusCountFilter;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import de.rwth.idsg.steve.web.dto.Statistics;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.03.2015
 */
@Service
public class ChargePointHelperServiceImpl implements ChargePointHelperService {

    @Autowired private GenericRepository genericRepository;

    // SOAP-based charge points are stored in DB with an endpoint address
    @Autowired private ChargePointRepository chargePointRepository;

    // For WebSocket-based charge points, the active sessions are stored in memory
    @Autowired private Ocpp12WebSocketEndpoint ocpp12WebSocketEndpoint;
    @Autowired private Ocpp15WebSocketEndpoint ocpp15WebSocketEndpoint;
    @Autowired private Ocpp16WebSocketEndpoint ocpp16WebSocketEndpoint;

    private final UnidentifiedIncomingObjectService unknownChargePointService = new UnidentifiedIncomingObjectService(100);

    @Override
    public Statistics getStats() {
        Statistics stats = genericRepository.getStats();
        stats.setNumOcpp12JChargeBoxes(ocpp12WebSocketEndpoint.getNumberOfChargeBoxes());
        stats.setNumOcpp15JChargeBoxes(ocpp15WebSocketEndpoint.getNumberOfChargeBoxes());
        stats.setNumOcpp16JChargeBoxes(ocpp16WebSocketEndpoint.getNumberOfChargeBoxes());

        List<ConnectorStatus> latestList = chargePointRepository.getChargePointConnectorStatus();
        stats.setStatusCountMap(ConnectorStatusCountFilter.getStatusCountMap(latestList));

        return stats;
    }

    @Override
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

    @Override
    public List<ChargePointSelect> getChargePointsV12() {
        return getChargePoints(OcppProtocol.V_12_SOAP, ocpp12WebSocketEndpoint);
    }

    @Override
    public List<ChargePointSelect> getChargePointsV15() {
        return getChargePoints(OcppProtocol.V_15_SOAP, ocpp15WebSocketEndpoint);
    }

    @Override
    public List<ChargePointSelect> getChargePointsV16() {
        return getChargePoints(OcppProtocol.V_16_SOAP, ocpp16WebSocketEndpoint);
    }

    @Override
    public void rememberNewUnknown(String chargeBoxId) {
        unknownChargePointService.processNewUnidentified(chargeBoxId);
    }

    @Override
    public List<UnidentifiedIncomingObject> getUnknownChargePoints() {
        return unknownChargePointService.getObjects();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private List<ChargePointSelect> getChargePoints(OcppProtocol forSoap, AbstractWebSocketEndpoint jsonEndpoint) {
        List<ChargePointSelect> returnList = chargePointRepository.getChargePointSelect(forSoap);
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
