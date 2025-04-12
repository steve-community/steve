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

import java.time.ZoneId;
import java.util.UUID;

import static net.parkl.ocpp.entities.TransactionStopEventActor.station;
import static net.parkl.ocpp.service.OcppConstants.REASON_VEHICLE_CHARGED;
import static net.parkl.ocpp.service.OcppConstants.REASON_VEHICLE_NOT_CONNECTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
class TransactionServiceUnitTest {
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
    void updateTransactionNotifyAboutChargingStop() {
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
    void updateTransactionNotifyAboutConsumption() {
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
    void updateTransactionStopFailed() {
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

        when(chargingProcess.getTransactionStart()).thenReturn(testTransactionStart);
        when(chargePointService.shouldInsertConnectorStatusAfterTransactionMsg(testChargeBoxId)).thenReturn(true);

        transactionService.updateTransaction(params);
        verify(transactionStopFailedRepository, Mockito.times(1)).save(any());
    }

    @Test
    void insertTransactionWillReturnExisting() {
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
                params.getStartTimestamp().toDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                params.getStartMeterValue()))
                .thenReturn(existing);

        assertThat(transactionService.insertTransaction(params)).isEqualTo(testTransactionPk);
    }

    @Test
    void insertTransactionWithChargingProcessUpdate() {
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

        OcppChargingProcess chargingProcess = new OcppChargingProcess();
        chargingProcess.setTransactionStart(transactionStart);
        chargingProcess.setConnector(testConnector);
        chargingProcess.setOcppChargingProcessId(UUID.randomUUID().toString());

        when(chargingProcessService.updateChargingProcessWithTransaction(anyString(), anyInt(), anyString(), any())).thenReturn(chargingProcess);

        when(transactionStartRepository.save(any())).thenReturn(transactionStart);
        assertThat(transactionService.insertTransaction(params)).isEqualTo(testTransactionPk);
    }



    @Test
    void updateTransactionInvalidTransaction() {
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
    void updateTransactionNoChargingProcessForTransaction() {
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
