package net.parkl.ocpp.service.cs.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import lombok.SneakyThrows;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.module.esp.model.ESPMeterValues;
import net.parkl.ocpp.service.driver.DriverTestBase;


import ocpp.cs._2015._10.MeterValuesRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;

public class ESPMeterValuesParserTest extends DriverTestBase {
    @Autowired
    private ESPMeterValuesParser espMeterValuesParser;
    @Test
    @SneakyThrows
    void testNullMeasurandMeterValues() {
        InputStream is = getClass().getResourceAsStream("/metervalues.json");
        Assertions.assertNotNull(is);

        String json = new String(is.readAllBytes());

        ObjectMapper objectMapper = JsonObjectMapper.INSTANCE.getMapper();
        MeterValuesRequest request=objectMapper.readValue(json, MeterValuesRequest.class);
        Assertions.assertNotNull(request);

        TransactionStart transactionStart = new TransactionStart();
        ESPMeterValues espMeterValues = espMeterValuesParser.parseMeterValues(transactionStart, request.getMeterValue());
        Assertions.assertNotNull(espMeterValues);
    }
}
