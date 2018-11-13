package de.rwth.idsg.steve.repository.dto;

import jooq.steve.db.tables.records.ChargingProfileRecord;
import jooq.steve.db.tables.records.ChargingSchedulePeriodRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.11.2018
 */
public class ChargingProfile {

    @Getter
    @RequiredArgsConstructor
    public static final class BasicInfo {
        private final int chargingProfilePk;
        private final String description;

        public String getItemDescription() {
            if (description == null) {
                return Integer.toString(chargingProfilePk);
            } else {
                return chargingProfilePk + " ("  + description + ")";
            }
        }
    }

    @Getter
    @Builder
    public static final class Overview {
        private final int chargingProfilePk;
        private final int stackLevel;
        private final String description, profilePurpose, profileKind, recurrencyKind;
        private final DateTime validFrom, validTo;
    }

    @Getter
    @RequiredArgsConstructor
    public static final class Details {
        private final ChargingProfileRecord profile;
        private final List<ChargingSchedulePeriodRecord> periods;
    }
}
