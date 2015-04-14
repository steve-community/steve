package de.rwth.idsg.steve.ocpp.ws.data;

import de.rwth.idsg.steve.ocpp.RequestType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2015
 */
@Getter
@Setter
public class OcppJsonCall extends OcppJsonMessage {
    private String action;
    private RequestType payload;

    public OcppJsonCall() {
        super(MessageType.CALL);
    }
}
