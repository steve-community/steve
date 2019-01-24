package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.repository.ReservationStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 31.08.2015
 */
@Getter
@Setter
public class ReservationQueryForm extends QueryForm {

    private ReservationStatus status;

    private QueryPeriodType periodType;

    /**
     * Init with sensible default values
     */
    public ReservationQueryForm() {
        periodType = QueryPeriodType.ACTIVE;
    }

    public boolean isStatusSet() {
        return status != null;
    }

    @AssertTrue(message = "The values 'From' and 'To' must be both set")
    public boolean isPeriodFromToCorrect() {
        return periodType != QueryPeriodType.FROM_TO || isFromToSet();
    }

    // -------------------------------------------------------------------------
    // Enums
    // -------------------------------------------------------------------------

    @RequiredArgsConstructor
    public enum QueryPeriodType {
        ACTIVE("Active"),
        FROM_TO("From/To");

        @Getter private final String value;

        public static QueryPeriodType fromValue(String v) {
            for (QueryPeriodType c: QueryPeriodType.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }
}
