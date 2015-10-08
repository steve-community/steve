package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.web.dto.common.AbstractNoteForm;
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
public class UserForm extends AbstractNoteForm {

    @NotEmpty(message = "User ID Tag is required")
    @IdTag
    private String idTag;

    // Is a FK in DB table. No validation needed. Operation will fail if DB constraint fails.
    private String parentIdTag;

    @Future(message = "Expiry Date/Time must be in future")
    private LocalDateTime expiration;

    @NotNull(message = "Should the user be blocked or not?")
    private Boolean blocked = false;

    public void setParentIdTag(String parentIdTag) {
        if (parentIdTag.equals("EMPTY-OPTION")) {
            this.parentIdTag = null;
        } else {
            this.parentIdTag = parentIdTag;
        }
    }
}
