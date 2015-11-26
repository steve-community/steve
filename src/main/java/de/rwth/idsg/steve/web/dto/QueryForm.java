package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.AssertTrue;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 31.08.2015
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class QueryForm {

    private String chargeBoxId;
    private String userId;

    private LocalDateTime from;
    private LocalDateTime to;

    @AssertTrue(message = "'To' must be after 'From'")
    public boolean isFromToValid() {
        return !isFromToSet() || to.isAfter(from);
    }

    boolean isFromToSet() {
        return from != null && to != null;
    }

    public boolean isChargeBoxIdSet() {
        return chargeBoxId != null;
    }

    public boolean isUserIdSet() {
        return userId != null;
    }
}
