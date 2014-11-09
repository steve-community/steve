package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Getter
@Setter
public class ChangeAvailabilityParams {

    private String[] cp_items;

    private Integer connectorId;

    private String availType;
}
