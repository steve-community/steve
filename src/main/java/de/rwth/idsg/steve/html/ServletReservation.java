package de.rwth.idsg.steve.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth.idsg.steve.common.Utils;

public class ServletReservation extends HttpServlet {

	private static final long serialVersionUID = 8576766110806723303L;
	private static final Logger LOG = LoggerFactory.getLogger(ServletReservation.class);
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
				+ printDeleteReservation()
				+ Common.printFoot(contextPath));
		
		writer.close();	
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String command = request.getPathInfo();	

		if (command.equals("/book")){
			String idTag = request.getParameter("idTag");
			String chargeBoxId = request.getParameter("chargeBoxId");
			String startDatetime = request.getParameter("startDatetime");
			String stopDatetime = request.getParameter("stopDatetime");
			
			// Check the dates first
			areDatesValid(startDatetime, stopDatetime);

			bookReservation(idTag, chargeBoxId, startDatetime, stopDatetime);

		} else if (command.equals("/delete")){
			int connector_pk = Integer.parseInt(request.getParameter("reservation_pk"));
			deleteReservation(connector_pk);
		}		
		response.sendRedirect(contextPath + servletPath);
		return;
	}

	private void bookReservation(String idTag, String chargeBoxId, String startDatetime, String stopDatetime) {

		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			
			// Check overlapping first
			isOverlapping(connect, pt, startDatetime, stopDatetime);
			
			connect.setAutoCommit(false);
			pt = connect.prepareStatement("INSERT INTO reservation (idTag, chargeBoxId, startDatetime, stopDatetime) VALUES (?,?,?,?)");

			// Set the parameter indices  
			pt.setString(1, idTag);
			pt.setString(2, chargeBoxId);
			pt.setString(3, startDatetime);
			pt.setString(4, stopDatetime);
			// Insert the new status
			int count = pt.executeUpdate();
			// Validate the change
			if (count == 1) {
				connect.commit();
			}else{
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
			}
			connect.setAutoCommit(true);
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			Utils.releaseResources(connect, pt, null);
		}
	}

	/**
	 * Throws exception, if there are rows whose date/time ranges overlap with the input
	 *
	 */
	private void isOverlapping(Connection connect, PreparedStatement pt, String inputStartDatetime, String inputStopDatetime) {
		
		ResultSet rs = null;
		try {
			// This WHERE clause covers all three cases
			pt = connect.prepareStatement("SELECT 1 FROM reservation WHERE ? <= stopDatetime AND ? >= startDatetime");
			pt.setString(1, inputStartDatetime);
			pt.setString(2, inputStopDatetime);

			rs = pt.executeQuery();
			// If the result set does have an entry, then there are overlaps
			if ( rs.next() ) {
				throw new InputException(Common.EXCEPTION_OVERLAPPING_RESERVATION);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			Utils.releaseResources(null, pt, rs);
		}
	}

	private void deleteReservation(int reservation_pk) {

		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			connect.setAutoCommit(false);
			pt = connect.prepareStatement("DELETE FROM reservation WHERE reservation_pk=?");

			// Set the parameter indices  
			pt.setInt(1, reservation_pk);
			// Execute the query
			int count = pt.executeUpdate();
			// Validate the change
			if (count == 1) {
				connect.commit();
			}else{
				LOG.error("Transaction is being rolled back.");
				connect.rollback();
			}
			connect.setAutoCommit(true);
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			Utils.releaseResources(connect, pt, null);
		}		
	}	

	/**
	 * Throws exception, if
	 * 1. the syntax does NOT match the expected pattern
	 * 2. startDatetime > stopDatetime
	 * 3. now > startDatetime
	 *
	 */
	private void areDatesValid(String startDatetime, String stopDatetime) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sdf.setLenient(false);

		Date start = null;
		Date stop = null;
		try {
			start = sdf.parse(startDatetime);
			stop = sdf.parse(stopDatetime);
		} catch (ParseException e) {
			throw new InputException(Common.EXCEPTION_PARSING_DATETIME);
		}

		Date now = new Date();
		if ( !(now.before(start) && start.before(stop)) ) {
			throw new InputException(Common.EXCEPTION_INVALID_DATETIME);
		}
	}

	private String printExistingReservations() {
		StringBuilder builder = new StringBuilder(
				"<b>Existing Reservations</b><hr>\n"
				+ "<center>\n"
				+ "<table class=\"res\">\n"
				+ "<tr><th>reservation_pk</th><th>idTag</th><th>chargeBoxId</th><th>startDatetime</th><th>stopDatetime</th><th>active</th></tr>\n");

		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT reservation_pk, idTag, chargeBoxId, DATE_FORMAT(startDatetime, '%Y-%m-%d %H:%i'), "
					+ "DATE_FORMAT(stopDatetime, '%Y-%m-%d %H:%i'), active FROM reservation");
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
				+ "<table>\n"		
				+ "<tr><td>idTag (of the user):</td><td><input type=\"text\" name=\"idTag\"></td></tr>\n"
				+ "<tr><td>chargeBoxId (of the charge point):</td><td><input type=\"text\" name=\"chargeBoxId\"></td></tr>\n"
				+ "<tr><td>Start date and time (ex: 2011-12-21 11:30):</td><td><input type=\"text\" name=\"startDatetime\"></td></tr>\n"
				+ "<tr><td>Stop date and time (ex: 2011-12-21 11:30):</td><td><input type=\"text\" name=\"stopDatetime\"></td></tr>\n"
				+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Book\"></td></tr>\n" 	   	
				+ "</table>\n"		
				+ "</form>\n"
				+ "</center>\n<br>\n");		
		return builder.toString();
	}

	private String printDeleteReservation() {
		StringBuilder builder = new StringBuilder(
				"<b>Delete An Existing Reservation</b><hr>\n"
				+ "<center>\n"	
				+ "<form method=\"POST\" action=\""+ contextPath + servletPath + "/delete\">\n"
				+ "<table>\n"
				+ "<tr><td>reservation_pk:</td><td><input type=\"number\" min=\"1\" name=\"reservation_pk\"></td></tr>\n"
				+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Delete\"></td></tr>\n"
				+ "</table>\n"
				+ "</form>\n"
				+ "</center>\n");
		return builder.toString();
	}
}
