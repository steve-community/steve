package de.rwth.idsg.steve.ocpp.ws.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2015
 */
@RequiredArgsConstructor
@Getter
public enum MessageType {
    CALL(2),
    CALL_RESULT(3),
    CALL_ERROR(4);

    private final int typeNr;

    public static MessageType fromTypeNr(int typeNr) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.typeNr == typeNr) {
                return messageType;
            }
        }
        throw new IllegalArgumentException(typeNr + " is an unknown message type");
    }
}
