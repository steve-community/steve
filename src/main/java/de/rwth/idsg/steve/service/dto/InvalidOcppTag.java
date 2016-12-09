package de.rwth.idsg.steve.service.dto;

import lombok.Getter;
import lombok.ToString;
import org.joda.time.DateTime;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.12.2016
 */
@ToString
@Getter
public class InvalidOcppTag {

    private final String idTag;
    private int numberOfAttempts = 0;
    private DateTime lastAttemptTimestamp;

    public InvalidOcppTag(String idTag) {
        this.idTag = idTag;
        updateStats();
    }

    public void updateStats() {
        numberOfAttempts++;
        lastAttemptTimestamp = DateTime.now();
    }
}
