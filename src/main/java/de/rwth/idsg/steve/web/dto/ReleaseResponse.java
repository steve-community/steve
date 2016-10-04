package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Does not contain all the fields in the actual response, but only the ones that we are interested in.
 *
 * API doc: https://developer.github.com/v3/repos/releases/#get-the-latest-release
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 04.10.2016
 */
@Getter
@Setter
@ToString
public class ReleaseResponse {
    private String tagName;
    private String name;

    private String htmlUrl;
    private String tarballUrl;
    private String zipballUrl;
}
