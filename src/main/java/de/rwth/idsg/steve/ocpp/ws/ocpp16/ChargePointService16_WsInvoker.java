/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.ocpp.ws.ocpp16;

import de.rwth.idsg.steve.ocpp.ws.AbstractChargePointServiceInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @author david
 */

@Component
public class ChargePointService16_WsInvoker extends AbstractChargePointServiceInvoker
{
    @Autowired private Ocpp16TypeStore typeStore;
    @Autowired private Ocpp16WebSocketEndpoint endpoint;

    @PostConstruct
    public void init() {
        super.setTypeStore(typeStore);
        super.setEndpoint(endpoint);
    }
}
