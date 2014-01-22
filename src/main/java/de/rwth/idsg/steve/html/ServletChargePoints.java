package de.rwth.idsg.steve.html;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.GsonBuilder;

import de.rwth.idsg.steve.common.ClientDBAccess;
import de.rwth.idsg.steve.common.utils.InputUtils;
import de.rwth.idsg.steve.model.ChargePoint;

/**
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
@WebServlet("/manager/chargepoints/*")
public class ServletChargePoints extends HttpServlet {

	private static final long serialVersionUID = 1L;
	String contextPath, servletPath;
	List<String> cpList;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details			
		contextPath = request.getContextPath();
		servletPath = contextPath + request.getServletPath();
		String command = request.getPathInfo();

		request.setAttribute("contextPath", contextPath );
		request.setAttribute("servletPath", servletPath );

		if (command == null || command.length() == 0) {
			cpList = ClientDBAccess.getChargePoints();
			request.setAttribute("cpList", cpList );			
			request.getRequestDispatcher("/WEB-INF/jsp/data-man/chargepoints.jsp").forward(request, response);

		} else if (command.equals("/getDetails")) {
			String cbi = request.getParameter("chargeBoxId");
			InputUtils.checkNullOrEmpty(cbi);
			
			ChargePoint cp = ClientDBAccess.getChargePointDetails(cbi);			
			if (cp != null) {				
				GsonBuilder gb = new GsonBuilder();
				gb.registerTypeAdapter(String.class, new GsonStringAdapter());
				
		        response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");
		        response.getWriter().write(gb.create().toJson(cp));
			} else {
				request.setAttribute("cpList", cpList );
				request.getRequestDispatcher("/WEB-INF/jsp/data-man/chargepoints.jsp").forward(request, response);
			}
		}
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