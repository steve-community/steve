package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.Ocpp16AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileFilterType;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileParams;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.ClearChargingProfileRequest;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
@Slf4j
public class ClearChargingProfileTask extends Ocpp16AndAboveTask<ClearChargingProfileParams, String> {

    private final ChargingProfileRepository chargingProfileRepository;

    public ClearChargingProfileTask(OcppVersion ocppVersion,
                                    ClearChargingProfileParams params,
                                    ChargingProfileRepository chargingProfileRepository) {
        super(ocppVersion, params);
        this.chargingProfileRepository = chargingProfileRepository;
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new DefaultOcppCallback<String>() {
            @Override
            public void success(String chargeBoxId, String statusValue) {
                addNewResponse(chargeBoxId, statusValue);

                if ("Accepted".equalsIgnoreCase(statusValue)) {
                    switch (params.getFilterType()) {
                        case ChargingProfileId:
                            chargingProfileRepository.clearProfile(params.getChargingProfilePk(), chargeBoxId);
                            break;
                        case OtherParameters:
                            chargingProfileRepository.clearProfile(chargeBoxId,
                                    params.getConnectorId(), params.getChargingProfilePurpose(), params.getStackLevel());
                            break;
                        default:
                            log.warn("Unexpected {} enum value", ClearChargingProfileFilterType.class.getSimpleName());
                    }
                }
            }
        };
    }

    @Override
    public ocpp.cp._2015._10.ClearChargingProfileRequest getOcpp16Request() {
        return new ClearChargingProfileRequest()
                .withId(params.getChargingProfilePk())
                .withConnectorId(params.getConnectorId())
                .withChargingProfilePurpose(params.getChargingProfilePurpose())
                .withStackLevel(params.getStackLevel());
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.ClearChargingProfileResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
