package de.rwth.idsg.steve.ocpp.ws.data;

import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2015
 */
@Getter
@Setter
public class OcppJsonResult extends OcppJsonResponse {
    private ResponseType payload;

    public OcppJsonResult() {
        super(MessageType.CALL_RESULT);
    }
}
