package de.rwth.idsg.steve.web.dto.ocpp15;

import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 02.01.2015
 */
@Getter
@Setter
public class DataTransferParams extends MultipleChargePointSelect {

    @NotNull(message = "Vendor ID is required")
    private String vendorId;

    private String messageId;

    private String data;
}
