package net.parkl.ocpp.service;

import de.rwth.idsg.steve.ocpp.ChargePointService15_Invoker;
import de.rwth.idsg.steve.ocpp.ChargePointService16_Invoker;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.ChangeConfigurationTask;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class HeartBeatService {

    private final ChargePointService15_Invoker service15Invoker;
    private final ChargePointService16_Invoker service16Invoker;
    private final ChargePointHelperService chargePointHelperService;
    private final int heartBeatIntervalInSecs;

    @Autowired
    public HeartBeatService(ChargePointService15_Invoker chargePointService15_InvokerImpl,
                            ChargePointService16_Invoker chargePointService16_InvokerImpl,
                            ChargePointHelperService chargePointHelperService,
                            @Value("${heartbeat.interval.secs:60}") int heartBeatIntervalInSecs) {

        this.service15Invoker = chargePointService15_InvokerImpl;
        this.service16Invoker = chargePointService16_InvokerImpl;
        this.chargePointHelperService = chargePointHelperService;
        this.heartBeatIntervalInSecs = heartBeatIntervalInSecs;
    }

    public void changeConfig(OcppProtocol ocppProtocol, String chargeBoxId) {
        List<ChargePointSelect> chargePoints;
        ChargePointSelect chargePointSelect;
        ChangeConfigurationTask task;

        if (ocppProtocol.getVersion() == OcppVersion.V_16) {
            log.info("Setting heartbeat interval secs to {} on OCPPv16", heartBeatIntervalInSecs);
            chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_16);
            chargePointSelect = filter(chargePoints, chargeBoxId);
            task = new ChangeConfigurationTask(OcppVersion.V_16, getParams(chargePointSelect));
            service16Invoker.changeConfiguration(chargePointSelect, task);
            log.info("Successfully changed heartbeat interval on OCPPv16");

        } else if (ocppProtocol.getVersion() == OcppVersion.V_15) {
            log.info("Setting heartbeat interval secs to {} on OCPPv15", heartBeatIntervalInSecs);
            chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_15);
            chargePointSelect = filter(chargePoints, chargeBoxId);
            task = new ChangeConfigurationTask(OcppVersion.V_15, getParams(chargePointSelect));
            service15Invoker.changeConfiguration(chargePointSelect, task);
            log.info("Successfully changed heartbeat interval on OCPPv15");
        }
    }

    private ChargePointSelect filter(List<ChargePointSelect> list, String chargeBoxId) {
        return list.stream().filter(cp -> cp.getChargeBoxId().equals(chargeBoxId)).findAny().orElse(null);
    }

    private ChangeConfigurationParams getParams(ChargePointSelect chargePointSelect) {
        ChangeConfigurationParams params = new ChangeConfigurationParams();
        params.setConfKey(ConfigurationKeyEnum.HeartBeatInterval.value());
        params.setValue(String.valueOf(heartBeatIntervalInSecs));
        params.setChargePointSelectList(Collections.singletonList(chargePointSelect));
        return params;
    }
}
