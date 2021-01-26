package net.parkl.ocpp.service;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import net.parkl.ocpp.module.esp.model.ESPChargingResult;
import net.parkl.ocpp.module.esp.model.ESPChargingStartRequest;
import net.parkl.ocpp.module.esp.model.ESPChargingStartResult;
import net.parkl.ocpp.module.esp.model.ESPChargingUserStopRequest;
import net.parkl.ocpp.service.cs.OcppServerService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OcppTestHelper {
	@Autowired
	private EmobilityServiceProviderFacade facade;
	@Autowired
	private OcppServerService serverService;
	@Autowired
	private OcppTestFixture fixture;
	
	public void createChargeBox(String name, OcppProtocol ocppProtocol, int connectors) {
		facade.registerChargeBox(name);
		
		UpdateChargeboxParams params=UpdateChargeboxParams.builder()
				.chargeBoxId(name)
				.ocppProtocol(ocppProtocol)
				.heartbeatTimestamp(new DateTime())
				.build();
		serverService.updateChargebox(params);
		serverService.updateEndpointAddress(name, "http://localhost:8081/ocpp-charger/ws");

		for (int i=1;i<=connectors;i++) {
			InsertConnectorStatusParams p2=InsertConnectorStatusParams.builder()
				.chargeBoxId(name)
				.connectorId(i)
				.status("Available")
				.build();
			serverService.insertConnectorStatus(p2);
		}
	}

	public void startAndStopCharging(String chargeBoxId, int connectorId, String plate, int startValue, int stopValue, String rfidTag) {
		fixture.registerStartValue(startValue);

		ESPChargingStartRequest req=ESPChargingStartRequest.builder()
				.chargeBoxId(chargeBoxId)
				.chargerId(String.valueOf(connectorId))
				.licensePlate(plate)
				.rfidTag(rfidTag).build();

		ESPChargingStartResult result = facade.startCharging(req);
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getExternalChargingProcessId());
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
			throw new IllegalStateException(ex);
		}
		fixture.registerStopValue(stopValue);
		
		ESPChargingUserStopRequest req2=ESPChargingUserStopRequest.builder().externalChargeId(result.getExternalChargingProcessId()).build();
		ESPChargingResult res2 = facade.stopCharging(req2);
		Assert.assertNotNull(res2);
		Assert.assertTrue(res2.getStoppedWithoutTransaction()==null||res2.getStoppedWithoutTransaction()==false);
	}
}
