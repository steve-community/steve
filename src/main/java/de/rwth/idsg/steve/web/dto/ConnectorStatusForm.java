package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 18.09.2018
 */
@Getter
@Setter
public class ConnectorStatusForm {
    private String chargeBoxId;
    private String status;
}
