package de.rwth.idsg.steve.ocpp.ws.cluster;

import de.rwth.idsg.steve.ocpp.ws.ChargePointServiceInvoker;

public class ClusteredWebSocketHelper {
    public static ClusterCommunicationMode getClusterClientCommunicationMode(ClusteredWebSocketConfig clusteredWebSocketConfig,
                                                                             ChargePointServiceInvoker wsHelper, String chargeBoxId) {
        if (!isLocal(clusteredWebSocketConfig, wsHelper,chargeBoxId)) {
            return ClusterCommunicationMode.REMOTE_CLIENT;
        }
        return null;

    }
    public static boolean isLocal(ClusteredWebSocketConfig clusteredWebSocketConfig,
                                  ChargePointServiceInvoker wsHelper, String chargeBoxId) {
        if (!clusteredWebSocketConfig.isClusteredWebSocketSessionEnabled()) {
            return true;
        }
        return wsHelper.getEndpoint().containsLocalSession(chargeBoxId);
    }

    public static String getPodIp() {
        return System.getenv("POD_IP");
    }
}
