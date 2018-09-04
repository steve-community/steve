package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.ocpp.CommunicationTask;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.01.2015
 */
public final class StringUtils {
    private StringUtils() { }

    /**
     * We don't want to hard-code operation names,
     * but derive them from the actual request object.
     *
     * Example for "ChangeAvailabilityTask":
     * - Remove "Task" at the end -> "ChangeAvailability"
     * - Insert space -> "Change Availability"
     */
    public static String getOperationName(CommunicationTask task) {
        String s = task.getClass().getSimpleName();

        if (s.endsWith("Task")) {
            s = s.substring(0, s.length() - 4);
        }

        // http://stackoverflow.com/a/4886141
        s = s.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");

        return s;
    }
}
