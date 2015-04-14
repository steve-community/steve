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
}
