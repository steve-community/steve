package net.parkl.ocpp.service.cs;

import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.ConnectorMeterValue;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.repositories.ConnectorMeterValueRepository;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.TransactionStartRepository;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MeterValueServiceIntegrationTest extends DriverTestBase {

    @Autowired
    private ConnectorMeterValueService meterValueService;

    @Autowired
    private ConnectorMeterValueRepository connectorMeterValueRepository;

    @Autowired
    private TransactionStartRepository transactionStartRepository;

    @Autowired
    private ConnectorRepository connectorRepository;

    @Test
    public void testFindByTransactionAndMeasurands() {
        Connector connector = new Connector();
        connector.setConnectorId(1);
        connector.setChargeBoxId("chargeBoxId");
        connector = connectorRepository.save(connector);


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTimePlusOneMinute = now.plusMinutes(1);

        LocalDateTime dateNow = LocalDateTime.now();
        LocalDateTime datePlusOneMinute = now.plusMinutes(1);

        TransactionStart transactionStart = new TransactionStart();
        transactionStart.setConnector(connector);
        transactionStart.setStartTimestamp(dateNow);
        transactionStart.setEventTimestamp(dateNow);
        transactionStart.setOcppTag("ocppTag");
        transactionStart = transactionStartRepository.save(transactionStart);

        ConnectorMeterValue energyValue = new ConnectorMeterValue();
        energyValue.setConnector(connector);
        energyValue.setTransaction(transactionStart);
        energyValue.setValueTimestamp(dateNow);
        energyValue.setMeasurand("Energy.Active.Import.Register");
        energyValue.setValue("500");
        energyValue.setUnit("Wh");
        connectorMeterValueRepository.save(energyValue);

        ConnectorMeterValue powerValue = new ConnectorMeterValue();
        powerValue.setConnector(connector);
        powerValue.setTransaction(transactionStart);
        powerValue.setValueTimestamp(dateNow);
        powerValue.setMeasurand("Power.Active.Import");
        powerValue.setValue("20");
        powerValue.setUnit("kW");
        connectorMeterValueRepository.save(powerValue);

        ConnectorMeterValue energyValue2 = new ConnectorMeterValue();
        energyValue2.setConnector(connector);
        energyValue2.setTransaction(transactionStart);
        energyValue2.setValueTimestamp(datePlusOneMinute);
        energyValue2.setMeasurand("Energy.Active.Import.Register");
        energyValue2.setValue("600");
        energyValue2.setUnit("Wh");
        connectorMeterValueRepository.save(energyValue2);

        ConnectorMeterValue powerValue2 = new ConnectorMeterValue();
        powerValue2.setConnector(connector);
        powerValue2.setTransaction(transactionStart);
        powerValue2.setValueTimestamp(datePlusOneMinute);
        powerValue2.setMeasurand("Power.Active.Import");
        powerValue2.setValue("15");
        powerValue2.setUnit("kW");
        connectorMeterValueRepository.save(powerValue2);

        ConnectorMeterValue socValue = new ConnectorMeterValue();
        socValue.setConnector(connector);
        socValue.setTransaction(transactionStart);
        socValue.setValueTimestamp(datePlusOneMinute);
        socValue.setMeasurand("SoC");
        socValue.setValue("45");
        connectorMeterValueRepository.save(socValue);

        ChargingMeterValueDtoList result = meterValueService.findByTransaction(transactionStart);

        assertNotNull(result);
        List<ChargingMeterValueDto> meterValues = result.getMeterValues();
        assertEquals(2, meterValues.size());

        ChargingMeterValueDto meterValueDto = meterValues.get(0);
        assertEquals("500", meterValueDto.getEnergy());
        assertEquals("Wh", meterValueDto.getEnergyUnit());
        assertEquals("20", meterValueDto.getPower());
        assertEquals("kW", meterValueDto.getPowerUnit());
        assertNull(meterValueDto.getSoc());

        ChargingMeterValueDto meterValueDto2 = meterValues.get(1);
        assertEquals("600", meterValueDto2.getEnergy());
        assertEquals("Wh", meterValueDto2.getEnergyUnit());
        assertEquals("15", meterValueDto2.getPower());
        assertEquals("kW", meterValueDto2.getPowerUnit());
        assertEquals("45", meterValueDto2.getSoc());
    }

}
