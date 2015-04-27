package de.rwth.idsg.steve.ocpp.ws.data;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2015
 */
@Getter
@Setter
public class OcppJsonError extends OcppJsonResponse {
    private ErrorCode errorCode;
    private String errorDescription;
    private String errorDetails;

    public OcppJsonError() {
        super(MessageType.CALL_ERROR);
    }

    public boolean isSetDescription() {
        return errorDescription != null;
    }

    public boolean isSetDetails() {
        return errorDetails != null;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("Code: ").append(errorCode.toString());

        if (isSetDescription()) {
            b.append(", Description: ").append(errorDescription);
        }

        if (isSetDetails()) {
            b.append(", Details: ").append(errorDetails);
        }

        return b.toString();
    }

    /**
     * Let's be defensive and prevent problems due to errorDetails
     * that can be longer than what is expected/allowed on the receiving end.
     *
     * It actually happened with a charge point which responded with the message
     * "ERROR: dropped too large packet" and the connection was closed.
     */
    public String toStringErrorDetails() {
        if (errorDetails.length() > 50) {
            return errorDetails.substring(0, 50) + "...";
        } else {
            return errorDetails;
        }
    }
}
