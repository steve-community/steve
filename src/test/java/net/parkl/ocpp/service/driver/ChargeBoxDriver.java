package net.parkl.ocpp.service.driver;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import lombok.NoArgsConstructor;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.service.cs.ChargePointService;
import net.parkl.ocpp.service.cs.ConnectorService;
import net.parkl.ocpp.service.middleware.OcppChargePointMiddleware;
import org.joda.time.DateTime;

import java.util.List;

@NoArgsConstructor
public class ChargeBoxDriver {
    private OcppChargePointMiddleware chargePointMiddleware;
    private ChargePointService chargePointService;
    private ConnectorService connectorService;

    private String name;
    private OcppProtocol protocol;
    private int connectors;

    public static ChargeBoxDriver createChargeBoxDriver(OcppChargePointMiddleware chargePointMiddleware,
                                                        ChargePointService chargePointService,
                                                        ConnectorService connectorService) {
        ChargeBoxDriver driver = new ChargeBoxDriver();
        driver.chargePointMiddleware = chargePointMiddleware;
        driver.chargePointService = chargePointService;
        driver.connectorService = connectorService;
        return driver;
    }

    public void createChargeBox() {
        chargePointMiddleware.registerChargeBox(name);

        UpdateChargeboxParams params = UpdateChargeboxParams.builder()
                .chargeBoxId(name)
                .ocppProtocol(protocol)
                .heartbeatTimestamp(new DateTime())
                .build();
        chargePointService.updateChargebox(params);
        chargePointService.updateEndpointAddress(name, "http://localhost:8081/ocpp-charger/ws");

        for (int i = 1; i <= connectors; i++) {
            InsertConnectorStatusParams p2 = InsertConnectorStatusParams.builder()
                    .chargeBoxId(name)
                    .connectorId(i)
                    .status("Available")
                    .build();
            connectorService.insertConnectorStatus(p2);
        }
    }

    public ChargeBoxDriver withName(String name) {
        this.name = name;
        return this;
    }

    public ChargeBoxDriver withProtocol(OcppProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public ChargeBoxDriver withConnectors(int connectors) {
        this.connectors = connectors;
        return this;
    }

    public void deleteAllChargePoints() {
        List<OcppChargeBox> chargeBoxList = chargePointService.getAllChargeBoxes();
        chargeBoxList.forEach(c->chargePointService.deleteChargePoint(c.getChargeBoxPk()));
    }

}
