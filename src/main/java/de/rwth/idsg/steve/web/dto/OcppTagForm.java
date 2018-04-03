package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.web.validation.IdTag;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Getter
@Setter
public class OcppTagForm {

    // Internal database id
    private Integer ocppTagPk;

    @NotEmpty(message = "ID Tag is required")
    @IdTag
    private String idTag;

    // Is a FK in DB table. No validation needed. Operation will fail if DB constraint fails.
    private String parentIdTag;

    @Future(message = "Expiry Date/Time must be in future")
    private LocalDateTime expiration;

    @NotNull(message = "Should this ID Tag be blocked or not?")
    private Boolean blocked = false;

    private String note;
}
