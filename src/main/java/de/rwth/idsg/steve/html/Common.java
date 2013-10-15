package de.rwth.idsg.steve.html;

/**
*
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class Common {
	
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
	
	public static String printHead(String contextPath){
		return
		"<!DOCTYPE html>\n"
		+ "<html>\n"
		+ "<head>\n"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + contextPath + "/style.css\">\n"
		+ "<script src=\"" + contextPath + "/script.js\" type=\"text/javascript\"></script>\n"
		+ "<title>SteVe - Steckdosenverwaltung</title>\n"
		+ "</head>\n"
		+ "<body>\n"
		+ "<div class=\"main\">\n"
		+ "<div class=\"top-banner\">\n"
		+ "<div class=\"container\">\n"
		+ "<a href=\"" + contextPath + "/manager\"><img src=\""+ contextPath + "/logo2.png\" height=\"80\"></a>\n"
		+ "</div></div>\n"
		+ "<div class=\"top-menu\">\n"
		+ "<div class=\"container\">\n"
		+ "<ul class=\"navigation\">\n"
		+ "<li><a href=\"" + contextPath + "/manager\">HOME</a></li>\n"		
		+ "<li><a>DATA MANAGEMENT &raquo;</a>\n"
		+ "<ul>\n"
		+ "<li><a href=\"" + contextPath + "/manager/reservations\">RESERVATIONS</a></li>\n"
		+ "<li><a href=\"" + contextPath + "/manager/chargepoints\">CHARGE POINTS</a></li>\n"
		+ "<li><a href=\"" + contextPath + "/manager/users\">USERS</a></li>\n"
		+ "</ul>\n"
		+ "</li>\n"		
		+ "<li><a>OPERATIONS &raquo;</a>\n"
		+ "<ul>\n"
		+ "<li><a href=\"" + contextPath + "/manager/operations/v1.2\">OCPP v1.2</a></li>\n"
		+ "<li><a href=\"" + contextPath + "/manager/operations/v1.5\">OCPP v1.5</a></li>\n"
		+ "</ul>\n"
		+ "</li>\n"
		+ "<li><a href=\"" + contextPath + "/manager/settings\">SETTINGS</a></li>\n"
		+ "<li><a href=\"" + contextPath + "/manager/log\">LOG</a></li>\n"
		+ "</ul>\n"
		+ "</div></div>\n"
		+ "<div class=\"main-wrapper\">\n"
		+ "<div class=\"content\">\n";
	}
	
	public static String printFoot(String contextPath){
		return
		"</div></div></div>\n"
		+ "<div class=\"footer\">\n"
		+ "<a href=\"http://www.rwth-aachen.de\"><img src=\""+ contextPath + "/logo_rwth.png\"></a>\n"
		+ "<a href=\"http://dbis.rwth-aachen.de\"><img src=\""+ contextPath + "/logo_i5.png\"></a>\n"
		+ "</div>\n"
		+ "</body>\n"
		+ "</html>";
	}
}