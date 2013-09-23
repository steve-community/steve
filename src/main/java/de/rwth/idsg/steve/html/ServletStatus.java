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

import de.rwth.idsg.steve.common.Utils;

public class ServletStatus extends HttpServlet {

	private static final long serialVersionUID = -8913449378037814608L;
	String contextPath, servletPath;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details			
		contextPath = request.getContextPath();
		//servletPath = request.getServletPath();	

		PrintWriter writer = response.getWriter();		
		response.setContentType("text/html");
		
		writer.println(
				Common.printHead(contextPath)
				+ printStatus()
				+ Common.printFoot(contextPath));
		
		writer.close();	
	}

	private String printStatus() {
		StringBuilder builder = new StringBuilder(
				"<b>Current Status Of Charge Points</b><hr>\n"
				+ "<center>\n"
				+ "<table class=\"res\">\n"
				+ "<tr><th>chargeBoxId</th><th>connectorId</th><th>status</th></tr>\n");

		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT chargeBoxId, connectorId FROM connector");
			rs = pt.executeQuery();

			while( rs.next() ) {
				builder.append("<tr><td>" + rs.getString(1) + "</td><td>" + rs.getInt(2) + "</td>");				
				String mama = "none";
				builder.append("<td>" + mama + "</td></tr>\n");
			}		
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);
		}			
		builder.append("</table>\n</center>\n");
		return builder.toString();
	}
}
