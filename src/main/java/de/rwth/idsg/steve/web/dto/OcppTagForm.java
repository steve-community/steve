package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.web.validation.IdTag;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Getter
@Setter
public class OcppTagForm {

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

    /**
     * Is used when sending the POJO to JSP
     */
    public void setParentIdTagWithoutCheck(String parentIdTag) {
        this.parentIdTag = parentIdTag;
    }

    /**
     * Is used when reading the POJO back from JSP
     */
    public void setParentIdTag(String parentIdTag) {
        if (ControllerHelper.EMPTY_OPTION.equals(parentIdTag)) {
            this.parentIdTag = null;
        } else {
            this.parentIdTag = parentIdTag;
        }
    }
}
