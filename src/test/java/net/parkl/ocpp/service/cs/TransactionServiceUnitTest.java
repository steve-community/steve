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
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import org.assertj.core.api.Assertions;
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
    @Mock
    private ConnectorService connectorService;
    @Mock
    private OcppIdTagService ocppIdTagService;
    @Mock
    private ReservationService reservationService;
    @Mock
    private AdvancedChargeBoxConfiguration advancedConfig;

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

        Assertions.assertThat(transactionService.insertTransaction(params)).isEqualTo(testTransactionPk);
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
        when(transactionStartRepository.findByConnectorAndIdTagAndStartValues(testConnector,
                testRfidTag,
                params.getStartTimestamp().toDate(),
                params.getStartMeterValue()))
                .thenReturn(null);
        when(transactionStartRepository.save(any())).thenReturn(transactionStart);

        Assertions.assertThat(transactionService.insertTransaction(params)).isEqualTo(testTransactionPk);
    }


}