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
		"<div class=\"top-banner\">\n" +
		"<div class=\"container\">\n" +
		"<img src=\""+ contextPath + "/logo.png\" height=\"100\">\n" +
		"</div>\n" +
		"</div>\n" +
		"<div class=\"top-menu\">\n" +
		"<div class=\"container\">\n" +
		"<ul class=\"nav-list\">\n" +
		"<li><a href=\"" + contextPath + "/manager\">HOME</a></li>\n" +
		"<li><a href=\"" + contextPath + "/manager/reservation\">RESERVATION</a></li>\n" +
		"<li><a href=\"" + contextPath + "/manager/operations\">OPERATIONS</a></li>\n" +
		"<li><a href=\"" + contextPath + "/manager/log\">LOG</a></li>\n" +
		"</ul>\n" +
		"</div>\n" +
		"</div>\n" +
		"<div id=\"wrapper\">";
	}

}
