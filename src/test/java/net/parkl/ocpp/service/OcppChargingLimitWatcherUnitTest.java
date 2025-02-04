package net.parkl.ocpp.service;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.service.middleware.OcppChargingMiddleware;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.util.Arrays.asList;
import static java.util.Date.from;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class OcppChargingLimitWatcherUnitTest {
    @InjectMocks
    private OcppChargingLimitWatcher limitWatcher;
    @Mock
    private ChargingProcessService chargingProcessService;
    @Mock
    private OcppChargingMiddleware chargingMiddleware;
    @Mock
    private TaskExecutor executor;

    @Test
    public void testStopKwhLimitProcess() {
        OcppChargingProcess chargingProcessBeforeLimit = mock(OcppChargingProcess.class);
        String beforeLimitProcessId = "beforeKwhLimitId";
        TransactionStart transactionStartBeforeLimit = mock(TransactionStart.class);
        PowerValue powerValueBeforeLimit = mock(PowerValue.class);

        OcppChargingProcess chargingProcessAfterLimit1 = mock(OcppChargingProcess.class);
        String afterLimitProcessId1 = "afterKwhLimitId1";
        TransactionStart transactionStartAfterLimit1 = mock(TransactionStart.class);
        PowerValue powerValueAfterLimit1 = mock(PowerValue.class);

        OcppChargingProcess chargingProcessAfterLimit2 = mock(OcppChargingProcess.class);
        String afterLimitProcessId2 = "afterKwhLimitId2";
        TransactionStart transactionStartAfterLimit2 = mock(TransactionStart.class);
        PowerValue powerValueAfterLimit2 = mock(PowerValue.class);

        when(chargingProcessService.findOpenChargingProcessesWithLimitKwh())
                .thenReturn(asList(chargingProcessBeforeLimit, chargingProcessAfterLimit1, chargingProcessAfterLimit2));

        when(chargingProcessBeforeLimit.getOcppChargingProcessId()).thenReturn(beforeLimitProcessId);
        when(transactionStartBeforeLimit.getTransactionPk()).thenReturn(1);
        when(chargingProcessBeforeLimit.getTransactionStart()).thenReturn(transactionStartBeforeLimit);
        when(chargingMiddleware.getPowerValue(chargingProcessBeforeLimit.getTransactionStart().getTransactionPk()))
                .thenReturn(powerValueBeforeLimit);
        when(powerValueBeforeLimit.getValue()).thenReturn(2000f);
        when(powerValueBeforeLimit.getUnit()).thenReturn(OcppConstants.UNIT_WH);
        when(chargingProcessBeforeLimit.getLimitKwh()).thenReturn(3f);

        when(chargingProcessAfterLimit1.getOcppChargingProcessId()).thenReturn(afterLimitProcessId1);
        when(transactionStartAfterLimit1.getTransactionPk()).thenReturn(2);
        when(chargingProcessAfterLimit1.getTransactionStart()).thenReturn(transactionStartAfterLimit1);
        when(chargingMiddleware.getPowerValue(chargingProcessAfterLimit1.getTransactionStart().getTransactionPk()))
                .thenReturn(powerValueAfterLimit1);
        when(powerValueAfterLimit1.getValue()).thenReturn(3100f);
        when(powerValueAfterLimit1.getUnit()).thenReturn(OcppConstants.UNIT_WH);
        when(chargingProcessAfterLimit1.getLimitKwh()).thenReturn(3f);

        when(chargingProcessAfterLimit2.getOcppChargingProcessId()).thenReturn(afterLimitProcessId2);
        when(transactionStartAfterLimit2.getTransactionPk()).thenReturn(3);
        when(chargingProcessAfterLimit2.getTransactionStart()).thenReturn(transactionStartAfterLimit2);
        when(chargingMiddleware.getPowerValue(chargingProcessAfterLimit2.getTransactionStart().getTransactionPk()))
                .thenReturn(powerValueAfterLimit2);
        when(powerValueAfterLimit2.getValue()).thenReturn(3050f);
        when(powerValueAfterLimit2.getUnit()).thenReturn(OcppConstants.UNIT_WH);
        when(chargingProcessAfterLimit1.getLimitKwh()).thenReturn(3f);

        limitWatcher.checkKwhChargingLimit();

        verify(executor, times(2)).execute(any(Runnable.class));
    }

    @Test
    public void testStopMinuteLimitProcess() {
        OcppChargingProcess chargingProcessBeforeLimit = mock(OcppChargingProcess.class);
        String beforeLimitProcessId = "beforeTimeLimitId";
        TransactionStart transactionStartBeforeLimit = mock(TransactionStart.class);

        OcppChargingProcess chargingProcessAfterLimit = mock(OcppChargingProcess.class);
        String afterLimitProcessId = "afterTimeLimitId1";
        TransactionStart transactionStartAfterLimit = mock(TransactionStart.class);
        PowerValue powerValueAfterLimit = mock(PowerValue.class);

        when(chargingProcessService.findOpenChargingProcessesWithLimitMinute())
                .thenReturn(asList(chargingProcessBeforeLimit, chargingProcessAfterLimit));

        when(chargingProcessBeforeLimit.getOcppChargingProcessId()).thenReturn(beforeLimitProcessId);
        when(chargingProcessBeforeLimit.getTransactionStart()).thenReturn(transactionStartBeforeLimit);
        when(chargingProcessBeforeLimit.getLimitMinute()).thenReturn(30);
        when(chargingProcessBeforeLimit.getStartDate()).thenReturn(LocalDateTime.now().minusMinutes(10));

        when(chargingProcessAfterLimit.getOcppChargingProcessId()).thenReturn(afterLimitProcessId);
        when(chargingProcessAfterLimit.getTransactionStart()).thenReturn(transactionStartAfterLimit);
        when(chargingMiddleware.getPowerValue(chargingProcessAfterLimit.getTransactionStart().getTransactionPk()))
                .thenReturn(powerValueAfterLimit);
        when(powerValueAfterLimit.getValue()).thenReturn(3100f);
        when(powerValueAfterLimit.getUnit()).thenReturn(OcppConstants.UNIT_WH);
        when(chargingProcessAfterLimit.getLimitMinute()).thenReturn(30);
        when(chargingProcessAfterLimit.getStartDate()).thenReturn(LocalDateTime.now().minusMinutes(40));

        limitWatcher.checkMinuteChargingLimit();

        verify(executor, times(1)).execute(any(Runnable.class));
    }
}
