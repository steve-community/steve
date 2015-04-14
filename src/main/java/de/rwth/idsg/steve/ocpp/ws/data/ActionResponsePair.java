package de.rwth.idsg.steve.ocpp.ws.data;

import de.rwth.idsg.steve.ocpp.ResponseType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 23.03.2015
 */
@Getter
@RequiredArgsConstructor
public class ActionResponsePair {
    private final String action;
    private final Class<? extends ResponseType> responseClass;
}
