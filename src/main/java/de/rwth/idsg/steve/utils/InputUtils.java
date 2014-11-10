package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.web.ExceptionMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
*
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public final class InputUtils {
    private InputUtils() {}

    /**
     * Returns true, if parameter string is null or empty.
     *
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * Throws exception, if the parameter string(s) is/are null or empty.
     *
     * @param strArray	string(s)
     */
    public static void checkNullOrEmpty(String... strArray) {
        for (String str : strArray) {
            if (isNullOrEmpty(str)) {
                throw new SteveException(ExceptionMessage.INPUT_EMPTY);
            }
        }
    }

    /**
     * Converts parameter into integer. Throws exception, if parameter cannot be converted.
     *
     */
    public static int toInt(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            throw new SteveException(ExceptionMessage.PARSING_NUMBER, e);
        }
    }

    /**
     * Returns zero, if parameter is empty. Otherwise, converts it to integer.
     *
     */
    public static int chooseInt(String str) {
        if (isNullOrEmpty(str)) {
            return 0;
        } else {
            return toInt(str);
        }
    }

    /**
     * Validates the input string.
     * Allowed characters are: Upper or lower case letters, numbers and dot, dash, underscore symbols.
     *
     */
    public static void validateIdTag(String str) {
        String idTagRegex = "^[a-zA-Z0-9._-]{1,20}$";

        Matcher matcher = Pattern.compile(idTagRegex).matcher(str);

        if (!matcher.matches()) {
            throw new SteveException(ExceptionMessage.INVALID_IDTAG);
        }
    }
}