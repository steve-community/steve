package de.rwth.idsg.steve.html;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.rwth.idsg.steve.common.Constants;

/**
* This servlet provides a Web interface to change the variables in the Constants class.
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class ServletSettings extends HttpServlet {

	private static final long serialVersionUID = -8913449378037814608L;
	String contextPath, servletPath;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details			
		contextPath = request.getContextPath();
		servletPath = request.getServletPath();	

		PrintWriter writer = response.getWriter();		
		response.setContentType("text/html");
		
		writer.println(
				Common.printHead(contextPath)
				+ printSettings()
				+ Common.printFoot(contextPath));
		
		writer.close();	
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		String command = request.getPathInfo();	
		if (command.equals("/change")){
			String heartbeatSTR = request.getParameter("heartbeat");
			String expirationSTR = request.getParameter("expiration");
			
			if (!heartbeatSTR.isEmpty()) {
				int heartbeat;
				try {
					heartbeat = Integer.parseInt(heartbeatSTR);	
				} catch (NumberFormatException e) {
					throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
				}
				Constants.HEARTBEAT_INTERVAL = heartbeat;
				
			} else if (!expirationSTR.isEmpty()) {
				int expiration;
				try {
					expiration = Integer.parseInt(expirationSTR);	
				} catch (NumberFormatException e) {
					throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
				}
				Constants.HOURS_TO_EXPIRE = expiration;
				
			} else {
				// Both input fields are emptys
				throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
			}
		}
		response.sendRedirect(contextPath + servletPath);
		return;
	}

	private String printSettings() {
		return
		"<h3><span>Settings</span></h3>\n"
		+ "<center>"
		+ "<form method=\"POST\" action=\"" + contextPath + servletPath + "/change\">\n" 
		+ "<table class=\"bc\">\n"
		+ "<tr><td>Heartbeat Interval:</td><td><input type=\"number\" name=\"heartbeat\"></td></tr>\n"
		+ "<tr><td><i>The time interval in <b>seconds</b> for how often a charge point <br> should request the current time from SteVe.</i></td>"
		+ "<td>(Current value: " + Constants.HEARTBEAT_INTERVAL + ")</td></tr>\n"
		+ "<tr><td>Expiration:</td><td><input type=\"number\" name=\"expiration\"></td></tr>\n"
		+ "<tr><td><i>The amount of time in <b>hours</b> for how long a charge point <br> should store the authorization info of an idTag in its local white list.</i></td>"
		+ "<td>(Current value: " + Constants.HOURS_TO_EXPIRE + ")</td></tr>\n"
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Change\"></td></tr>\n"
		+ "</table></form></center>\n";
	}
}
