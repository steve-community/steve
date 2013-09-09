package html;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
		
		printHead(response, writer);
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

			bookReservation(idTag, chargeBoxId, startDatetime, stopDatetime);
			response.sendRedirect(contextPath + servletPath);

		} else if (command.equals("/delete")){
			int connector_pk = Integer.parseInt(request.getParameter("reservation_pk"));

			deleteReservation(connector_pk);
			response.sendRedirect(contextPath + servletPath);
		}
		
		writer.close();	
	}

	private void printHead(HttpServletResponse response, PrintWriter writer) throws IOException {		
		// Start printing regular HTML content
		response.setContentType("text/html");

		writer.println("<!DOCTYPE html>");
		writer.println("<html>");
		writer.println("<head>");
		writer.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + contextPath + "/style.css\">");
		writer.println("<script src=\"" + contextPath + "/script.js\" type=\"text/javascript\"></script>");
		writer.println("<title>SteVe - Steckdosenverwaltung</title>");
		writer.println("</head>");

		writer.println("<body>");
		writer.println("<div class=\"top-banner\">");
		writer.println("<div class=\"container\">");
		writer.println("<img src=\""+ contextPath + "/logo.png\" height=\"100\">");
		writer.println("</div>");
		writer.println("</div>");
		writer.println("<div class=\"top-menu\">");
		writer.println("<div class=\"container\">");
		writer.println("<ul class=\"nav-list\">");	
		writer.println("<li><a href=\"" + contextPath + "/manager\">HOME</a></li>");
		writer.println("<li><a href=\"" + contextPath + "/manager/reservation\">RESERVATION</a></li>");
		writer.println("<li><a href=\"" + contextPath + "/manager/operations\">OPERATIONS</a></li>");
		writer.println("<li><a href=\"" + contextPath + "/manager/log\">LOG</a></li>");
		writer.println("</ul>");	
		writer.println("</div>");
		writer.println("</div>");
		
		writer.println("<div id=\"wrapper\">");

	}

	private void bookReservation(String idTag, String chargeBoxId, String startDatetime, String stopDatetime) {

		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
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

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, null);
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

			pt.executeUpdate();
			connect.commit();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, null);
		}		
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
			pt = connect.prepareStatement("SELECT reservation_pk, idTag, chargeBoxId, DATE_FORMAT(startDatetime, '%Y-%m-%d %H:%i'), DATE_FORMAT(stopDatetime, '%Y-%m-%d %H:%i'), active FROM reservation");
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
