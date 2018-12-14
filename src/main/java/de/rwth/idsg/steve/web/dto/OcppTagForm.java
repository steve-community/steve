package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.web.validation.IdTag;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;

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

    private Integer maxActiveTransactionCount;

    private String note;

    /**
     * As specified in V0_9_9__update.sql default value is 1.
     */
    public Integer getMaxActiveTransactionCount() {
        if (maxActiveTransactionCount == null) {
            return 1;
        } else {
            return maxActiveTransactionCount;
        }
    }
}
