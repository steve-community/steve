package net.parkl.ocpp.service;

import de.rwth.idsg.steve.ocpp.ChargePointService15_InvokerImpl;
import de.rwth.idsg.steve.ocpp.ChargePointService16_InvokerImpl;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.ChangeConfigurationTask;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.HeartBeatInterval;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HeartBeatServiceUnitTest {

    private static final int heartBeatInterval = 60;

    @Test
    public void changeConfig15() {
        ChargePointHelperService mockChargePointHelperService = mock(ChargePointHelperService.class);
        ChargePointService15_InvokerImpl mockChargePointService15_invoker = mock(ChargePointService15_InvokerImpl.class);
        ChargePointService16_InvokerImpl mockChargePointService16_invoker = mock(ChargePointService16_InvokerImpl.class);

        HeartBeatService heartBeatService =
                new HeartBeatService(mockChargePointService15_invoker,
                        mockChargePointService16_invoker,
                        mockChargePointHelperService,
                        heartBeatInterval);

        String chargeBoxId = "ID15";
        List<ChargePointSelect> chargePoints = new ArrayList<>();
        ChargePointSelect chargePointSelect = new ChargePointSelect(OcppTransport.JSON, chargeBoxId);
        chargePoints.add(chargePointSelect);

        when(mockChargePointHelperService.getChargePoints(OcppVersion.V_15)).thenReturn(chargePoints);

        heartBeatService.changeConfig(OcppProtocol.V_15_JSON, chargeBoxId);

        ArgumentCaptor<ChangeConfigurationTask> argumentCaptor = forClass(ChangeConfigurationTask.class);

        verify(mockChargePointService15_invoker).changeConfiguration(any(ChargePointSelect.class), argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getParams().getKey()).isEqualTo(HeartBeatInterval.value());
        assertThat(argumentCaptor.getValue().getParams().getValue()).isEqualTo(String.valueOf(heartBeatInterval));
    }

    @Test
    public void changeConfig16() {
        ChargePointHelperService mockChargePointHelperService = mock(ChargePointHelperService.class);
        ChargePointService15_InvokerImpl mockChargePointService15_invoker = mock(ChargePointService15_InvokerImpl.class);
        ChargePointService16_InvokerImpl mockChargePointService16_invoker = mock(ChargePointService16_InvokerImpl.class);

        HeartBeatService heartBeatService =
                new HeartBeatService(mockChargePointService15_invoker,
                        mockChargePointService16_invoker,
                        mockChargePointHelperService,
                        heartBeatInterval);


        String chargeBoxId = "ID16";
        List<ChargePointSelect> chargePoints = new ArrayList<>();
        ChargePointSelect chargePointSelect = new ChargePointSelect(OcppTransport.JSON, chargeBoxId);
        chargePoints.add(chargePointSelect);

        when(mockChargePointHelperService.getChargePoints(OcppVersion.V_16)).thenReturn(chargePoints);

        heartBeatService.changeConfig(OcppProtocol.V_16_JSON, chargeBoxId);

        ArgumentCaptor<ChangeConfigurationTask> argumentCaptor = forClass(ChangeConfigurationTask.class);

        verify(mockChargePointService16_invoker).changeConfiguration(any(ChargePointSelect.class), argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getParams().getKey()).isEqualTo(HeartBeatInterval.value());
        assertThat(argumentCaptor.getValue().getParams().getValue()).isEqualTo(String.valueOf(heartBeatInterval));
    }
}