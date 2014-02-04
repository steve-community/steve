package de.rwth.idsg.steve.html;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.rwth.idsg.steve.common.ClientDBAccess;
import de.rwth.idsg.steve.common.utils.InputUtils;

/**
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
@WebServlet("/manager/chargepoints/*")
public class ServletChargePoints extends HttpServlet {

	private static final long serialVersionUID = 1L;
	String contextPath, servletPath;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details			
		contextPath = request.getContextPath();
		servletPath = contextPath + request.getServletPath();

		request.setAttribute("contextPath", contextPath );
		request.setAttribute("servletPath", servletPath );
		request.setAttribute("cpList", ClientDBAccess.getChargePoints());
		
		request.getRequestDispatcher("/WEB-INF/jsp/data-man/chargepoints.jsp").forward(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String command = request.getPathInfo();
		String chargeBoxId = request.getParameter("chargeBoxId");
		InputUtils.checkNullOrEmpty(chargeBoxId);

		if (command.equals("/add")){
			ClientDBAccess.addChargePoint(chargeBoxId);

		} else if (command.equals("/delete")){
			ClientDBAccess.deleteChargePoint(chargeBoxId);			
		}

		response.sendRedirect(servletPath);
		return;
	}
}