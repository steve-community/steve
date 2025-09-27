package de.rwth.idsg.steve.gateway.oicp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalibrationLawVerificationInfo {
    private String calibrationLawCertificateId;
    private String publicKey;
    private String meteringSignatureUrl;
    private String meteringSignatureFormat;
    private String signedMeterValue;
}