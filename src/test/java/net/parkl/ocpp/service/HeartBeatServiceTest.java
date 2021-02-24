package net.parkl.ocpp.service;

import de.rwth.idsg.steve.ocpp.*;
import de.rwth.idsg.steve.ocpp.task.ChangeConfigurationTask;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HeartBeatServiceTest {

    @InjectMocks
    private HeartBeatService heartBeatService;

    @Mock
    private ChargePointHelperService chargePointHelperService;

    @Mock
    private ChargePointService15_InvokerImpl service15Invoker;

    @Mock
    private ChargePointService16_InvokerImpl service16Invoker;

    @Captor
    ArgumentCaptor<ChangeConfigurationTask> argumentCaptor;

    private static final int heartBeatInterval = 60;

    @Test
    public void changeConfig15() {
        ReflectionTestUtils.setField(heartBeatService, "heartBeatIntervalInSecs", heartBeatInterval);

        String chargeBoxId = "ID15";
        List<ChargePointSelect> chargePoints = new ArrayList<>();
        ChargePointSelect chargePointSelect = new ChargePointSelect(OcppTransport.JSON, chargeBoxId);
        chargePoints.add(chargePointSelect);

        when(chargePointHelperService.getChargePoints(OcppVersion.V_15)).thenReturn(chargePoints);

        heartBeatService.changeConfig(OcppProtocol.V_15_JSON, chargeBoxId);

        verify(service15Invoker).changeConfiguration(any(ChargePointSelect.class), argumentCaptor.capture());

        assertEquals(argumentCaptor.getValue().getParams().getKey(), ConfigurationKeyEnum.HeartBeatInterval.value());
        assertEquals(argumentCaptor.getValue().getParams().getValue(), String.valueOf(heartBeatInterval));
    }

    @Test
    public void changeConfig16() {
        ReflectionTestUtils.setField(heartBeatService, "heartBeatIntervalInSecs", heartBeatInterval);

        String chargeBoxId = "ID16";
        List<ChargePointSelect> chargePoints = new ArrayList<>();
        ChargePointSelect chargePointSelect = new ChargePointSelect(OcppTransport.JSON, chargeBoxId);
        chargePoints.add(chargePointSelect);

        when(chargePointHelperService.getChargePoints(OcppVersion.V_16)).thenReturn(chargePoints);

        heartBeatService.changeConfig(OcppProtocol.V_16_JSON, chargeBoxId);

        verify(service16Invoker).changeConfiguration(any(ChargePointSelect.class), argumentCaptor.capture());

        assertEquals(argumentCaptor.getValue().getParams().getKey(), ConfigurationKeyEnum.HeartBeatInterval.value());
        assertEquals(argumentCaptor.getValue().getParams().getValue(), String.valueOf(heartBeatInterval));
    }
}