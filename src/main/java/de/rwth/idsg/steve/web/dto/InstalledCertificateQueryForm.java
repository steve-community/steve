package de.rwth.idsg.steve.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ocpp._2022._02.security.GetInstalledCertificateIds.CertificateUseEnumType;

@Getter
@Setter
@ToString
public class InstalledCertificateQueryForm {

    @Schema(description = "The identifier of the chargebox (i.e. charging station)")
    private String chargeBoxId;

    private CertificateUseEnumType certificateType;

    @Schema(hidden = true)
    public boolean isChargeBoxIdSet() {
        return chargeBoxId != null;
    }
}
