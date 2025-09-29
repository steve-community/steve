package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.DeleteCertificateRequest;
import de.rwth.idsg.steve.ocpp20.model.DeleteCertificateResponse;
import de.rwth.idsg.steve.ocpp20.model.CertificateHashData;
import de.rwth.idsg.steve.ocpp20.model.HashAlgorithmEnum;
import lombok.Getter;
import java.util.List;

@Getter
public class DeleteCertificateTask extends Ocpp20Task<DeleteCertificateRequest, DeleteCertificateResponse> {

    private final String issuerNameHash;
    private final String issuerKeyHash;
    private final String serialNumber;

    public DeleteCertificateTask(List<String> chargeBoxIdList, String issuerNameHash,
                                  String issuerKeyHash, String serialNumber) {
        super("DeleteCertificate", chargeBoxIdList);
        this.issuerNameHash = issuerNameHash;
        this.issuerKeyHash = issuerKeyHash;
        this.serialNumber = serialNumber;
    }

    @Override
    public DeleteCertificateRequest createRequest() {
        DeleteCertificateRequest request = new DeleteCertificateRequest();

        CertificateHashData hashData = new CertificateHashData();
        hashData.setHashAlgorithm(HashAlgorithmEnum.SHA_256);
        hashData.setIssuerNameHash(issuerNameHash);
        hashData.setIssuerKeyHash(issuerKeyHash);
        hashData.setSerialNumber(serialNumber);

        request.setCertificateHashData(hashData);
        return request;
    }

    @Override
    public Class<DeleteCertificateResponse> getResponseClass() {
        return DeleteCertificateResponse.class;
    }
}
