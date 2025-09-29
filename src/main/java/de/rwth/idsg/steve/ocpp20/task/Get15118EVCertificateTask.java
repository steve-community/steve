package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.Get15118EVCertificateRequest;
import de.rwth.idsg.steve.ocpp20.model.Get15118EVCertificateResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class Get15118EVCertificateTask extends Ocpp20Task<Get15118EVCertificateRequest, Get15118EVCertificateResponse> {

    private final String iso15118SchemaVersion;
    private final String exi;

    public Get15118EVCertificateTask(List<String> chargeBoxIdList, String iso15118SchemaVersion, String exi) {
        super("Get15118EVCertificate", chargeBoxIdList);
        this.iso15118SchemaVersion = iso15118SchemaVersion;
        this.exi = exi;
    }

    @Override
    public Get15118EVCertificateRequest createRequest() {
        Get15118EVCertificateRequest request = new Get15118EVCertificateRequest();
        request.setIso15118SchemaVersion(iso15118SchemaVersion);
        request.setExiRequest(exi);
        return request;
    }

    @Override
    public Class<Get15118EVCertificateResponse> getResponseClass() {
        return Get15118EVCertificateResponse.class;
    }
}
