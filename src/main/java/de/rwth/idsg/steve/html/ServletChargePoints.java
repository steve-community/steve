package de.rwth.idsg.steve.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.rwth.idsg.steve.common.ClientDBAccess;
import de.rwth.idsg.steve.common.Utils;

public class ServletChargePoints extends HttpServlet {

	private static final long serialVersionUID = 8576766110806723303L;
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
				+ printChargeBoxes()
				+ printAddChargeBox()
				+ printDeleteChargeBox()
				+ Common.printFoot(contextPath));
		
		writer.close();	
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String command = request.getPathInfo();	
		String chargeBoxId = request.getParameter("chargeBoxId");
		
		if (chargeBoxId == null || chargeBoxId.isEmpty()) {
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}
		
		if (command.equals("/add")){
			ClientDBAccess.addChargePoint(chargeBoxId);
			
		} else if (command.equals("/delete")){
			ClientDBAccess.deleteChargePoint(chargeBoxId);
			
		}		
		response.sendRedirect(contextPath + servletPath);
		return;
	}	

	private String printChargeBoxes() {
		
		StringBuilder builder = new StringBuilder(
				"<h3><span>Registered Charge Points</span></h3>\n"
				+ "<center>\n"
				+ "<table class=\"res\">\n"
				+ "<tr><th>chargeBoxId</th><th>endpoint_address</th><th>ocppVersion</th><th>chargePointVendor</th><th>chargePointModel</th><th>fwVersion</th></tr>\n");
		
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT chargeboxId, endpoint_address, ocppVersion, chargePointVendor, chargePointModel, fwVersion FROM chargebox;");
			rs = pt.executeQuery();

			while ( rs.next() ) {
				builder.append("<tr>"
						+ "<td>" + rs.getString(1) + "</td>"
						+ "<td>" + rs.getString(2) + "</td>"
						+ "<td>" + rs.getString(3) + "</td>"
						+ "<td>" + rs.getString(4) + "</td>"
						+ "<td>" + rs.getString(5) + "</td>"
						+ "<td>" + rs.getString(6) + "</td>"
						+ "</tr>\n");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);
		}			
		builder.append("</table>\n</center>\n<br>\n");
		return builder.toString();
	}

	private String printAddChargeBox() {		
		StringBuilder builder = new StringBuilder(
				"<h3><span>Add A New Charge Point</span></h3>\n"
				+ "<center>\n"
				+ "<form method=\"POST\" action=\"" + contextPath + servletPath + "/add\">\n"
				+ "<table class=\"bc\">\n"		
				+ "<tr><td>chargeBoxId (string):</td><td><input type=\"text\" name=\"chargeBoxId\"></td></tr>\n"
				+ "<tr><td><i>chargeBoxId is sufficient enough to register a charge point.<br> "
				+ "After every reset of a charge point the remaining fields are updated.</i></td><td></td></tr>\n"
				+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Add\"></td></tr>\n" 	   	
				+ "</table>\n"		
				+ "</form>\n"
				+ "</center>\n<br>\n");		
		return builder.toString();
	}

	private String printDeleteChargeBox() {
		StringBuilder builder = new StringBuilder(
				"<h3><span>Delete A Charge Point</span></h3>\n"
				+ "<center>\n"	
				+ "<form method=\"POST\" action=\""+ contextPath + servletPath + "/delete\">\n"
				+ "<table class=\"bc\">\n"
				+ "<tr><td>chargeBoxId (string):</td><td><input type=\"text\" name=\"chargeBoxId\"></td></tr>\n"
				+ "<tr><td><i><b>Warning:</b> Deleting a charge point causes losing all related information including<br>"
				+ "transactions, reservations, connector status and connector meter values.</i></td><td></td></tr>\n"
				+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Delete\"></td></tr>\n"
				+ "</table>\n"
				+ "</form>\n"
				+ "</center>\n");
		return builder.toString();
	}
}
