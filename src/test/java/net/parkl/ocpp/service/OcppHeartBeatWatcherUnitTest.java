package net.parkl.ocpp.service;

import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.repositories.OcppChargeBoxRepository;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.task.TaskExecutor;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

class OcppHeartBeatWatcherUnitTest {
    private OcppHeartBeatWatcher heartBeatWatcher;
    private AdvancedChargeBoxConfiguration advancedChargeBoxConfiguration;
    private TaskExecutor taskExecutor;
    private OcppChargeBoxRepository chargeBoxRepository;

    @BeforeEach
    public void setUp() {
        advancedChargeBoxConfiguration = Mockito.mock(AdvancedChargeBoxConfiguration.class);
        taskExecutor = Mockito.mock(TaskExecutor.class);
        chargeBoxRepository = Mockito.mock(OcppChargeBoxRepository.class);

        heartBeatWatcher = new OcppHeartBeatWatcher(taskExecutor,
                                                    null,
                                                    chargeBoxRepository,
                                                    advancedChargeBoxConfiguration);
    }

    @Test
    void sendHeartbeatAlert() {
        OcppChargeBox testBox = new OcppChargeBox();
        testBox.setChargeBoxId("TestBox");
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MINUTE, -10);
        Date tenMinsBefore = date.getTime();
        testBox.setLastHeartbeatTimestamp(tenMinsBefore);

        when(advancedChargeBoxConfiguration.getChargeBoxesForAlert()).thenReturn(Collections.singletonList(testBox.getChargeBoxId()));
        when(chargeBoxRepository.findByChargeBoxIdIn(any())).thenReturn(List.of(testBox));
        heartBeatWatcher.watch();
        verify(taskExecutor, times(1)).execute(any());
    }
}
