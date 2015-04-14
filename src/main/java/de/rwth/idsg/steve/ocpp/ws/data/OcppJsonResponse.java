package de.rwth.idsg.steve.ocpp.ws.data;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2015
 */
public abstract class OcppJsonResponse extends OcppJsonMessage {
    public OcppJsonResponse(MessageType messageType) {
        super(messageType);
    }
}
