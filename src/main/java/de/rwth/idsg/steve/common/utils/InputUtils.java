package de.rwth.idsg.steve.common.utils;

import de.rwth.idsg.steve.html.ExceptionMessage;
import de.rwth.idsg.steve.html.InputException;

/**
*
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class InputUtils {
	
	/**
	 * Throws exception, if the parameter string(s) is/are null or empty.
	 * 
	 * @param inArray	string(s)
	 */
	public static void checkNullOrEmpty(String... strArray) {
		for (String str : strArray) {
			if (str == null || str.trim().length() == 0) {
				throw new InputException(ExceptionMessage.INPUT_EMPTY);
			}
		}
	}
	
	/**
	 * Returns true, if parameter string is null or empty.
	 * 
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.trim().length() == 0;
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
	 * Converts parameter string to integer. Throws exception, if integer is 0.
	 * 
	 */
	public static int toNonZeroInt(String str) {
		int out = toInt(str);
		if (out == 0) {
			throw new InputException(ExceptionMessage.INPUT_ZERO);
		}
		return out;
	}
	
	/**
	 * Converts parameter into integer. Throws exception, if parameter cannot be converted.
	 * 
	 */
	public static int toInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			throw new InputException(ExceptionMessage.PARSING_NUMBER);
		}
	}
}