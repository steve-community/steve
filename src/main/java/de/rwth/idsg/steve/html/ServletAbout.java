package de.rwth.idsg.steve.html;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.rwth.idsg.steve.common.ClientDBAccess;
import de.rwth.idsg.steve.common.Constants;

/**
 * This servlet provides information about SteVe.
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * 
 */
public class ServletAbout extends HttpServlet {

	private static final long serialVersionUID = 1L;
	String contextPath, servletPath;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details			
		contextPath = request.getContextPath();
		servletPath = contextPath + request.getServletPath();	

		PrintWriter writer = response.getWriter();		
		response.setContentType("text/html");

		writer.println(
				Common.printHead(contextPath)
				+ printAbout()
				+ Common.printFoot(contextPath));

		writer.close();	
	}

	private String printAbout() {
		return
        "<h3><span>About SteVe</span></h3>\n"
        + "<center>"
        + "<table class=\"bc\">\n"
        + "<tr><td>Version:</td><td>" + Constants.STEVE_VERSION + "</td></tr>\n"
        + "<tr><td>Database Version:</td><td>" + ClientDBAccess.getDBVersion() + "</td></tr>\n"
        + "<tr><td>GitHub Page:</td><td><a href=\"https://github.com/RWTH-i5-IDSG/steve\">https://github.com/RWTH-i5-IDSG/steve</a></td></tr>\n"
        + "</table>"
        + "</center>\n";
	}
}
