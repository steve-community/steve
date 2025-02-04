package net.parkl.ocpp.service.cs.factory;

import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import net.parkl.ocpp.entities.*;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static net.parkl.ocpp.entities.TransactionStopEventActor.manual;
import static net.parkl.ocpp.service.cs.factory.TransactionFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionFactoryUnitTest {
    @Test
    public void testCreateTransactionStop() {
        UpdateTransactionParams params = mock(UpdateTransactionParams.class);
        DateTime timestamp = new DateTime();
        TransactionStopEventActor stopEventActor = manual;
        String stopMeterValue = "2";
        String stopReason = "reason";

        when(params.getEventTimestamp()).thenReturn(timestamp);
        when(params.getEventActor()).thenReturn(stopEventActor);
        when(params.getStopTimestamp()).thenReturn(timestamp);
        when(params.getStopMeterValue()).thenReturn(stopMeterValue);
        when(params.getStopReason()).thenReturn(stopReason);

        TransactionStop transactionStop = createTransactionStop(params);

        assertThat(transactionStop)
                .isNotNull()
                .extracting("eventActor",
                            "stopTimestamp",
                            "stopValue",
                            "stopReason")
                .contains(stopEventActor,
                          timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                          stopMeterValue,
                          stopReason);
    }

    @Test
    public void testCreateTransactionStopFailed() {
        UpdateTransactionParams params = mock(UpdateTransactionParams.class);
        DateTime timestamp = new DateTime();
        TransactionStopEventActor stopEventActor = manual;
        String stopMeterValue = "2";
        String stopReason = "reason";
        Exception exception = mock(Exception.class);

        when(params.getEventTimestamp()).thenReturn(timestamp);
        when(params.getEventActor()).thenReturn(stopEventActor);
        when(params.getStopTimestamp()).thenReturn(timestamp);
        when(params.getStopMeterValue()).thenReturn(stopMeterValue);
        when(params.getStopReason()).thenReturn(stopReason);

        TransactionStopFailed transactionStop = createTransactionStopFailed(params, exception);

        assertThat(transactionStop)
                .isNotNull()
                .extracting("eventActor",
                            "stopTimestamp",
                            "stopValue",
                            "stopReason",
                            "failReason")
                .contains(stopEventActor,
                          timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                          stopMeterValue,
                          stopReason,
                          getStackTraceAsString(exception));
    }

    @Test
    public void testCreateTransactionStopId() {
        int transactionPk = 1;
        DateTime date = new DateTime();

        TransactionStopId transactionStopId = createTransactionStopId(transactionPk, date);

        assertThat(transactionStopId)
                .isNotNull()
                .extracting("transactionPk", "eventTimestamp")
                .containsExactly(transactionPk, date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    @Test
    public void testCreateTransactionStart() {
        Connector connector = mock(Connector.class);
        InsertTransactionParams params = mock(InsertTransactionParams.class);
        String ocppIdTag = "TAG";
        DateTime timeStamp = new DateTime();
        String startMeterValue = "1";

        when(params.getIdTag()).thenReturn(ocppIdTag);
        when(params.getStartTimestamp()).thenReturn(timeStamp);
        when(params.getStartMeterValue()).thenReturn(startMeterValue);
        when(params.getEventTimestamp()).thenReturn(timeStamp);

        TransactionStart transactionStart = createTransactionStart(connector, params);

        assertThat(transactionStart)
                .isNotNull()
                .extracting("connector", "ocppTag", "startTimestamp", "startValue", "eventTimestamp")
                .contains(connector, ocppIdTag, timeStamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), startMeterValue, timeStamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }
}
