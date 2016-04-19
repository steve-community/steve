package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.04.2016
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ReleaseReport {
    private final boolean moreRecent;

    private String githubVersion;

    private String htmlUrl;
    private String downloadUrl;
}
