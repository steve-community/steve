package de.rwth.idsg.steve.html;

import java.io.IOException;

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

		contextPath = request.getContextPath();
		servletPath = contextPath + request.getServletPath();
		
		request.setAttribute("contextPath", contextPath );
		request.setAttribute("servletPath", servletPath );
		
		request.setAttribute("version", Constants.STEVE_VERSION );
		request.setAttribute("dbversion", ClientDBAccess.getDBVersion() );
		
		// forward the request
		request.getRequestDispatcher("/WEB-INF/jsp/about.jsp").forward(request, response);
	}
}
