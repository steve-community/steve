package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.repositories.TransactionRepository;
import net.parkl.ocpp.repositories.TransactionStartRepository;
import net.parkl.ocpp.repositories.TransactionStopFailedRepository;
import net.parkl.ocpp.repositories.TransactionStopRepository;
import net.parkl.ocpp.service.ChargingProcessService;
import net.parkl.ocpp.service.ESPNotificationService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static net.parkl.ocpp.entities.TransactionStopEventActor.station;
import static net.parkl.ocpp.service.OcppConstants.REASON_VEHICLE_CHARGED;
import static net.parkl.ocpp.service.OcppConstants.REASON_VEHICLE_NOT_CONNECTED;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceUnitTest {
    @InjectMocks
    private TransactionServiceImpl transactionService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionStopFailedRepository transactionStopFailedRepository;
    @Mock
    private TransactionStartRepository transactionStartRepository;
    @Mock
    private TransactionStopRepository transactionStopRepository;
    @Mock
    private ChargePointService chargePointService;
    @Mock
    private ESPNotificationService espNotificationService;
    @Mock
    private ChargingProcessService chargingProcessService;

    @Test
    public void updateTransactionNotifyAboutChargingStop() {
        int testTransactionId = 1;
        Transaction testTransaction = mock(Transaction.class);

        OcppChargingProcess chargingProcess = mock(OcppChargingProcess.class);

        String testChargeBoxId = "testChargeBox";

        UpdateTransactionParams params =
                UpdateTransactionParams.builder()
                        .chargeBoxId(testChargeBoxId)
                        .transactionId(testTransactionId)
                        .stopTimestamp(null)
                        .stopMeterValue(null)
                        .stopReason(REASON_VEHICLE_CHARGED)
                        .eventTimestamp(DateTime.now())
                        .eventActor(station)
                        .build();

        when(transactionRepository.findById(testTransactionId)).thenReturn(java.util.Optional.of(testTransaction));
        when(chargingProcessService.findByTransactionId(testTransactionId)).thenReturn(chargingProcess);
        when(chargePointService.shouldInsertConnectorStatusAfterTransactionMsg(testChargeBoxId)).thenReturn(false);
        when(testTransaction.vehicleUnplugged()).thenReturn(true);
        when(chargingProcess.stoppedExternally()).thenReturn(true);

        transactionService.updateTransaction(params);
        verify(espNotificationService, Mockito.times(1)).notifyAboutChargingStopped(chargingProcess);
    }

    @Test
    public void updateTransactionNotifyAboutConsumption() {
        int testTransactionId = 1;
        Transaction testTransaction = mock(Transaction.class);

        OcppChargingProcess chargingProcess = mock(OcppChargingProcess.class);

        String testChargeBoxId = "testChargeBox";

        UpdateTransactionParams params =
                UpdateTransactionParams.builder()
                        .chargeBoxId(testChargeBoxId)
                        .transactionId(testTransactionId)
                        .stopTimestamp(null)
                        .stopMeterValue(String.valueOf(500))
                        .stopReason(REASON_VEHICLE_NOT_CONNECTED)
                        .eventTimestamp(DateTime.now())
                        .eventActor(station)
                        .build();

        when(transactionRepository.findById(testTransactionId)).thenReturn(java.util.Optional.of(testTransaction));
        when(chargingProcessService.findByTransactionId(testTransactionId)).thenReturn(chargingProcess);
        when(chargePointService.shouldInsertConnectorStatusAfterTransactionMsg(testChargeBoxId)).thenReturn(false);
        when(testTransaction.vehicleUnplugged()).thenReturn(true);
        when(chargingProcess.stoppedExternally()).thenReturn(false);

        transactionService.updateTransaction(params);
        verify(espNotificationService, Mockito.times(1))
                .notifyAboutConsumptionUpdated(chargingProcess, testTransaction);
    }

    @Test
    public void updateTransactionStopFailed() {
        int testTransactionId = 1;
        Transaction testTransaction = mock(Transaction.class);

        OcppChargingProcess chargingProcess = mock(OcppChargingProcess.class);

        String testChargeBoxId = "testChargeBox";

        UpdateTransactionParams params =
                UpdateTransactionParams.builder()
                        .chargeBoxId(testChargeBoxId)
                        .transactionId(testTransactionId)
                        .stopTimestamp(null)
                        .stopMeterValue(String.valueOf(500))
                        .stopReason(REASON_VEHICLE_NOT_CONNECTED)
                        .eventTimestamp(DateTime.now())
                        .eventActor(station)
                        .build();

        when(transactionRepository.findById(testTransactionId)).thenReturn(java.util.Optional.of(testTransaction));
        when(chargingProcessService.findByTransactionId(testTransactionId)).thenReturn(chargingProcess);
        when(chargePointService.shouldInsertConnectorStatusAfterTransactionMsg(testChargeBoxId)).thenReturn(true);

        transactionService.updateTransaction(params);
        verify(transactionStopFailedRepository, Mockito.times(1)).save(any());
    }

}