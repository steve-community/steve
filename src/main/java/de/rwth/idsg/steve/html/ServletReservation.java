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

import org.joda.time.DateTime;

import de.rwth.idsg.steve.common.ClientDBAccess;
import de.rwth.idsg.steve.common.Utils;

public class ServletReservation extends HttpServlet {

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
				+ printExistingReservations()
				+ printBookReservation()
				+ printCancelReservation()
				+ Common.printFoot(contextPath));
		
		writer.close();	
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String command = request.getPathInfo();	

		if (command.equals("/book")){
			String idTag = request.getParameter("idTag");
			String chargeBoxId = request.getParameter("chargeBoxId");
			String startString = request.getParameter("startDatetime");
			String expiryString = request.getParameter("expiryDatetime");
			
			DateTime startDateTime = Utils.convertToDateTime(startString);
			DateTime expiryDateTime = Utils.convertToDateTime(expiryString);

			ClientDBAccess.bookReservation(idTag, chargeBoxId, startDateTime, expiryDateTime);
			
		} else if (command.equals("/cancel")){
			int reservation_pk = Integer.parseInt(request.getParameter("reservation_pk"));
			ClientDBAccess.cancelReservation(reservation_pk);
		}		
		response.sendRedirect(contextPath + servletPath);
		return;
	}

	private String printExistingReservations() {
		StringBuilder builder = new StringBuilder(
				"<b>Existing Reservations</b><hr>\n"
				+ "<center>\n"
				+ "<table class=\"res\">\n"
				+ "<tr><th>Reservation Id</th><th>idTag</th><th>chargeBoxId</th><th>startDatetime</th><th>expiryDatetime</th><th>ended</th></tr>\n");

		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT reservation_pk, idTag, chargeBoxId, DATE_FORMAT(startDatetime, '%Y-%m-%d %H:%i'), "
					+ "DATE_FORMAT(expiryDatetime, '%Y-%m-%d %H:%i'), ended FROM reservation");
			rs = pt.executeQuery();

			while( rs.next() ) {
				builder.append("<tr>"
						+ "<td>" + rs.getInt(1) + "</td>"
						+ "<td>" + rs.getString(2) + "</td>"
						+ "<td>" + rs.getString(3) + "</td>"
						+ "<td>" + rs.getString(4) + "</td>"
						+ "<td>" + rs.getString(5) + "</td>"
						+ "<td>" + rs.getBoolean(6) + "</td>"
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

	private String printBookReservation() {		
		StringBuilder builder = new StringBuilder(
				"<b>Book A New Reservation</b><hr>\n"
				+ "<center>\n"
				+ "<form method=\"POST\" action=\"" + contextPath + servletPath + "/book\">\n"
				+ "<table class=\"bc\">\n"		
				+ "<tr><td>idTag (of the user):</td><td><input type=\"text\" name=\"idTag\"></td></tr>\n"
				+ "<tr><td>chargeBoxId (of the charge point):</td><td><input type=\"text\" name=\"chargeBoxId\"></td></tr>\n"
				+ "<tr><td>Start date and time (ex: 2011-12-21 11:30):</td><td><input type=\"text\" name=\"startDatetime\"></td></tr>\n"
				+ "<tr><td>Expiry date and time (ex: 2011-12-21 11:30):</td><td><input type=\"text\" name=\"expiryDatetime\"></td></tr>\n"
				+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Book\"></td></tr>\n" 	   	
				+ "</table>\n"		
				+ "</form>\n"
				+ "</center>\n<br>\n");		
		return builder.toString();
	}

	private String printCancelReservation() {
		StringBuilder builder = new StringBuilder(
				"<b>Cancel An Existing Reservation</b><hr>\n"
				+ "<center>\n"	
				+ "<form method=\"POST\" action=\""+ contextPath + servletPath + "/cancel\">\n"
				+ "<table class=\"bc\">\n"
				+ "<tr><td>Reservation Id:</td><td><input type=\"number\" min=\"1\" name=\"reservation_pk\"></td></tr>\n"
				+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Delete\"></td></tr>\n"
				+ "</table>\n"
				+ "</form>\n"
				+ "</center>\n");
		return builder.toString();
	}
}
