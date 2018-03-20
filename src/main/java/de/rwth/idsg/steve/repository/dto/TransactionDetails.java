package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 27.04.2016
 */
@Getter
@RequiredArgsConstructor
public class TransactionDetails {
    private final Transaction transaction;
    private final List<MeterValues> values;

    @Getter
    @Builder
    public static class MeterValues {
        private final DateTime valueTimestamp;
        private final String value, readingContext, format, measurand, location, unit;

        // New in OCPP 1.6
        private final String phase;
    }
}
