package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.utils.StringUtils;
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

    @AssertTrue(message = "'From' must be after 'To'")
    public boolean isFromToValid() {
        return !isFromToSet() || to.isAfter(from);
    }

    boolean isFromToSet() {
        return from != null && to != null;
    }

    public boolean isChargeBoxIdSet() {
        return !StringUtils.isNullOrEmpty(chargeBoxId);
    }

    public boolean isUserIdSet() {
        return !StringUtils.isNullOrEmpty(userId);
    }
}
