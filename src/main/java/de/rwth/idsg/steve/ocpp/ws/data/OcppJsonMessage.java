package de.rwth.idsg.steve.ocpp.ws.data;

import lombok.Getter;
import lombok.Setter;

/**
 * Class hierarchy:
 *
 *      OcppJsonMessage
 *      +
 *      +--> OcppJsonCall
 *      +
 *      +--> OcppJsonResponse
 *           +
 *           +--> OcppJsonResult
 *           +
 *           +--> OcppJsonError
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.03.2015
 */
@Getter
@Setter
public abstract class OcppJsonMessage {
    private final MessageType messageType;
    private String messageId;

    public OcppJsonMessage(MessageType messageType) {
        this.messageType = messageType;
    }
}
