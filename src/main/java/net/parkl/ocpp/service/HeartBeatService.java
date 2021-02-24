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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class HeartBeatService {


    private final ChargePointService15_Invoker service15Invoker;
    private final ChargePointService16_Invoker service16Invoker;
    private final ChargePointHelperService chargePointHelperService;

    @Value("${heartbeat.interval.secs}")
    private int heartBeatIntervalInSecs;

    @Autowired
    public HeartBeatService(ChargePointService15_Invoker chargePointService15_InvokerImpl,
                            ChargePointService16_Invoker chargePointService16_InvokerImpl,
                            ChargePointHelperService chargePointHelperService) {
        this.service15Invoker = chargePointService15_InvokerImpl;
        this.service16Invoker = chargePointService16_InvokerImpl;
        this.chargePointHelperService = chargePointHelperService;
    }

    public void changeConfig(OcppProtocol ocppProtocol, String chargeBoxId) {
        List<ChargePointSelect> chargePoints;
        ChargePointSelect chargePointSelect;
        ChangeConfigurationTask task;

        if (ocppProtocol.getVersion() == OcppVersion.V_16) {
            chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_16);
            chargePointSelect = filter(chargePoints, chargeBoxId);
            task = new ChangeConfigurationTask(OcppVersion.V_16, getParams(chargePointSelect));
            service16Invoker.changeConfiguration(chargePointSelect, task);

        } else if (ocppProtocol.getVersion() == OcppVersion.V_15) {
            chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_15);
            chargePointSelect = filter(chargePoints, chargeBoxId);
            task = new ChangeConfigurationTask(OcppVersion.V_15, getParams(chargePointSelect));
            service15Invoker.changeConfiguration(chargePointSelect, task);
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
