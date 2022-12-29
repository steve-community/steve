package net.parkl.ocpp.service;

import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OcppHeartBeatWatcherUnitTest {
    @InjectMocks
    private OcppHeartBeatWatcher heartBeatWatcher;
    @Mock
    private AdvancedChargeBoxConfiguration advancedChargeBoxConfiguration;
    @Mock
    private TaskExecutor taskExecutor;

    @Test
    public void sendHeartbeatAlert() {
        OcppChargeBox testBox = mock(OcppChargeBox.class);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MINUTE, -10);
        Date tenMinsBefore = date.getTime();

        when(advancedChargeBoxConfiguration.getChargeBoxesForAlert()).thenReturn(Collections.singletonList(testBox));
        when(testBox.getChargeBoxId()).thenReturn("TestBox");
        when(testBox.getLastHeartbeatTimestamp()).thenReturn(tenMinsBefore);
        heartBeatWatcher.watch();
        verify(taskExecutor, times(1)).execute(any());
    }
}
