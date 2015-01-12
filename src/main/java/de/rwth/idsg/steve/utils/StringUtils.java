package de.rwth.idsg.steve.utils;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.01.2015
 */
public final class StringUtils {
    private StringUtils() {}

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
