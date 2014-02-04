package de.rwth.idsg.steve.html;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.rwth.idsg.steve.common.ClientDBAccess;

/**
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * 
 */
@WebServlet("/manager/home/*")
public class ServletHome extends HttpServlet {

	private static final long serialVersionUID = 1L;
	String contextPath, servletPath;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		

		contextPath = request.getContextPath();
		servletPath = contextPath + request.getServletPath();
		String command = request.getPathInfo();
		
		request.setAttribute("contextPath", contextPath );
		request.setAttribute("servletPath", servletPath );
		
		String path;
		
		if (command == null || command.length() == 0) {
			
			request.setAttribute("stats", ClientDBAccess.getStats());
			path = "/WEB-INF/jsp/home.jsp";
		
		} else if (command.equals("/heartbeats")) {
			
			request.setAttribute("heartbeatList", ClientDBAccess.getChargePointHeartbeats());
			path = "/WEB-INF/jsp/heartbeats.jsp";
			
		} else if (command.equals("/connectorStatus")) {
			
			request.setAttribute("connectorStatusList", ClientDBAccess.getChargePointConnectorStatus());
			path = "/WEB-INF/jsp/connectorStatus.jsp";

		} else {
			
			path = "/WEB-INF/jsp/00-error.jsp";
			
		}

		// forward the request
		request.getRequestDispatcher(path).forward(request, response);

	}
}