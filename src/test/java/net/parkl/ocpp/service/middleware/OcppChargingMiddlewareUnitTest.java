package net.parkl.ocpp.service.middleware;

import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.module.esp.EmobilityServiceProvider;
import net.parkl.ocpp.module.esp.model.ESPChargingStopRequest;
import net.parkl.ocpp.service.OcppConsumptionHelper;
import net.parkl.ocpp.service.cs.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static net.parkl.ocpp.service.OcppConstants.REASON_VEHICLE_CHARGED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OcppChargingMiddlewareUnitTest {

    @InjectMocks
    private OcppChargingMiddleware middleware;

    @Mock
    private TransactionService transactionService;
    @Mock
    private EmobilityServiceProvider emobilityServiceProvider;
    @Mock
    private OcppConsumptionHelper consumptionHelper;

    @Test
    public void stopChargingExternal_convertsReasonFromCamelCaseToUpperSnakeCase() {
        OcppChargingProcess process = mock(OcppChargingProcess.class);
        TransactionStart transactionStart = mock(TransactionStart.class);
        Transaction transaction = mock(Transaction.class);

        when(process.getOcppChargingProcessId()).thenReturn("proc-1");
        when(process.getTransactionStart()).thenReturn(transactionStart);
        when(transactionStart.getTransactionPk()).thenReturn(42);
        when(transactionService.findTransaction(42)).thenReturn(Optional.of(transaction));

        middleware.stopChargingExternal(process, "ConnectorLockFailure");

        ArgumentCaptor<ESPChargingStopRequest> captor = ArgumentCaptor.forClass(ESPChargingStopRequest.class);
        verify(emobilityServiceProvider).stopChargingExternal(captor.capture());
        assertThat(captor.getValue().getEventCode()).isEqualTo("CONNECTOR_LOCK_FAILURE");
    }

    @Test
    public void stopChargingExternal_convertsTransactionReasonFromCamelCaseToUpperSnakeCase() {
        OcppChargingProcess process = mock(OcppChargingProcess.class);
        TransactionStart transactionStart = mock(TransactionStart.class);
        Transaction transaction = mock(Transaction.class);

        when(process.getOcppChargingProcessId()).thenReturn("proc-1");
        when(process.getTransactionStart()).thenReturn(transactionStart);
        when(transactionStart.getTransactionPk()).thenReturn(42);
        when(transaction.getStopReason()).thenReturn("EVDisconnected");
        when(transactionService.findTransaction(42)).thenReturn(Optional.of(transaction));

        middleware.stopChargingExternal(process, REASON_VEHICLE_CHARGED);

        ArgumentCaptor<ESPChargingStopRequest> captor = ArgumentCaptor.forClass(ESPChargingStopRequest.class);
        verify(emobilityServiceProvider).stopChargingExternal(captor.capture());
        assertThat(captor.getValue().getEventCode()).isEqualTo("EV_DISCONNECTED");
    }

    @Test
    public void stopChargingExternal_keepsOriginalReason_whenStopReasonIsNull() {
        OcppChargingProcess process = mock(OcppChargingProcess.class);
        TransactionStart transactionStart = mock(TransactionStart.class);
        Transaction transaction = mock(Transaction.class);

        when(process.getOcppChargingProcessId()).thenReturn("proc-2");
        when(process.getTransactionStart()).thenReturn(transactionStart);
        when(transactionStart.getTransactionPk()).thenReturn(43);
        when(transaction.getStopReason()).thenReturn(null);
        when(transactionService.findTransaction(43)).thenReturn(Optional.of(transaction));

        middleware.stopChargingExternal(process, REASON_VEHICLE_CHARGED);

        ArgumentCaptor<ESPChargingStopRequest> captor = ArgumentCaptor.forClass(ESPChargingStopRequest.class);
        verify(emobilityServiceProvider).stopChargingExternal(captor.capture());
        assertThat(captor.getValue().getEventCode()).isEqualTo(REASON_VEHICLE_CHARGED);
    }
}
