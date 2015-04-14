package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import de.rwth.idsg.steve.ocpp.ws.AbstractChargePointServiceInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2015
 */
@Component
public class ChargePointService15_WsInvoker extends AbstractChargePointServiceInvoker {

    @Autowired private Ocpp15TypeStore typeStore;
    @Autowired private Ocpp15WebSocketEndpoint endpoint;

    @PostConstruct
    public void init() {
        super.setTypeStore(typeStore);
        super.setEndpoint(endpoint);
    }
}
