package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.InstallCertificateRequest;
import de.rwth.idsg.steve.ocpp20.model.InstallCertificateResponse;
import de.rwth.idsg.steve.ocpp20.model.InstallCertificateUseEnum;
import lombok.Getter;
import java.util.List;

@Getter
public class InstallCertificateTask extends Ocpp20Task<InstallCertificateRequest, InstallCertificateResponse> {

    private final InstallCertificateUseEnum certificateType;
    private final String certificate;

    public InstallCertificateTask(List<String> chargeBoxIdList, InstallCertificateUseEnum certificateType, String certificate) {
        super("InstallCertificate", chargeBoxIdList);
        this.certificateType = certificateType;
        this.certificate = certificate;
    }

    @Override
    public InstallCertificateRequest createRequest() {
        InstallCertificateRequest request = new InstallCertificateRequest();
        request.setCertificate(certificate);
        request.setCertificateType(certificateType);
        return request;
    }

    @Override
    public Class<InstallCertificateResponse> getResponseClass() {
        return InstallCertificateResponse.class;
    }
}
