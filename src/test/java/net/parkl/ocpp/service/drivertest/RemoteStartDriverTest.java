package net.parkl.ocpp.service.drivertest;

import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.service.ChargingProcessService;
import net.parkl.ocpp.service.RemoteStartService;
import net.parkl.ocpp.service.cs.TransactionService;
import net.parkl.ocpp.service.driver.ChargeBoxDriver;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static de.rwth.idsg.steve.ocpp.OcppProtocol.V_16_JSON;

@Slf4j
class RemoteStartDriverTest extends DriverTestBase {
    private ChargeBoxDriver chargeBoxDriver;

    @Autowired
    private RemoteStartService remoteStartService;
    @Autowired
    private ChargingProcessService chargingProcessService;
    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        chargeBoxDriver = driverFactory.createChargeBoxDriver();
    }
    @Test
    void testRemoteStart() {
        String chargeBoxId = "testRemoteStartConfirmationFirst";
        String idTag="TEST-ID-TAG";
        chargeBoxDriver.withName(chargeBoxId)
                .withProtocol(V_16_JSON)
                .withConnectors(2)
                .createChargeBox();

        remoteStartService.remoteStartRequested(chargeBoxId, 1,
                idTag);

        OcppChargingProcess process = chargingProcessService.createChargingProcess(chargeBoxId,
                1,
                idTag,
                null,null,null);
        log.info("Charging process created: {}", process.getOcppChargingProcessId());

        log.info("Starting transaction on {}...", chargeBoxId);
        InsertTransactionParams params =
                InsertTransactionParams.builder()
                        .chargeBoxId(chargeBoxId)
                        .connectorId(1)
                        .idTag(idTag)
                        .startTimestamp(DateTime.now())
                        .startMeterValue("100")
                        .eventTimestamp(DateTime.now())
                        .build();
        Integer transactionPk = transactionService.insertTransaction(params);
        Assertions.assertNotNull(transactionPk);
        log.info("Transaction started: {}", transactionPk);
    }

}
