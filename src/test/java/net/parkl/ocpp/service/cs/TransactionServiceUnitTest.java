package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.Transaction;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.repositories.TransactionRepository;
import net.parkl.ocpp.repositories.TransactionStartRepository;
import net.parkl.ocpp.repositories.TransactionStopFailedRepository;
import net.parkl.ocpp.repositories.TransactionStopRepository;
import net.parkl.ocpp.service.ChargingProcessService;
import net.parkl.ocpp.service.ESPNotificationService;
import net.parkl.ocpp.util.AsyncWaiter;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static net.parkl.ocpp.entities.TransactionStopEventActor.station;
import static net.parkl.ocpp.service.OcppConstants.REASON_VEHICLE_CHARGED;
import static net.parkl.ocpp.service.OcppConstants.REASON_VEHICLE_NOT_CONNECTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
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
    @Mock
    private ConnectorService connectorService;
    @Mock
    private ReservationService reservationService;

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
        TransactionStart testTransactionStart = mock(TransactionStart.class);

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
        when(chargingProcess.getTransactionStart()).thenReturn(testTransactionStart);
        when(testTransactionStart.getStartValue()).thenReturn("0");
        when(chargePointService.shouldInsertConnectorStatusAfterTransactionMsg(testChargeBoxId)).thenReturn(false);
        when(testTransaction.vehicleUnplugged()).thenReturn(true);
        when(chargingProcess.stoppedExternally()).thenReturn(false);

        transactionService.updateTransaction(params);
        verify(espNotificationService, Mockito.times(1))
                .notifyAboutConsumptionUpdated(chargingProcess, "0", params.getStopMeterValue());
    }

    @Test
    public void updateTransactionStopFailed() {
        int testTransactionId = 1;
        Transaction testTransaction = mock(Transaction.class);
        TransactionStart testTransactionStart = mock(TransactionStart.class);

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
        when(chargingProcess.getTransactionStart()).thenReturn(testTransactionStart);
        when(chargePointService.shouldInsertConnectorStatusAfterTransactionMsg(testChargeBoxId)).thenReturn(true);

        transactionService.updateTransaction(params);
        verify(transactionStopFailedRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void insertTransactionWillReturnExisting() {
        String testChargeBoxId = "chargeBox";
        int connectorId = 1;
        String testRfidTag = "myRfidTag";

        InsertTransactionParams params =
                InsertTransactionParams.builder()
                        .chargeBoxId(testChargeBoxId)
                        .connectorId(connectorId)
                        .idTag(testRfidTag)
                        .startTimestamp(DateTime.now())
                        .startMeterValue(String.valueOf(0))
                        .reservationId(null)
                        .eventTimestamp(DateTime.now())
                        .build();

        Connector testConnector = mock(Connector.class);
        when(connectorService.createConnectorIfNotExists(testChargeBoxId, connectorId)).thenReturn(testConnector);

        int testTransactionPk = 1;
        TransactionStart existing = new TransactionStart();
        existing.setTransactionPk(testTransactionPk);
        when(transactionStartRepository.findByConnectorAndIdTagAndStartValues(testConnector,
                testRfidTag,
                params.getStartTimestamp().toDate(),
                params.getStartMeterValue()))
                .thenReturn(existing);

        assertThat(transactionService.insertTransaction(params)).isEqualTo(testTransactionPk);
    }

    @Test
    public void insertTransactionWithoutActiveChargingProcess() {
        String testChargeBoxId = "chargeBox";
        int connectorId = 1;
        String testRfidTag = "myRfidTag";

        InsertTransactionParams params =
                InsertTransactionParams.builder()
                        .chargeBoxId(testChargeBoxId)
                        .connectorId(connectorId)
                        .idTag(testRfidTag)
                        .startTimestamp(DateTime.now())
                        .startMeterValue(String.valueOf(0))
                        .reservationId(null)
                        .eventTimestamp(DateTime.now())
                        .build();

        Connector testConnector = mock(Connector.class);
        when(connectorService.createConnectorIfNotExists(testChargeBoxId, connectorId)).thenReturn(testConnector);

        int testTransactionPk = 2;
        TransactionStart transactionStart = new TransactionStart();
        transactionStart.setTransactionPk(testTransactionPk);

        when(transactionStartRepository.save(any())).thenReturn(transactionStart);

        assertThat(transactionService.insertTransaction(params)).isEqualTo(testTransactionPk);
    }

    @Test
    public void insertTransactionWithChargingProcess() {
        String testChargeBoxId = "chargeBox";
        int connectorId = 1;
        String testRfidTag = "myRfidTag";

        InsertTransactionParams params =
                InsertTransactionParams.builder()
                        .chargeBoxId(testChargeBoxId)
                        .connectorId(connectorId)
                        .idTag(testRfidTag)
                        .startTimestamp(DateTime.now())
                        .startMeterValue(String.valueOf(0))
                        .reservationId(null)
                        .eventTimestamp(DateTime.now())
                        .build();

        Connector testConnector = mock(Connector.class);
        when(connectorService.createConnectorIfNotExists(testChargeBoxId, connectorId)).thenReturn(testConnector);

        int testTransactionPk = 2;
        TransactionStart transactionStart = new TransactionStart();
        transactionStart.setTransactionPk(testTransactionPk);

        when(transactionStartRepository.save(any())).thenReturn(transactionStart);

        OcppChargingProcess testChargingProcess = new OcppChargingProcess();
        when(chargingProcessService.fetchChargingProcess(anyInt(),
                anyString(),
                any(AsyncWaiter.class)))
                .thenReturn(testChargingProcess);

        assertThat(transactionService.insertTransaction(params)).isEqualTo(testChargingProcess.getTransactionStart().getTransactionPk());
    }

    @Test
    public void updateTransactionInvalidTransaction() {
        int testTransactionId = 1;
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

        transactionService.updateTransaction(params);
        verify(transactionStopFailedRepository, Mockito.times(0)).save(any());
    }

    @Test
    public void updateTransactionNoChargingProcessForTransaction() {
        int testTransactionId = 1;
        Transaction testTransaction = mock(Transaction.class);
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

        transactionService.updateTransaction(params);
        verify(transactionStopFailedRepository, Mockito.times(0)).save(any());
    }
}