package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.03.2015
 */
public interface FutureResponseContextStore {
    void addChargeBox(String chargeBoxId);
    void removeChargeBox(String chargeBoxId);
    void add(String chargeBoxId, String messageId, FutureResponseContext context);
    FutureResponseContext get(String chargeBoxId, String messageId);
}
