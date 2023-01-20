package net.parkl.ocpp.service.cs;

import net.parkl.ocpp.entities.OcppReservation;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.repositories.OcppReservationRepository;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplUnitTest {
    @InjectMocks
    private ReservationServiceImpl reservationService;
    @Mock
    private AdvancedChargeBoxConfiguration config;
    @Mock
    private OcppReservationRepository reservationRepository;

    @Test
    public void markReservationAsUsedWithoutAdvancedConfig() {
        TransactionStart testTransactionStart = mock(TransactionStart.class);
        int reservationId = 32;
        String testChargeBoxId = "chargeBoxId";

        when(config.checkReservationId(testChargeBoxId)).thenReturn(false);

        reservationService.markReservationAsUsed(testTransactionStart, reservationId, testChargeBoxId);

        verify(reservationRepository, times(0)).save(any());
    }

    @Test
    public void markReservationAsUsedWithAdvancedConfig() {
        TransactionStart testTransactionStart = mock(TransactionStart.class);
        int reservationId = 32;
        String testChargeBoxId = "chargeBoxId";
        OcppReservation testReservation = mock(OcppReservation.class);

        when(config.checkReservationId(testChargeBoxId)).thenReturn(true);
        when(reservationRepository.findById(reservationId)).thenReturn(java.util.Optional.ofNullable(testReservation));

        reservationService.markReservationAsUsed(testTransactionStart, reservationId, testChargeBoxId);

        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    public void markReservationAsUsedThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class,() -> {
            TransactionStart testTransactionStart = mock(TransactionStart.class);
            int reservationId = 32;
            String testChargeBoxId = "chargeBoxId";

            when(config.checkReservationId(testChargeBoxId)).thenReturn(true);

            reservationService.markReservationAsUsed(testTransactionStart, reservationId, testChargeBoxId);
        });

    }
}