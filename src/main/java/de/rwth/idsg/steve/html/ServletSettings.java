package de.rwth.idsg.steve.html;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.rwth.idsg.steve.common.Constants;
import de.rwth.idsg.steve.common.utils.InputUtils;

/**
 * This servlet provides a Web interface to change the variables in the Constants class.
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * 
 */
public class ServletSettings extends HttpServlet {

	private static final long serialVersionUID = 1L;
	String contextPath, servletPath;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		contextPath = request.getContextPath();
		servletPath = contextPath + request.getServletPath();
		
		request.setAttribute("contextPath", contextPath );
		request.setAttribute("servletPath", servletPath );
		
		request.setAttribute("heartbeat", Constants.HEARTBEAT_INTERVAL );
		request.setAttribute("expire", Constants.HOURS_TO_EXPIRE );

		// forward the request
		request.getRequestDispatcher("/WEB-INF/jsp/settings.jsp").forward(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		

		String heartbeatSTR = request.getParameter("heartbeat");
		String expirationSTR = request.getParameter("expiration");

		// If heartbeat input exists
		if ( !InputUtils.isNullOrEmpty(heartbeatSTR) ) {
			Constants.HEARTBEAT_INTERVAL = InputUtils.toInt(heartbeatSTR);
		}

		// If expiration input exists
		if ( !InputUtils.isNullOrEmpty(expirationSTR) ) {
			Constants.HOURS_TO_EXPIRE = InputUtils.toInt(expirationSTR);
		}
		
		response.sendRedirect(request.getContextPath() + request.getServletPath());
		return;
	}
}
