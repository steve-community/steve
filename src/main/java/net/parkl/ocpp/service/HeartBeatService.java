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
import ocpp.cs._2015._10.BootNotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeartBeatService {

    private final ChargePointService15_Invoker service15Invoker;
    private final ChargePointService16_Invoker service16Invoker;
    private final ChargePointHelperService chargePointHelperService;

    @Autowired
    public HeartBeatService(ChargePointService15_Invoker service15Invoker,
                            ChargePointService16_Invoker service16Invoker,
                            ChargePointHelperService chargePointHelperService) {
        this.service15Invoker = service15Invoker;
        this.service16Invoker = service16Invoker;
        this.chargePointHelperService = chargePointHelperService;
    }

    public void changeConfig(OcppProtocol ocppProtocol, String chargeBoxId, BootNotificationRequest bootNotificationRequest) {
        List<ChargePointSelect> chargePoints;
        ChargePointSelect chargePointSelect;
        ChangeConfigurationParams params = new ChangeConfigurationParams();
        ChangeConfigurationTask task;

        if (ocppProtocol.getVersion() == OcppVersion.V_16) {
            chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_16);
            chargePointSelect = filter(chargePoints, chargeBoxId);

            params.setConfKey(ConfigurationKeyEnum.HeartBeatInterval.value());
            params.setValue("60");
            task = new ChangeConfigurationTask(OcppVersion.V_16, params);
            service16Invoker.changeConfiguration(chargePointSelect, task);
        } else if (ocppProtocol.getVersion() == OcppVersion.V_15) {
            chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_15);
            chargePointSelect = filter(chargePoints, chargeBoxId);

            params.setConfKey(ConfigurationKeyEnum.HeartBeatInterval.value());
            params.setValue("60");
            task = new ChangeConfigurationTask(OcppVersion.V_15, params);
            service15Invoker.changeConfiguration(chargePointSelect, task);
        }
    }

    private ChargePointSelect filter(List<ChargePointSelect> list, String chargeBoxId) {
        return list.stream().filter(cp -> cp.getChargeBoxId().equals(chargeBoxId)).findAny().orElse(null);
    }
}
