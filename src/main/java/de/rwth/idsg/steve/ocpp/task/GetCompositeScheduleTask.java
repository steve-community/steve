package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.Ocpp16AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.GetCompositeScheduleParams;
import ocpp.cp._2015._10.GetCompositeScheduleRequest;
import ocpp.cp._2015._10.GetCompositeScheduleResponse;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
public class GetCompositeScheduleTask extends Ocpp16AndAboveTask<GetCompositeScheduleParams, String> {

    public GetCompositeScheduleTask(OcppVersion ocppVersion,
                                    GetCompositeScheduleParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        // TODO: print schedule Details
        return new StringOcppCallback();
    }

    @Override
    public GetCompositeScheduleRequest getOcpp16Request() {
        return new GetCompositeScheduleRequest()
                .withConnectorId(params.getConnectorId())
                .withDuration(params.getDurationInSeconds())
                .withChargingRateUnit(params.getChargingRateUnit());
    }

    @Override
    public AsyncHandler<GetCompositeScheduleResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                // TODO: print schedule Details
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
