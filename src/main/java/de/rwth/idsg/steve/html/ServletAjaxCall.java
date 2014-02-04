package de.rwth.idsg.steve.html;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.GsonBuilder;

import de.rwth.idsg.steve.common.ClientDBAccess;
import de.rwth.idsg.steve.common.utils.InputUtils;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@WebServlet("/manager/ajax/*")
public class ServletAjaxCall extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		String command = request.getPathInfo();
		String cbi = request.getParameter("chargeBoxId");
		InputUtils.checkNullOrEmpty(cbi);
		
		if (command.equals("/getCPDetails")) {

			writeResponse(response, ClientDBAccess.getChargePointDetails(cbi) );
			
		} else if (command.equals("/getConnectorIds")) {
		
			writeResponse(response, ClientDBAccess.getConnectorIds(cbi) );
	        
		} else if (command.equals("/getTransactionIds")) {

			writeResponse(response, ClientDBAccess.getActiveTransactionIds(cbi) );

		} else if (command.equals("/getReservationIds")) {

			writeResponse(response, ClientDBAccess.getExistingReservationIds(cbi) );
		}
	}

	private static void writeResponse(HttpServletResponse response, Object obj) throws IOException {		
		if (obj == null) return;
		
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(String.class, new GsonStringAdapter());

		String result = gb.create().toJson(obj);		
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(result);
	}
}
