package net.parkl.ocpp.service;

import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OcppStartTimeoutManagerUnitTest {
    @InjectMocks
    private OcppStartTimeoutManager startTimeoutManager;
    @Mock
    private AdvancedChargeBoxConfiguration advancedChargeBoxConfiguration;
    @Mock
    private ChargingProcessService chargingProcessService;
    @Mock
    private OcppMiddleware ocppMiddleware;

    @Test
    public void checkForStartTimeoutNoEnabledCharger() {
        when(advancedChargeBoxConfiguration.isStartTimeoutEnabledForAny()).thenReturn(false);

        startTimeoutManager.checkForStartTimeout();

        verify(chargingProcessService, times(0)).findOpenChargingProcessesWithoutTransaction();
    }

    @Test
    public void checkForStartTimeout() {
        OcppChargingProcess chargingProcess = mock(OcppChargingProcess.class);
        Connector connector = mock(Connector.class);
        Date startDate = Date.from(now().minusMinutes(1).atZone(systemDefault()).toInstant());

        when(advancedChargeBoxConfiguration.isStartTimeoutEnabledForAny()).thenReturn(true);
        when(chargingProcessService.findOpenChargingProcessesWithoutTransaction())
                .thenReturn(singletonList(chargingProcess));
        when(chargingProcess.getConnector()).thenReturn(connector);
        when(connector.getChargeBoxId()).thenReturn("chargeBox1");
        when(advancedChargeBoxConfiguration.isStartTimeoutEnabled(chargingProcess.getConnector().getChargeBoxId()))
                .thenReturn(true);
        when(chargingProcess.getStartDate()).thenReturn(startDate);
        when(advancedChargeBoxConfiguration.getStartTimeoutSecs(chargingProcess.getConnector().getChargeBoxId()))
                .thenReturn(30);

        startTimeoutManager.checkForStartTimeout();

        verify(ocppMiddleware, times(2)).changeAvailability(anyString(), anyString(), anyBoolean());
    }

    @Test
    public void checkForPreparingTimeout() {
        OcppChargingProcess chargingProcess = mock(OcppChargingProcess.class);
        String chargingProcessId = "id1";
        Connector connector = mock(Connector.class);
        Date startDate = Date.from(now().minusMinutes(1).atZone(systemDefault()).toInstant());

        when(advancedChargeBoxConfiguration.isPreparingTimeoutEnabledForAny()).thenReturn(true);
        when(chargingProcessService.findOpenChargingProcessesWithoutTransaction())
                .thenReturn(singletonList(chargingProcess));
        when(chargingProcess.getOcppChargingProcessId()).thenReturn(chargingProcessId);
        when(chargingProcess.getConnector()).thenReturn(connector);
        when(connector.getChargeBoxId()).thenReturn("chargeBox1");
        when(advancedChargeBoxConfiguration.isPreparingTimeoutEnabled(chargingProcess.getConnector().getChargeBoxId()))
                .thenReturn(true);
        when(chargingProcess.getStartDate()).thenReturn(startDate);
        when(advancedChargeBoxConfiguration.getPreparingTimeoutSecs(chargingProcess.getConnector().getChargeBoxId()))
                .thenReturn(30);

        startTimeoutManager.checkForStartTimeout();

        verify(ocppMiddleware, times(1)).stopChargingWithPreparingTimeout(chargingProcessId);
    }
}