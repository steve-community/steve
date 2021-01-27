package net.parkl.ocpp.service.driver;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import lombok.NoArgsConstructor;
import net.parkl.ocpp.service.OcppMiddleware;
import net.parkl.ocpp.service.cs.OcppServerService;
import org.joda.time.DateTime;

@NoArgsConstructor
public class ChargeBoxDriver {
    private OcppMiddleware facade;
    private OcppServerService serverService;

    private String name;
    private OcppProtocol protocol;
    private int connectors;

    public static ChargeBoxDriver createChargeBoxDriver(OcppMiddleware facade, OcppServerService serverService) {
        ChargeBoxDriver driver = new ChargeBoxDriver();
        driver.facade = facade;
        driver.serverService = serverService;
        return driver;
    }

    public void createChargeBox() {
        facade.registerChargeBox(name);

        UpdateChargeboxParams params = UpdateChargeboxParams.builder()
                .chargeBoxId(name)
                .ocppProtocol(protocol)
                .heartbeatTimestamp(new DateTime())
                .build();
        serverService.updateChargebox(params);
        serverService.updateEndpointAddress(name, "http://localhost:8081/ocpp-charger/ws");

        for (int i = 1; i <= connectors; i++) {
            InsertConnectorStatusParams p2 = InsertConnectorStatusParams.builder()
                    .chargeBoxId(name)
                    .connectorId(i)
                    .status("Available")
                    .build();
            serverService.insertConnectorStatus(p2);
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

}
