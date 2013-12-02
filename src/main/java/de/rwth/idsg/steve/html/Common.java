package de.rwth.idsg.steve.html;

import java.util.HashMap;

/**
*
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class Common {
	
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
		+ "<a href=\"" + contextPath + "/manager\"><img src=\""+ contextPath + "/images/logo2.png\" height=\"80\"></a>\n"
		+ "</div></div>\n"
		+ "<div class=\"top-menu\">\n"
		+ "<div class=\"container\">\n"
		+ "<ul class=\"navigation\">\n"
		+ "<li><a href=\"" + contextPath + "/manager\">HOME</a></li>\n"		
		+ "<li><a>DATA MANAGEMENT &raquo;</a>\n"
		+ "<ul>\n"
		+ "<li><a href=\"" + contextPath + "/manager/chargepoints\">CHARGE POINTS</a></li>\n"
		+ "<li><a href=\"" + contextPath + "/manager/users\">USERS</a></li>\n"
		+ "<li><a href=\"" + contextPath + "/manager/reservations\">RESERVATIONS</a></li>\n"
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
		+ "<li><a href=\"" + contextPath + "/manager/about\">ABOUT</a></li>\n"
		+ "</ul>\n"
		+ "</div></div>\n"
		+ "<div class=\"main-wrapper\">\n"
		+ "<div class=\"content\">\n";
	}
	
	public static String printFoot(String contextPath){
		return
		"</div></div></div>\n"
		+ "<div class=\"footer\">\n"
		+ "<a href=\"http://www.rwth-aachen.de\"><img src=\""+ contextPath + "/images/logo_rwth.png\"></a>\n"
		+ "<a href=\"http://dbis.rwth-aachen.de\"><img src=\""+ contextPath + "/images/logo_i5.png\"></a>\n"
		+ "</div>\n"
		+ "</body>\n"
		+ "</html>";
	}
	
	public static String printChargePointsMultipleSelect(HashMap<String,String> chargePointsList, String ocppVersion) {		
		StringBuilder builder = new StringBuilder(
				"<h3><span>Charge Points with OCPP v" + ocppVersion + "</span></h3>\n"
				+ "<table>\n"
				+ "<tr><td style=\"vertical-align:top\">"
				+ "<input type=\"button\" value=\"Select All\" onClick=\"selectAll(document.getElementById('cp_items'))\">\n"
				+ "<input type=\"button\" value=\"Select None\" onClick=\"selectNone(document.getElementById('cp_items'))\">\n"
				+ "</td>\n"
				+ "<td><select name=\"cp_items\" id=\"cp_items\" size=\"5\" multiple>\n");

		for (String key : chargePointsList.keySet()) {
			String value = chargePointsList.get(key);
			builder.append("<option value=\"" + key + ";" + value + "\">" + key + " &#8212; " + value + "</option>\n");
		}		
		builder.append("</select></td>\n</tr>\n</table>\n<br>\n");
		return builder.toString();
	}
	
	public static String printChargePointsSingleSelect(HashMap<String,String> chargePointsList, String ocppVersion) {		
		StringBuilder builder = new StringBuilder(
				"<h3><span>Charge Points with OCPP v" + ocppVersion + "</span></h3>\n"
				+ "<table>\n"
				+ "<tr><td style=\"vertical-align:top\">Select one:</td>\n"
				+ "<td><select name=\"cp_items\" id=\"cp_items\" size=\"5\">\n");

		for (String key : chargePointsList.keySet()) {
			String value = chargePointsList.get(key);
			builder.append("<option value=\"" + key + ";" + value + "\">" + key + " &#8212; " + value + "</option>\n");
		}		
		builder.append("</select>\n</td>\n</tr>\n</table>\n<br>\n");
		return builder.toString();
	}
}