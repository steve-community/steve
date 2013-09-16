package html;

public class CPS_Common {
	
	public static String printHead(String contextPath){
		return
		"<!DOCTYPE html>\n" +
		"<html>\n" +
		"<head>\n" +
		"<link rel=\"stylesheet\" type=\"text/css\" href=\"" + contextPath + "/style.css\">\n" +
		"<script src=\"" + contextPath + "/script.js\" type=\"text/javascript\"></script>\n" +
		"<title>SteVe - Steckdosenverwaltung</title>\n" +
		"</head>\n" +
		"<body>\n" +
		"<div class=\"main\">\n" +
		"<div class=\"top-banner\">\n" +
		"<div class=\"container\">\n" +
		"<a href=\"" + contextPath + "/manager\"><img src=\""+ contextPath + "/logo.png\" height=\"100\"></a>\n" +
		"</div></div>\n" +
		"<div class=\"top-menu\">\n" +
		"<div class=\"container\">\n" +
		"<ul class=\"nav-list\">\n" +
		"<li><a href=\"" + contextPath + "/manager\">HOME</a></li>\n" +
		"<li><a href=\"" + contextPath + "/manager/reservation\">RESERVATION</a></li>\n" +
		"<li><a href=\"" + contextPath + "/manager/operations\">OPERATIONS</a></li>\n" +
		"<li><a href=\"" + contextPath + "/manager/log\">LOG</a></li>\n" +
		"</ul>\n" +
		"</div></div>\n" +
		"<div class=\"main-wrapper\">\n" +
		"<div class=\"content\">";
	}
	
	public static String printFoot(String contextPath){
		return
		"</div></div></div>\n" +
		"<div class=\"footer\">\n" +
		"<a href=\"http://www.rwth-aachen.de\"><img src=\""+ contextPath + "/logo_rwth.png\"></a>\n" + 
		"<a href=\"http://dbis.rwth-aachen.de\"><img src=\""+ contextPath + "/logo_i5.png\"></a>\n" + 
		"<a href=\"http://dbis.rwth-aachen.de/cms/teaching/IDSG\"><img src=\""+ contextPath + "/logo_idsg.png\"></a>\n" + 
		"</div>\n" +
		"</body>\n" +
		"</html>";
	}
}
