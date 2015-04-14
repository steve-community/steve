package de.rwth.idsg.steve.ocpp.ws.data;

import de.rwth.idsg.steve.handler.OcppResponseHandler;
import de.rwth.idsg.steve.ocpp.ResponseType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 23.03.2015
 */
@Getter
@Setter
@RequiredArgsConstructor
public class FutureResponseContext {
    private final OcppResponseHandler handler;
    private final Class<? extends ResponseType> responseClass;
}
