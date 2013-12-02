package de.rwth.idsg.steve.html;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.rwth.idsg.steve.common.ClientDBAccess;
import de.rwth.idsg.steve.common.Constants;
import de.rwth.idsg.steve.common.Updater;

/**
 * This servlet provides a Web interface to change the variables in the Constants class and to update SteVe.
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
		servletPath = contextPath + request.getServletPath();	

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

			// If heartbeat input exists
			if (heartbeatSTR != null && !heartbeatSTR.isEmpty()) {
				int heartbeat;
				try {
					heartbeat = Integer.parseInt(heartbeatSTR);	
				} catch (NumberFormatException e) {
					throw new InputException(ExceptionMessage.PARSING_NUMBER);
				}
				Constants.HEARTBEAT_INTERVAL = heartbeat;
			}

			// If expiration input exists
			if (expirationSTR != null && !expirationSTR.isEmpty()) {
				int expiration;
				try {
					expiration = Integer.parseInt(expirationSTR);	
				} catch (NumberFormatException e) {
					throw new InputException(ExceptionMessage.PARSING_NUMBER);
				}
				Constants.HOURS_TO_EXPIRE = expiration;
			}

			// If both input fields are empty
			if ((heartbeatSTR == null || heartbeatSTR.isEmpty()) && (expirationSTR == null || expirationSTR.isEmpty())) {				
				throw new InputException(ExceptionMessage.INPUT_EMPTY);
			}

			response.sendRedirect(servletPath);
			return;

		} else if (command.equals("/update")){
			String warPathSTR = request.getParameter("warPath");
			
			if (warPathSTR == null || warPathSTR.isEmpty()) {
				throw new InputException(ExceptionMessage.INPUT_EMPTY);
			} else {
				File file = new File(warPathSTR);
				if (!file.isFile()) throw new InputException(ExceptionMessage.INVALID_FILE_PATH);
			}
			
			String updating = "Update started...\nPlease revisit SteVe after a few seconds.";
			
			response.setContentType("text/plain");
			PrintWriter writer = response.getWriter();
			writer.write(updating);
			writer.close();
						
			String stevePath = getServletContext().getRealPath("");
			Updater.doUpdate(stevePath, warPathSTR);
		}
	}

	private String printSettings() {
		return
		"<h3><span>OCPP Settings</span></h3>\n"
		+ "<center>"
		+ "<form method=\"POST\" action=\"" + servletPath + "/change\">\n" 
		+ "<table class=\"bc\">\n"
		+ "<tr><td>Heartbeat Interval:</td><td><input type=\"number\" name=\"heartbeat\"></td></tr>\n"
		+ "<tr><td><i>The time interval in <b>seconds</b> for how often a charge point <br> should request the current time from SteVe.</i></td>"
		+ "<td>(Current value: " + Constants.HEARTBEAT_INTERVAL + ")</td></tr>\n"
		+ "<tr><td>Expiration:</td><td><input type=\"number\" name=\"expiration\"></td></tr>\n"
		+ "<tr><td><i>The amount of time in <b>hours</b> for how long a charge point <br> should store the authorization info of an idTag in its local white list.</i></td>"
		+ "<td>(Current value: " + Constants.HOURS_TO_EXPIRE + ")</td></tr>\n"
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Change\"></td></tr>\n"
		+ "</table></form>\n"
		
		+ "<h3><span>Update SteVe</span></h3>\n"
		+ "<form method=\"POST\" action=\"" + servletPath + "/update\">"
		+ "<table class=\"bc\">\n"
		+ "<tr><td>SteVe Version:</td><td>" + Constants.STEVE_VERSION + "</td></tr>\n"
		+ "<tr><td>SteVeDB Version:</td><td>" + ClientDBAccess.getDBVersion() + "</td></tr>\n"
		+ "<tr><td>Local WAR File Path of New Version:</td><td><input type=\"text\" name=\"warPath\"></td></tr>\n"
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Update\"></td></tr>\n"
		+ "</table></form>\n"
		+ "</center>\n";
	}
}
