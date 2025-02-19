package net.parkl.ocpp.service;

import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.repositories.OcppChargeBoxRepository;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.service.middleware.OcppNotificationMiddleware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.task.TaskExecutor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class OcppHeartBeatWatcherUnitTest {
    private OcppHeartBeatWatcher heartBeatWatcher;
    private AdvancedChargeBoxConfiguration advancedChargeBoxConfiguration;
    private TaskExecutor taskExecutor;
    private OcppChargeBoxRepository chargeBoxRepository;
    private OcppNotificationMiddleware notificationMiddleware;

    @BeforeEach
    public void setUp() {
        advancedChargeBoxConfiguration = Mockito.mock(AdvancedChargeBoxConfiguration.class);
        taskExecutor = Mockito.mock(TaskExecutor.class);
        chargeBoxRepository = Mockito.mock(OcppChargeBoxRepository.class);
        notificationMiddleware = Mockito.mock(OcppNotificationMiddleware.class);

        heartBeatWatcher = new OcppHeartBeatWatcher(taskExecutor,
                                                    notificationMiddleware,
                                                    chargeBoxRepository,
                                                    advancedChargeBoxConfiguration);
    }

    @Test
    void sendHeartbeatAlert() {
        OcppChargeBox testBox = new OcppChargeBox();
        LocalDateTime tenMinsBefore = LocalDateTime.now().minusMinutes(10);
        testBox.setLastHeartbeatTimestamp(tenMinsBefore);

        when(advancedChargeBoxConfiguration.getChargeBoxesForAlert()).thenReturn(Collections.singletonList(testBox.getChargeBoxId()));
        when(chargeBoxRepository.findByChargeBoxIdIn(any())).thenReturn(List.of(testBox));
        heartBeatWatcher.watch();
        verify(notificationMiddleware, times(1)).sendHeartBeatOfflineAlert(any());
    }
}
