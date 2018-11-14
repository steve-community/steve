package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.Ocpp16AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.web.dto.ocpp.GetCompositeScheduleParams;
import ocpp.cp._2015._10.GetCompositeScheduleRequest;
import ocpp.cp._2015._10.GetCompositeScheduleResponse;
import ocpp.cp._2015._10.GetCompositeScheduleStatus;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
public class GetCompositeScheduleTask extends Ocpp16AndAboveTask<GetCompositeScheduleParams, GetCompositeScheduleResponse> {

    public GetCompositeScheduleTask(OcppVersion ocppVersion,
                                    GetCompositeScheduleParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<GetCompositeScheduleResponse> defaultCallback() {
        return new DefaultOcppCallback<GetCompositeScheduleResponse>() {

            @Override
            public void success(String chargeBoxId, GetCompositeScheduleResponse response) {
                addNewResponse(chargeBoxId, response.getStatus().value());

                if (response.getStatus() == GetCompositeScheduleStatus.ACCEPTED) {
                    RequestResult result = getResultMap().get(chargeBoxId);
                    result.setDetails(response);
                }
            }
        };
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
                success(chargeBoxId, res.get());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
