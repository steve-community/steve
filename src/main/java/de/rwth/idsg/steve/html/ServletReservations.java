package de.rwth.idsg.steve.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth.idsg.steve.common.Utils;

/**
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class ServletReservations extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(ServletReservations.class);
	private static final long serialVersionUID = 8576766110806723303L;
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
				+ printExistingReservations()
				//+ printBookReservation()
				//+ printCancelReservation()
				+ Common.printFoot(contextPath));
		
		writer.close();	
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

//		String command = request.getPathInfo();	
//
//		if (command.equals("/book")){
//			String idTag = request.getParameter("idTag");
//			String chargeBoxId = request.getParameter("chargeBoxId");
//			String startString = request.getParameter("startDatetime");
//			String expiryString = request.getParameter("expiryDatetime");
//			
//			if (idTag.isEmpty()
//					|| chargeBoxId.isEmpty() 
//					|| startString.isEmpty()
//					|| expiryString.isEmpty()){
//				throw new InputException(ExceptionMessage.EXCEPTION_INPUT_EMPTY);
//			}
//			
//			DateTime startDateTime = Utils.convertToDateTime(startString);
//			DateTime expiryDateTime = Utils.convertToDateTime(expiryString);
//
//			ClientDBAccess.bookReservation(idTag, chargeBoxId, startDateTime, expiryDateTime);
//			
//		} else if (command.equals("/cancel")){
//			String reservSTR = request.getParameter("reservation_pk");
//			if (reservSTR.isEmpty()){
//				throw new InputException(ExceptionMessage.EXCEPTION_INPUT_EMPTY);
//			}
//			
//			int reservation_pk;			
//			try {
//				reservation_pk = Integer.parseInt(reservSTR);	
//			} catch (NumberFormatException e) {
//				throw new InputException(ExceptionMessage.EXCEPTION_PARSING_NUMBER);
//			}
//			ClientDBAccess.cancelReservation(reservation_pk);
//		}		
		response.sendRedirect(servletPath);
		return;
	}

	private String printExistingReservations() {
		StringBuilder builder = new StringBuilder(
				"<h3><span>Existing Reservations</span></h3>\n"
				+ "<center>\n"
				+ "<table class=\"res\">\n"
				+ "<tr><th>Reservation Id</th><th>idTag</th><th>chargeBoxId</th><th>startDatetime</th><th>expiryDatetime</th></tr>\n");

		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT * FROM reservation WHERE expiryDatetime >= NOW() ORDER BY expiryDatetime;");
			rs = pt.executeQuery();

			while ( rs.next() ) {
				Timestamp ex = rs.getTimestamp(5);
				builder.append("<tr>"
						+ "<td>" + rs.getInt(1) + "</td>"
						+ "<td>" + rs.getString(2) + "</td>"
						+ "<td>" + rs.getString(3) + "</td>"
						+ "<td>" + Utils.convertToString(rs.getTimestamp(4)) + "</td>"
						+ "<td>" + Utils.convertToString(ex)+ "</td>"
						+ "</tr>\n");
			}
		} catch (SQLException ex) {
			LOG.error("SQL exception", ex);	
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);
		}			
		builder.append("</table>\n</center>\n<br>\n");
		return builder.toString();
	}

	private String printBookReservation() {		
		return
		"<h3><span>Book A New Reservation</span></h3>\n"
		+ "<center>\n"
		+ "<form method=\"POST\" action=\"" + servletPath + "/book\">\n"
		+ "<table class=\"bc\">\n"		
		+ "<tr><td>idTag (of the user):</td><td><input type=\"text\" name=\"idTag\"></td></tr>\n"
		+ "<tr><td>chargeBoxId (of the charge point):</td><td><input type=\"text\" name=\"chargeBoxId\"></td></tr>\n"
		+ "<tr><td>Start date and time (ex: 2011-12-21 11:30):</td><td><input type=\"text\" name=\"startDatetime\"></td></tr>\n"
		+ "<tr><td>Expiry date and time (ex: 2011-12-21 11:30):</td><td><input type=\"text\" name=\"expiryDatetime\"></td></tr>\n"
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Book\"></td></tr>\n" 	   	
		+ "</table>\n"		
		+ "</form>\n"
		+ "</center>\n<br>\n";
	}

	private String printCancelReservation() {
		return
		"<h3><span>Cancel An Existing Reservation</span></h3>\n"
		+ "<center>\n"	
		+ "<form method=\"POST\" action=\""+ servletPath + "/cancel\">\n"
		+ "<table class=\"bc\">\n"
		+ "<tr><td>Reservation Id:</td><td><input type=\"number\" min=\"1\" name=\"reservation_pk\"></td></tr>\n"
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Delete\"></td></tr>\n"
		+ "</table>\n"
		+ "</form>\n"
		+ "</center>\n";
	}
}
