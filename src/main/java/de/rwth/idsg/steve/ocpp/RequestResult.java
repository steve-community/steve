package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.steve.SteveException;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.10.2015
 */
@Getter
@Setter
public class RequestResult {
    private String response;
    private String errorMessage;
    private Object details;

    @SuppressWarnings("unchecked")
    public <T> T getDetails() {
        if (details == null) {
            throw new SteveException("Result details not found");
        } else {
            return (T) details;
        }
    }

    public <T> void setDetails(@NotNull T item) {
        this.details = item;
    }
}
