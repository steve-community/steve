package de.rwth.idsg.steve.html;

public class ExceptionMessage {
	
	public static String EXCEPTION_INPUT_EMPTY = 
			"Error: One or more input fields were empty.\n"
			+ "Go back and try again.";
	
	public static String EXCEPTION_PARSING_NUMBER = 
			"Error: One input field should be a number, but wasn't.\n"
			+ "Go back and try again.";
	
	public static String EXCEPTION_INPUT_ZERO = 
			"Error: Numeric input field should be greater than zero, but wasn't.\n"
			+ "Go back and try again.";
	
	public static String EXCEPTION_CHARGEPOINTS_NULL = 
			"Error: You did not select any charge points, did you!?\n"
			+ "Go back and try again.";
	
	public static String EXCEPTION_CONFKEYS_NULL = 
			"Error: You did not select any configuration keys, did you!?\n"
			+ "Go back and try again.";
	
	public static String EXCEPTION_PARSING_DATETIME = 
			"Error: Date/time input(s) must match the expected pattern.\n"
			+ "Go back and try again.";
	
	public static String EXCEPTION_INVALID_DATETIME = 
			"Error: Invalid startDatetime and/or stopDatetime.\n"
			+ "Allowed input:\n"
			+ "1. startDatetime must be before the stopDatetime.\n"
			+ "2. startDatetime must be in the future.\n"
			+ "Go back and try again.";
	
	public static String EXCEPTION_OVERLAPPING_RESERVATION = 
			"Error: The desired reservation overlaps with another reservation.\n"
			+ "Go back and try again.";
}
