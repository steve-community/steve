package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 16.11.2018
 */
@Getter
@Setter
public class ChargingProfileAssignmentQueryForm {

    private String chargeBoxId;
    private Integer chargingProfilePk;
    private String chargingProfileDescription;

}
