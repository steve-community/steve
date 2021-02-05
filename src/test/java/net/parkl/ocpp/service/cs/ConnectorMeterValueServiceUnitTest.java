package net.parkl.ocpp.service.cs;

import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.repositories.ConnectorMeterValueRepository;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.SampledValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static ocpp.cs._2015._10.Measurand.POWER_ACTIVE_IMPORT;
import static ocpp.cs._2015._10.UnitOfMeasure.W;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConnectorMeterValueServiceUnitTest {
    @InjectMocks
    private ConnectorMeterValueService connectorMeterValueService;
    @Mock
    private ConnectorMeterValueRepository repository;

    @Test
    public void insertMeterValues() {
        MeterValue testMeterValue = new MeterValue();

        int transactionPk = 1;
        TransactionStart testTransactionStart = new TransactionStart();
        testTransactionStart.setTransactionPk(transactionPk);

        SampledValue testValue1 = new SampledValue();
        testValue1.setValue(String.valueOf(11000));
        testValue1.setMeasurand(POWER_ACTIVE_IMPORT);
        testValue1.setUnit(W);

        testMeterValue = testMeterValue.withSampledValue(singletonList(testValue1));

        connectorMeterValueService.insertMeterValues(singletonList(testMeterValue), testTransactionStart);

        verify(repository, times(1)).save(any());
    }
}