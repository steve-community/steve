package de.rwth.idsg.steve.ocpp;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.10.2015
 */
@Getter
@Setter
public class RequestResult {
    private String response;
    private String errorMessage;
}
