package de.rwth.idsg.steve.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.rwth.idsg.steve.html.ExceptionMessage;
import de.rwth.idsg.steve.html.InputException;

/**
*
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class InputUtils {
	
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
				throw new InputException(ExceptionMessage.INPUT_EMPTY);
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
			throw new InputException(ExceptionMessage.PARSING_NUMBER);
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
		String REGEX = "^[a-zA-Z0-9._-]{1,20}$";
	
		Matcher matcher = Pattern.compile(REGEX).matcher(str);
		
		if ( !matcher.matches() ) {
			throw new InputException(ExceptionMessage.INVALID_IDTAG);
		}
	}
}