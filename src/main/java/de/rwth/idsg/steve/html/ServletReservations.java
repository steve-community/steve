package de.rwth.idsg.steve.html;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.rwth.idsg.steve.common.ClientDBAccess;

/**
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class ServletReservations extends HttpServlet {

	private static final long serialVersionUID = 1L;
	String contextPath, servletPath;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details			
		contextPath = request.getContextPath();
		servletPath = contextPath + request.getServletPath();	

		request.setAttribute("contextPath", contextPath );
		request.setAttribute("servletPath", servletPath );
		request.setAttribute("reservList", ClientDBAccess.getReservations() );
		
		request.getRequestDispatcher("/WEB-INF/jsp/data-man/reservations.jsp").forward(request, response);
	}
}
