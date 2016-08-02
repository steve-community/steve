package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12WebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15WebSocketEndpoint;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.GenericRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.utils.ConnectorStatusCountFilter;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import de.rwth.idsg.steve.web.dto.Statistics;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

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

    @Override
    public Statistics getStats() {
        Statistics stats = genericRepository.getStats();
        stats.setNumOcpp12JChargeBoxes(ocpp12WebSocketEndpoint.getNumberOfChargeBoxes());
        stats.setNumOcpp15JChargeBoxes(ocpp15WebSocketEndpoint.getNumberOfChargeBoxes());

        List<ConnectorStatus> latestList = chargePointRepository.getChargePointConnectorStatus();
        stats.setStatusCountMap(ConnectorStatusCountFilter.getStatusCountMap(latestList));

        return stats;
    }

    @Override
    public List<OcppJsonStatus> getOcppJsonStatus() {
        Map<String, Deque<SessionContext>> ocpp12Map = ocpp12WebSocketEndpoint.getACopy();
        Map<String, Deque<SessionContext>> ocpp15Map = ocpp15WebSocketEndpoint.getACopy();

        Map<String, Integer> primaryKeyLookup = getPrimaryKeyLookup(ocpp12Map, ocpp15Map);

        DateTime now = DateTime.now();
        List<OcppJsonStatus> returnList = new ArrayList<>();

        appendList(ocpp12Map, returnList, now, OcppVersion.V_12, primaryKeyLookup);
        appendList(ocpp15Map, returnList, now, OcppVersion.V_15, primaryKeyLookup);
        return returnList;
    }

    @Override
    public List<ChargePointSelect> getChargePointsV12() {
        List<ChargePointSelect> returnList = chargePointRepository.getChargePointSelect(OcppProtocol.V_12_SOAP);

        for (String chargeBoxId : ocpp12WebSocketEndpoint.getChargeBoxIdList()) {
            returnList.add(new ChargePointSelect(OcppTransport.JSON, chargeBoxId));
        }
        return returnList;
    }

    @Override
    public List<ChargePointSelect> getChargePointsV15() {
        List<ChargePointSelect> returnList = chargePointRepository.getChargePointSelect(OcppProtocol.V_15_SOAP);

        for (String chargeBoxId : ocpp15WebSocketEndpoint.getChargeBoxIdList()) {
            returnList.add(new ChargePointSelect(OcppTransport.JSON, chargeBoxId));
        }
        return returnList;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Map<String, Integer> getPrimaryKeyLookup(Map<String, Deque<SessionContext>> ocpp12Map,
                                                     Map<String, Deque<SessionContext>> ocpp15Map) {

        ArrayList<String> idList = new ArrayList<>(ocpp12Map.keySet());
        idList.addAll(ocpp15Map.keySet());

        return chargePointRepository.getChargeBoxIdPkPair(idList);
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
