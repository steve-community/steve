package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetInstalledCertificateIdsParams extends BaseParams {
    private String certificateType;
}
