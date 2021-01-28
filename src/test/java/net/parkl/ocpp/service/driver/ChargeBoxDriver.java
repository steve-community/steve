package net.parkl.ocpp.service.driver;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import lombok.NoArgsConstructor;
import net.parkl.ocpp.service.OcppMiddleware;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfigService;
import net.parkl.ocpp.service.cs.ChargePointService;
import net.parkl.ocpp.service.cs.ConnectorService;
import org.joda.time.DateTime;

@NoArgsConstructor
public class ChargeBoxDriver {
    private OcppMiddleware ocppMiddleware;
    private ChargePointService chargePointService;
    private ConnectorService connectorService;
    private AdvancedChargeBoxConfigService advancedChargeBoxConfigService;

    private String name;
    private OcppProtocol protocol;
    private int connectors;

    public static ChargeBoxDriver createChargeBoxDriver(OcppMiddleware facade,
                                                        ChargePointService chargePointService,
                                                        ConnectorService connectorService,
                                                        AdvancedChargeBoxConfigService advancedChargeBoxConfigService) {
        ChargeBoxDriver driver = new ChargeBoxDriver();
        driver.ocppMiddleware = facade;
        driver.chargePointService = chargePointService;
        driver.connectorService = connectorService;
        driver.advancedChargeBoxConfigService = advancedChargeBoxConfigService;
        return driver;
    }

    public void createChargeBox() {
        ocppMiddleware.registerChargeBox(name);

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

    public void deleteAdvancedConfigByKey(String key) {
        advancedChargeBoxConfigService.deleteByKey(key);
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
