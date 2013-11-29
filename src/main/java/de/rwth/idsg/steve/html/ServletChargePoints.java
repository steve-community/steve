package de.rwth.idsg.steve.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.rwth.idsg.steve.common.ClientDBAccess;
import de.rwth.idsg.steve.common.Utils;

public class ServletChargePoints extends HttpServlet {

	private static final long serialVersionUID = 8576766110806723303L;
	String contextPath, servletPath;
	List<String> list;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details			
		contextPath = request.getContextPath();
		servletPath = contextPath + request.getServletPath();
		String command = request.getPathInfo();

		StringBuilder mainBuilder = new StringBuilder(Common.printHead(contextPath));
		
		if (command == null || command.length() == 0) {
			list = ClientDBAccess.getChargePoints();
			mainBuilder.append(printChargeBoxes(null));

		} else if (command.equals("/getDetails")) {
			mainBuilder.append(printChargeBoxes(request.getParameter("chargeBoxId")));
		}
		
		mainBuilder.append(
				printAddChargeBox()
				+ printDeleteChargeBox()
				+ Common.printFoot(contextPath));
		
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		writer.write(mainBuilder.toString());
		writer.close();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String command = request.getPathInfo();
		String chargeBoxId = request.getParameter("chargeBoxId");

		if (chargeBoxId == null || chargeBoxId.isEmpty()) {
			throw new InputException(ExceptionMessage.INPUT_EMPTY);
		}
		
		if (command.equals("/add")){
			ClientDBAccess.addChargePoint(chargeBoxId);

		} else if (command.equals("/delete")){
			ClientDBAccess.deleteChargePoint(chargeBoxId);			
		}
		response.sendRedirect(servletPath);
		return;
	}
	
	private String printChargeBoxes(String chargeBoxId) {
		StringBuilder builder = new StringBuilder(
				"<h3><span>Registered Charge Points</span></h3>\n"
						+ "<center>\n"
						+ "<form method=\"GET\" action=\"" + servletPath + "/getDetails\">\n"
						+ "<select name=\"chargeBoxId\">\n");		
		
		String cbd = "";
		if (chargeBoxId == null) {
			for (String item : list) {
				builder.append("<option value=\"" + item + "\">" + item + "</option>\n");
			}
		} else {
			String selected = null;	
			for (String item : list) {
				if (chargeBoxId.equals(item)) selected = " selected";			
				else selected = "";
				builder.append("<option value=\"" + item + "\""+ selected +">" + item + "</option>\n");			
			}
			cbd = printChargeBoxDetails(chargeBoxId);
		}
		
		builder.append("</select>\n"
				+ "<input type=\"submit\" value=\"Get Details\">\n"
				+ "</form><br>\n"
				+ cbd
				+ "</center>\n<br>\n");
		return builder.toString();
	}

	private String printChargeBoxDetails(String chargeBoxId) {		
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT * FROM chargebox WHERE chargeBoxId=?;");
			pt.setString(1, chargeBoxId);
			rs = pt.executeQuery();
			
			String details = "";
			if ( rs.next() ) {
				details = "<table class=\"cpd\">\n"
						+ "<tr><th colspan=\"2\">Charge Point Details</th></tr>\n"
						+ "<tr><td>chargeBoxId</td><td>" + rs.getString(1) + "</td></tr>\n"
						+ "<tr><td>endpoint_address</td><td>" + rs.getString(2) + "</td></tr>\n"
						+ "<tr><td>ocppVersion</td><td>" + rs.getString(3) + "</td></tr>\n"
						+ "<tr><td>chargePointVendor</td><td>" + rs.getString(4) + "</td></tr>\n"
						+ "<tr><td>chargePointModel</td><td>" + rs.getString(5) + "</td></tr>\n"
						+ "<tr><td>chargePointSerialNumber</td><td>" + rs.getString(6) + "</td></tr>\n"
						+ "<tr><td>chargeBoxSerialNumber</td><td>" + rs.getString(7) + "</td></tr>\n"
						+ "<tr><td>firewireVersion</td><td>" + rs.getString(8) + "</td></tr>\n"
						+ "<tr><td>firewireUpdateStatus</td><td>" + rs.getString(9) + "</td></tr>\n"
						+ "<tr><td>firewireUpdateTimestamp</td><td>" + rs.getTimestamp(10) + "</td></tr>\n"
						+ "<tr><td>iccid</td><td>" + rs.getString(11) + "</td></tr>\n"
						+ "<tr><td>imsi</td><td>" + rs.getString(12) + "</td></tr>\n"
						+ "<tr><td>meterType</td><td>" + rs.getString(13) + "</td></tr>\n"
						+ "<tr><td>meterSerialNumber</td><td>" + rs.getString(14) + "</td></tr>\n"
						+ "<tr><td>diagnosticsStatus</td><td>" + rs.getString(15) + "</td></tr>\n"
						+ "<tr><td>diagnosticsTimestamp</td><td>" + rs.getTimestamp(16) + "</td></tr>\n"
						+ "</table>\n";
			}
			return details;
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);
		}		
	}

	private String printAddChargeBox() {		
		return
		"<h3><span>Add A New Charge Point</span></h3>\n"
		+ "<center>\n"
		+ "<form method=\"POST\" action=\"" + servletPath + "/add\">\n"
		+ "<table class=\"bc\">\n"		
		+ "<tr><td>chargeBoxId (string):</td><td><input type=\"text\" name=\"chargeBoxId\"></td></tr>\n"
		+ "<tr><td><i>chargeBoxId is sufficient enough to register a charge point.<br> "
		+ "After every reset of a charge point the remaining fields are updated.</i></td><td></td></tr>\n"
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Add\"></td></tr>\n" 	   	
		+ "</table>\n"		
		+ "</form>\n"
		+ "</center>\n<br>\n";	
	}

	private String printDeleteChargeBox() {
		return
		"<h3><span>Delete A Charge Point</span></h3>\n"
		+ "<center>\n"	
		+ "<form method=\"POST\" action=\""+ servletPath + "/delete\">\n"
		+ "<table class=\"bc\">\n"
		+ "<tr><td>chargeBoxId (string):</td><td><input type=\"text\" name=\"chargeBoxId\"></td></tr>\n"
		+ "<tr><td><i><b>Warning:</b> Deleting a charge point causes losing all related information including<br>"
		+ "transactions, reservations, connector status and connector meter values.</i></td><td></td></tr>\n"
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Delete\"></td></tr>\n"
		+ "</table>\n"
		+ "</form>\n"
		+ "</center>\n";
	}
}