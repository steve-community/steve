package de.rwth.idsg.steve;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 28.08.2014
 */
public class SteveException extends RuntimeException {

    private static final long serialVersionUID = 3081743035434873349L;

    public SteveException(String message) {
        super(message);
    }

    public SteveException(String message, Throwable cause) {
        super(message, cause);
    }
}