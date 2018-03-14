package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.ChargePointService12_Invoker;
import de.rwth.idsg.steve.ocpp.ChargePointService15_Invoker;
import de.rwth.idsg.steve.ocpp.ChargePointService16_InvokerImpl;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
@Slf4j
@Service
@Qualifier("ChargePointService16_Client")
public class ChargePointService16_Client extends ChargePointService15_Client {

    @Autowired private ChargePointService16_InvokerImpl invoker16;

    @Override
    protected OcppVersion getVersion() {
        return OcppVersion.V_16;
    }

    @Override
    protected ChargePointService12_Invoker getOcpp12Invoker() {
        return invoker16;
    }

    protected ChargePointService15_Invoker getOcpp15Invoker() {
        return invoker16;
    }

}
