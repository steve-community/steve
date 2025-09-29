package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.CertificateSignedRequest;
import de.rwth.idsg.steve.ocpp20.model.CertificateSignedResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class CertificateSignedTask extends Ocpp20Task<CertificateSignedRequest, CertificateSignedResponse> {

    private final String certificateChain;

    public CertificateSignedTask(List<String> chargeBoxIdList, String certificateChain) {
        super("CertificateSigned", chargeBoxIdList);
        this.certificateChain = certificateChain;
    }

    @Override
    public CertificateSignedRequest createRequest() {
        CertificateSignedRequest request = new CertificateSignedRequest();
        request.setCertificateChain(certificateChain);
        return request;
    }

    @Override
    public Class<CertificateSignedResponse> getResponseClass() {
        return CertificateSignedResponse.class;
    }
}
