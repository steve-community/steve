package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.GetInstalledCertificateIdsRequest;
import de.rwth.idsg.steve.ocpp20.model.GetInstalledCertificateIdsResponse;
import de.rwth.idsg.steve.ocpp20.model.GetCertificateIdUseEnum;
import lombok.Getter;
import java.util.Arrays;
import java.util.List;

@Getter
public class GetInstalledCertificateIdsTask extends Ocpp20Task<GetInstalledCertificateIdsRequest, GetInstalledCertificateIdsResponse> {

    private final GetCertificateIdUseEnum certificateType;

    public GetInstalledCertificateIdsTask(List<String> chargeBoxIdList, GetCertificateIdUseEnum certificateType) {
        super("GetInstalledCertificateIds", chargeBoxIdList);
        this.certificateType = certificateType;
    }

    @Override
    public GetInstalledCertificateIdsRequest createRequest() {
        GetInstalledCertificateIdsRequest request = new GetInstalledCertificateIdsRequest();
        if (certificateType != null) {
            request.setCertificateType(Arrays.asList(certificateType));
        }
        return request;
    }

    @Override
    public Class<GetInstalledCertificateIdsResponse> getResponseClass() {
        return GetInstalledCertificateIdsResponse.class;
    }
}
