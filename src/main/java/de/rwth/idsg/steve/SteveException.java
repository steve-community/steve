package de.rwth.idsg.steve;

import static java.lang.String.format;

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

    // -------------------------------------------------------------------------
    // No String/variable interpolation in Java. Use format instead.
    // -------------------------------------------------------------------------

    public SteveException(String template, Object arg1) {
        this(format(template, arg1));
    }

    public SteveException(String template, Object arg1, Throwable cause) {
        this(format(template, arg1), cause);
    }

    public SteveException(String template, Object arg1, Object arg2) {
        this(format(template, arg1, arg2));
    }

    public SteveException(String template, Object arg1, Object arg2, Throwable cause) {
        this(format(template, arg1, arg2), cause);
    }
}
