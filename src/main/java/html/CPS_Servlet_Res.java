package html;

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

import common.Utils;

public class CPS_Servlet_Res extends HttpServlet {

	private static final long serialVersionUID = 8576766110806723303L;
	String contextPath, servletPath;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details			
		contextPath = request.getContextPath();
		servletPath = request.getServletPath();	

		PrintWriter writer = response.getWriter();		
		response.setContentType("text/html");
		writer.println(CPS_Common.printHead(contextPath));
		printReservationPage(writer);
		
		writer.println("</div>");
		writer.println("</body></html>");
		writer.close();	
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		PrintWriter writer = response.getWriter();
		String command = request.getPathInfo();	

		if (command.equals("/book")){
			String idTag = request.getParameter("idTag");
			String chargeBoxId = request.getParameter("chargeBoxId");
			String startDatetime = request.getParameter("startDatetime");
			String stopDatetime = request.getParameter("stopDatetime");
			
			if ( areDatesValid(startDatetime, stopDatetime) ) {				
				if ( bookReservation(idTag, chargeBoxId, startDatetime, stopDatetime) ){
					response.sendRedirect(contextPath + servletPath);
					return;
				}				
				response.setContentType("text/plain");
				writer.println("The desired reservation overlaps with another reservation.");
				writer.println("Go back and try again.");
				writer.close();
				return;
			}
			
			response.setContentType("text/plain");
			writer.println("Invalid startDatetime and/or stopDatetime. Allowed input:");
			writer.println("1. startDatetime and stopDatetime must match the expected pattern.");
			writer.println("2. startDatetime must be before the stopDatetime.");
			writer.println("3. startDatetime must be in the future.");
			writer.println("Go back and try again.");
			writer.close();

		} else if (command.equals("/delete")){
			int connector_pk = Integer.parseInt(request.getParameter("reservation_pk"));

			deleteReservation(connector_pk);
			response.sendRedirect(contextPath + servletPath);
			return;
		}
	}
	
	private boolean bookReservation(String idTag, String chargeBoxId, String startDatetime, String stopDatetime) {

		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();

			if ( isOverlapping(connect, pt, startDatetime, stopDatetime) ){
				// The reservation cannot be booked
				return false;
			}
			
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
			Utils.validateDMLChanges(count);          
			// Now we can commit
			connect.commit();
			// The reservation is booked
			return true;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, null);
		}		
	}
	
	/**
	 * Returns true, if there are rows whose date/time ranges overlap with the input
	 *
	 */
	private boolean isOverlapping(Connection connect, PreparedStatement pt, String inputStartDatetime, String inputStopDatetime) {
		
		ResultSet rs = null;
		boolean overlaps = true;
		try {
			// This WHERE clause covers all three cases
			pt = connect.prepareStatement("SELECT 1 FROM reservation WHERE ? <= stopDatetime AND ? >= startDatetime");
			pt.setString(1, inputStartDatetime);
			pt.setString(2, inputStopDatetime);
			
			rs = pt.executeQuery();
			// If the result set does NOT have an entry, then there are no overlaps
			if ( !rs.next() ) overlaps = false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Utils.releaseResources(null, pt, rs);
		}
		return overlaps;
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

			pt.executeUpdate();
			connect.commit();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, null);
		}		
	}	
	
	/**
	 * Returns false, if
	 * 1. the syntax does NOT match the expected pattern
	 * 2. startDatetime > stopDatetime
	 * 3. now > startDatetime
	 *
	 */
	private boolean areDatesValid(String startDatetime, String stopDatetime) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sdf.setLenient(false);
		
		Date start = null;
		Date stop = null;
		try {
			start = sdf.parse(startDatetime);
		    stop = sdf.parse(stopDatetime);
		} catch (ParseException e) {
		    //e.printStackTrace();
		    return false;
		}

		Date now = new Date();
		return ( now.before(start) && start.before(stop) );
	}

	private void printReservationPage(PrintWriter writer) {
		printExistingReservations(writer);
		writer.println("<br>");
		printBookReservation(writer);
		writer.println("<br>");
		printDeleteReservation(writer);			
	}

	private void printExistingReservations(PrintWriter writer) {

		writer.println("<b>Existing Reservations</b><hr>");
		writer.println("<center>");	
		writer.println("<table class=\"res\" >");
		writer.println("<tr><th>reservation_pk</th><th>idTag</th><th>chargeBoxId</th><th>startDatetime</th><th>stopDatetime</th><th>active</th></tr>");

		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT reservation_pk, idTag, chargeBoxId, DATE_FORMAT(startDatetime, '%Y-%m-%d %H:%i'), "
					+ "DATE_FORMAT(stopDatetime, '%Y-%m-%d %H:%i'), active FROM reservation ORDER BY startDatetime");
			rs = pt.executeQuery();

			while( rs.next() ) {
				writer.print("<tr>");
				writer.print("<td>");
				writer.print(rs.getInt(1));
				writer.print("</td>");
				writer.print("<td>");
				writer.print(rs.getString(2));
				writer.print("</td>");
				writer.print("<td>");
				writer.print(rs.getString(3));
				writer.print("</td>");
				writer.print("<td>");
				writer.print(rs.getString(4));
				writer.print("</td>");
				writer.print("<td>");
				writer.print(rs.getString(5));
				writer.print("</td>");
				writer.print("<td>");
				writer.print(rs.getBoolean(6));
				writer.print("</td>");

				writer.println("</tr>");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);
		}			
		writer.println("</table>");
		writer.println("</center>");
	}

	private void printBookReservation(PrintWriter writer) {

		writer.println("<b>Book A New Reservation</b><hr>");
		writer.println("<center>");	
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/book\">");
		writer.println("<table>");		
		writer.println("<tr><td>");
		writer.println("idTag (of the user): ");
		writer.println("</td><td>");
		writer.println("<input type=\"text\" name=\"idTag\">");
		writer.println("</td></tr>");
		writer.println("<tr><td>");
		writer.println("chargeBoxId (of the charging point): ");
		writer.println("</td><td>");
		writer.println("<input type=\"text\" name=\"chargeBoxId\">");
		writer.println("</td></tr><tr><td>");
		writer.println("Start date and time (ex: 2011-12-21 11:30):");
		writer.println("</td><td>");
		writer.println("<input type=\"text\" name=\"startDatetime\">");
		writer.println("</td></tr><tr><td>");
		writer.println("Stop date and time (ex: 2011-12-21 11:30):");
		writer.println("</td><td>");
		writer.println("<input type=\"text\" name=\"stopDatetime\">");		
		writer.println("</td></tr>");
		writer.println("<tr><td></td>");
		writer.println("<td id=\"add_space\">");
		writer.println("<input type=\"submit\" value=\"Book\">");
		writer.println("</td></tr>");   	   	
		writer.println("</table>");		
		writer.println("</form>");
		writer.println("</center>");
	}

	private void printDeleteReservation(PrintWriter writer) {

		writer.println("<b>Delete An Existing Reservation</b><hr>");
		writer.println("<center>");	
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/delete\">");
		writer.println("<table>");		
		writer.println("<tr><td>");
		writer.println("reservation_pk: ");
		writer.println("</td><td>");
		writer.println("<input type=\"number\" min=\"1\" name=\"reservation_pk\">");
		writer.println("</td></tr>");
		writer.println("<tr><td></td>");
		writer.println("<td id=\"add_space\">");
		writer.println("<input type=\"submit\" value=\"Delete\">");
		writer.println("</td></tr>"); 
		writer.println("</table>");		
		writer.println("</form>");
		writer.println("</center>");
	}
}
