package net.parkl.ocpp.repositories;

import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.OcppRemoteStart;
import net.parkl.ocpp.service.driver.DriverTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoteStartRepositoryTest extends DriverTestBase {
    @Autowired
    private OcppRemoteStartRepository ocppRemoteStartRepository;

    @Autowired
    private ConnectorRepository connectorRepository;

    private Connector connector1;
    private Connector connector2;
    private Connector connector3;

    private LocalDateTime date;

    private String idTag1 = "12345678";
    private String idTag2 = "987654321";

    @BeforeEach
    public void setUp() {
        connector1 = new Connector();
        connector1.setConnectorId(1);
        connector1.setChargeBoxId("chargeboxR1");
        connector1 = connectorRepository.save(connector1);

        connector2 = new Connector();
        connector2.setConnectorId(2);
        connector2.setChargeBoxId("chargeboxR1");
        connector2 = connectorRepository.save(connector2);

        connector3 = new Connector();
        connector3.setConnectorId(3);
        connector3.setChargeBoxId("chargeboxR2");
        connector3 = connectorRepository.save(connector3);

        OcppRemoteStart remoteStart1 = new OcppRemoteStart();
        remoteStart1.setConnector(connector1);
        remoteStart1.setOcppTag(idTag1);
        ocppRemoteStartRepository.save(remoteStart1);

        OcppRemoteStart remoteStart2 = new OcppRemoteStart();
        remoteStart2.setConnector(connector2);
        remoteStart2.setOcppTag(idTag1);
        ocppRemoteStartRepository.save(remoteStart2);

        OcppRemoteStart remoteStart3 = new OcppRemoteStart();
        remoteStart3.setConnector(connector3);
        remoteStart3.setOcppTag(idTag2);
        ocppRemoteStartRepository.save(remoteStart3);

        date = LocalDateTime.now().minusMinutes(1);
    }

    @AfterEach
    public void tearDown() {
        ocppRemoteStartRepository.deleteAll();
        connectorRepository.deleteAll();
    }

    @Test
    public void testCountByIdTagAndConnectors() {
        List<Connector> connectors = List.of(connector1, connector2);
        long count = ocppRemoteStartRepository
                .countByConnectorsAndOcppTagAfter(connectors,idTag1, date);

        assertEquals(2, count);
    }

    @Test
    public void testCountByIdTagAndConnectorsWithDifferentIdTag() {
        List<Connector> connectors = List.of(connector3);
        long count = ocppRemoteStartRepository
                .countByConnectorsAndOcppTagAfter(connectors,idTag2, date);

        assertEquals(1, count);
    }

    @Test
    public void testCountByIdTagAndConnectorsWithNoMatchingTag() {
        List<Connector> connectors = List.of(connector1, connector2);
        long count = ocppRemoteStartRepository
                .countByConnectorsAndOcppTagAfter(connectors,"ID_TAG_NON_EXISTENT", date);

        assertEquals(0, count);
    }
}
