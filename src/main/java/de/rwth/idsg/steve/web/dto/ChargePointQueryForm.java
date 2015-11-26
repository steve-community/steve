package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 26.11.2015
 */
@Getter
@Setter
public class ChargePointQueryForm {

    private OcppVersion ocppVersion;
    private QueryPeriodType heartbeatPeriod;

    /**
     * Init with sensible default values
     */
    public ChargePointQueryForm() {
        heartbeatPeriod = QueryPeriodType.ALL;
    }

    public boolean isSetOcppVersion() {
        return ocppVersion != null;
    }

    @RequiredArgsConstructor
    public enum QueryPeriodType {
        ALL("All"),
        TODAY("Today"),
        YESTERDAY("Yesterday"),
        EARLIER("Earlier");

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
