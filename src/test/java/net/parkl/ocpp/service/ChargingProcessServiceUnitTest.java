package net.parkl.ocpp.service;

import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.OcppChargingProcessRepository;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.util.AsyncWaiter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChargingProcessServiceUnitTest {
    @InjectMocks
    private ChargingProcessService chargingProcessService;
    @Mock
    private AdvancedChargeBoxConfiguration advancedConfig;
    @Mock
    private ConnectorRepository connectorRepo;
    @Mock
    private OcppChargingProcessRepository chargingProcessRepo;

    @Test
    public void fetchChargingProcessWithoutWaiting() {
        String chargeBoxId = "testChargeBoxId";
        int connectorId = 1;

        String chargingProcessId = "testChargingProcessId";
        OcppChargingProcess testChargingProcess = mock(OcppChargingProcess.class);
        testChargingProcess.setOcppChargingProcessId(chargingProcessId);

        when(advancedConfig.waitingForChargingProcessEnabled(chargeBoxId)).thenReturn(false);

        Connector testConnector = new Connector();
        when(connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId)).thenReturn(testConnector);

        @SuppressWarnings("unchecked") AsyncWaiter<OcppChargingProcess> waiter = mock(AsyncWaiter.class);
        chargingProcessService.fetchChargingProcess(connectorId, chargeBoxId, waiter);

        verify(connectorRepo, Mockito.times(1)).findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        verify(chargingProcessRepo, Mockito.times(1)).findByConnectorAndTransactionStartIsNullAndEndDateIsNull(testConnector);
        verify(waiter, Mockito.never()).waitFor(Mockito.any());
    }

    @Test
    public void fetchNullChargingProcessWithWait() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            String chargeBoxId = "testChargeBoxId";
            int connectorId = 1;

            String chargingProcessId = "testChargingProcessId";
            OcppChargingProcess testChargingProcess = mock(OcppChargingProcess.class);
            testChargingProcess.setOcppChargingProcessId(chargingProcessId);

            when(advancedConfig.waitingForChargingProcessEnabled(chargeBoxId)).thenReturn(true);

            Connector testConnector = new Connector();
            when(connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId)).thenReturn(testConnector);

            @SuppressWarnings("unchecked") AsyncWaiter<OcppChargingProcess> waiter = mock(AsyncWaiter.class);
            chargingProcessService.fetchChargingProcess(connectorId, chargeBoxId, waiter);

            verify(connectorRepo, Mockito.times(1)).findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
            verify(chargingProcessRepo, Mockito.times(1)).findByConnectorAndTransactionStartIsNullAndEndDateIsNull(testConnector);
        });

    }

    @Test
    public void fetchChargingProcessWithWait() {
        String chargeBoxId = "testChargeBoxId";
        int connectorId = 1;

        String chargingProcessId = "testChargingProcessId";
        OcppChargingProcess testChargingProcess = mock(OcppChargingProcess.class);
        testChargingProcess.setOcppChargingProcessId(chargingProcessId);

        when(advancedConfig.waitingForChargingProcessEnabled(chargeBoxId)).thenReturn(true);

        Connector testConnector = mock(Connector.class);
        when(connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId)).thenReturn(testConnector);

        @SuppressWarnings("unchecked") AsyncWaiter<OcppChargingProcess> waiter = mock(AsyncWaiter.class);
        when(waiter.waitFor(Mockito.any())).thenReturn(testChargingProcess);

        chargingProcessService.fetchChargingProcess(connectorId, chargeBoxId, waiter);

        verify(connectorRepo, Mockito.times(1)).findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        verify(waiter, Mockito.times(1)).waitFor(Mockito.any());
    }
}