package net.parkl.ocpp.service.middleware;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.Address;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.module.esp.model.ESPChargeBoxConfiguration;
import net.parkl.ocpp.service.cs.ChargePointService;
import ocpp.cs._2015._10.RegistrationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@Service
@Slf4j
public class OcppConfigurationMiddleware extends AbstractOcppMiddleware {

    private int sendGetConfiguration(GetConfigurationParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_15_SOAP:
            case V_15_JSON:
                return client15.getConfiguration(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.getConfiguration(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    private int sendChangeConfiguration(ChangeConfigurationParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_15_SOAP:
            case V_15_JSON:
                return client15.changeConfiguration(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.changeConfiguration(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    public List<ESPChargeBoxConfiguration> getChargeBoxConfiguration(String chargeBoxId) {
        log.info("Configuration request: {}...", chargeBoxId);
        ChargePointSelect c;
        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox == null) {
            log.error("Invalid charge box id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);
        }

        c = getChargePoint(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());
        if (c == null) {
            log.error("Invalid charge point id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);

        }

        GetConfigurationParams params = new GetConfigurationParams();
        params.setChargePointSelectList(singletonList(c));
        int taskId = sendGetConfiguration(params, chargeBox.getOcppProtocol());

        RequestResult result = waitForResult(chargeBoxId, taskId);

        return processGetConfigurationResult(chargeBoxId, result);

    }

    private List<ESPChargeBoxConfiguration> processGetConfigurationResult(String chargeBoxId,
                                                                          RequestResult result) {
        if (result != null) {
            if (result.getDetails() != null) {
                log.info(result.getDetails());
            }
            if (result.getResponse() != null) {
                return OcppConfigParser.parseConfList(result.getResponse());
            } else if (result.getErrorMessage() != null) {
                throw new IllegalStateException(result.getErrorMessage());
            } else {
                log.info("Get configuration unknown error: {}", chargeBoxId);
                throw new IllegalStateException("Unknown error: " + chargeBoxId);

            }
        } else {
            log.info("Get configuration no response error: {}", chargeBoxId);
            throw new IllegalStateException("No response from charge box: " + chargeBoxId);
        }
    }



    public List<ESPChargeBoxConfiguration> changeChargeBoxConfiguration(String chargeBoxId, String key,
                                                                        String value) {
        log.info("Configuration change request for {}: {}={}...", chargeBoxId, key, value);
        ChargePointSelect c = null;
        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox == null) {
            log.error("Invalid charge box id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);
        }

        c = getChargePoint(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());
        if (c == null) {
            log.error("Invalid charge point id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);

        }


        ChangeConfigurationParams params = new ChangeConfigurationParams();
        params.setChargePointSelectList(singletonList(c));
        params.setConfKey(key);
        params.setValue(value);
        int taskId = sendChangeConfiguration(params, chargeBox.getOcppProtocol());

        RequestResult result = waitForResult(chargeBoxId, taskId);
        processGenericResult("Change configuration", chargeBoxId, result);

        return getChargeBoxConfiguration(chargeBoxId);
    }




}
