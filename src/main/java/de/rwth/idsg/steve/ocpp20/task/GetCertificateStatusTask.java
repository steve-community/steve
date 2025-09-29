package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.GetCertificateStatusRequest;
import de.rwth.idsg.steve.ocpp20.model.GetCertificateStatusResponse;
import de.rwth.idsg.steve.ocpp20.model.OCSPRequestData;
import lombok.Getter;
import java.util.List;

@Getter
public class GetCertificateStatusTask extends Ocpp20Task<GetCertificateStatusRequest, GetCertificateStatusResponse> {

    private final OCSPRequestData ocspRequestData;

    public GetCertificateStatusTask(List<String> chargeBoxIdList, OCSPRequestData ocspRequestData) {
        super("GetCertificateStatus", chargeBoxIdList);
        this.ocspRequestData = ocspRequestData;
    }

    @Override
    public GetCertificateStatusRequest createRequest() {
        GetCertificateStatusRequest request = new GetCertificateStatusRequest();
        request.setOcspRequestData(ocspRequestData);
        return request;
    }

    @Override
    public Class<GetCertificateStatusResponse> getResponseClass() {
        return GetCertificateStatusResponse.class;
    }
}
