package net.parkl.ocpp.service.middleware;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.Address;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.ConnectorLastStatus;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.module.esp.model.*;
import net.parkl.ocpp.repositories.ConnectorLastStatusRepository;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.service.cs.ChargePointService;
import net.parkl.ocpp.util.ListTransform;
import ocpp.cs._2015._10.RegistrationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.rwth.idsg.steve.web.dto.ocpp.AvailabilityType.INOPERATIVE;
import static de.rwth.idsg.steve.web.dto.ocpp.AvailabilityType.OPERATIVE;
import static java.util.Collections.singletonList;
import static net.parkl.ocpp.service.ErrorMessages.INVALID_CHARGE_BOX_ID;

@Service
@Slf4j
public class OcppChargePointMiddleware extends AbstractOcppMiddleware {

    @Autowired
    private ChargePointService chargePointService;


    @Autowired
    private ConnectorRepository connectorRepo;
    @Autowired
    private ConnectorLastStatusRepository connectorLastStatusRepository;

    public void registerChargeBox(String chargeBoxId) {
        log.info("Register charge box request: {}...", chargeBoxId);

        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox != null) {
            log.error("Charge box already exists: {}", chargeBoxId);
            throw new IllegalArgumentException("Charge box already exists: " + chargeBoxId);
        }

        ChargePointForm form = new ChargePointForm();
        form.setChargeBoxId(chargeBoxId);
        form.setAddress(new Address());
        form.setRegistrationStatus(RegistrationStatus.ACCEPTED.value());
        chargePointService.addChargePoint(form);
    }

    public void unregisterChargeBox(String chargeBoxId) {
        log.info("Unregister charge box request: {}...", chargeBoxId);

        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox == null) {
            throw new IllegalArgumentException(INVALID_CHARGE_BOX_ID + chargeBoxId);
        }

        chargePointService.deleteChargePoint(chargeBox.getChargeBoxPk());
    }



    public ESPChargerStatusResult getChargerStatuses() {
        log.info("Querying all connector statuses...");
        List<Connector> connectors = connectorRepo.findAllByOrderByConnectorPkAsc();
        Iterable<ConnectorLastStatus> statuses = connectorLastStatusRepository.findAll();
        Map<Integer, ConnectorLastStatus> statusMap = new HashMap<>();

        for (ConnectorLastStatus status : statuses) {
            statusMap.put(status.getConnectorPk(), status);
        }

        ESPChargerStatusResult ret = ESPChargerStatusResult.builder().status(new ArrayList<>()).build();


        for (Connector connector : connectors) {
            ESPChargerStatus dto = ESPChargerStatus.builder().
                    externalChargerId(String.format("%s_%d", connector.getChargeBoxId(), connector.getConnectorId())).build();

            ConnectorLastStatus status = statusMap.get(connector.getConnectorPk());
            if (status != null) {
                switch (status.getStatus()) {
                    case "Available":
                        dto.setState(ESPChargerState.Free);
                        break;
                    case "Charging":
                    case "Preparing":
                    case "Finishing":
                        dto.setState(ESPChargerState.Occupied);
                        break;
                    default:
                        dto.setState(ESPChargerState.Error);
                        break;
                }
            }
            ret.getStatus().add(dto);
        }
        return ret;
    }

    public ESPChargerState getChargerStatus(String chargeBoxId, int connectorId) {
        Connector connector = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);

        if (connector == null) {
            throw new IllegalArgumentException("OcppConnector was null");
        }

        ConnectorLastStatus connectorStatus = connectorLastStatusRepository.findById(connector.getConnectorPk())
                .orElse(null);

        if (connectorStatus != null) {
            switch (connectorStatus.getStatus()) {
                case "Available":
                    return ESPChargerState.Free;
                case "Charging":
                case "Preparing":
                case "Finishing":
                    return ESPChargerState.Occupied;
                default:
                    return ESPChargerState.Error;
            }
        }

        return null;
    }

    public void changeAvailability(String chargeBoxId, String chargerId, boolean available) {
        log.info("Availability change request for {}-{}: {}...", chargeBoxId, chargerId, available);
        ChargePointSelect c;
        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox == null) {
            throw new IllegalArgumentException(INVALID_CHARGE_BOX_ID + chargeBoxId);
        }

        c = getChargePoint(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());
        if (c == null) {
            log.error("Invalid charge point id: {}", chargeBoxId);
            throw new IllegalArgumentException(INVALID_CHARGE_BOX_ID + chargeBoxId);

        }

        ChangeAvailabilityParams params = new ChangeAvailabilityParams();
        params.setChargePointSelectList(singletonList(c));
        params.setConnectorId(Integer.parseInt(chargerId));
        params.setAvailType(available ? OPERATIVE : INOPERATIVE);
        int taskId = sendChangeAvailability(params, chargeBox.getOcppProtocol());

        RequestResult result = waitForResult(chargeBoxId, taskId);
        processGenericResult("Change availability", chargeBoxId, result);
    }

    private int sendChangeAvailability(ChangeAvailabilityParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_12_SOAP:
            case V_12_JSON:
                return client12.changeAvailability(params);
            case V_15_SOAP:
            case V_15_JSON:
                return client15.changeAvailability(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.changeAvailability(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    public ESPChargeBoxData getChargeBoxData(String chargeBoxId) {
        log.info("Querying charge box data for charge box: {}...", chargeBoxId);
        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox == null) {
            log.error("Invalid charge box id: {}", chargeBoxId);
            throw new IllegalArgumentException(INVALID_CHARGE_BOX_ID + chargeBoxId);
        }

        return ESPChargeBoxData.builder()
                .chargeBoxSerialNumber(chargeBox.getChargeBoxSerialNumber())
                .chargePointModel(chargeBox.getChargePointModel())
                .chargePointVendor(chargeBox.getChargePointVendor())
                .chargePointSerialNumber(chargeBox.getChargePointSerialNumber())
                .endpointAddress(chargeBox.getEndpointAddress())
                .fwVersion(chargeBox.getFwVersion())
                .lastHeartbeatTimestamp(chargeBox.getLastHeartbeatTimestamp())
                .meterSerialNumber(chargeBox.getMeterSerialNumber())
                .meterType(chargeBox.getMeterType())
                .ocppProtocol(chargeBox.getOcppProtocol())
                .build();
    }

    public List<ESPConnectorStatus> getChargeBoxConnectorStatus(String chargeBoxId) {
        log.info("Querying connector statuses for charge box: {}...", chargeBoxId);
        List<ConnectorLastStatus> lastStatuses = connectorLastStatusRepository.findByChargeBoxId(chargeBoxId);
        return ListTransform.transform(lastStatuses, this::toConnectorStatus);
    }

    private ESPConnectorStatus toConnectorStatus(ConnectorLastStatus connectorLastStatus) {
        return ESPConnectorStatus.builder()
                .connectorId(connectorLastStatus.getConnectorId())
                .status(connectorLastStatus.getStatus())
                .statusTimestamp(connectorLastStatus.getStatusTimestamp())
                .errorCode(connectorLastStatus.getErrorCode())
                .errorInfo(connectorLastStatus.getErrorInfo())
                .vendorErrorCode(connectorLastStatus.getVendorErrorCode())
                .vendorId(connectorLastStatus.getVendorId())
                .build();
    }

}
