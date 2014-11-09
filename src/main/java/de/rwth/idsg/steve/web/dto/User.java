package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.web.validation.IdTag;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Getter
@Setter
public class User {

    @NotEmpty
    @IdTag
    private String idTag;

    @IdTag
    private String parentIdTag;

    @Future
    private LocalDate expiryDate;

    private LocalTime expiryTime;

    @NotNull
    private Boolean blocked;
}