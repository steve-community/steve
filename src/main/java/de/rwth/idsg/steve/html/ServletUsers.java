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

import org.joda.time.DateTime;

import de.rwth.idsg.steve.common.ClientDBAccess;
import de.rwth.idsg.steve.common.Utils;

/**
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class ServletUsers extends HttpServlet {

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
				+ printUsers()
				+ printAddUser()
				+ printDeleteUser()
				+ Common.printFoot(contextPath));
		
		writer.close();	
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String command = request.getPathInfo();	
		
		if (command.equals("/add")){
			String idTag = request.getParameter("idTag");
			String parentIdTagSTR = request.getParameter("parentIdTag");
			String expiryDateSTR = request.getParameter("expiryDate");
			
			if (idTag == null || idTag.isEmpty()) {
				throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
			}
						
			Timestamp expiryTimestamp = null;
			if (expiryDateSTR != null && !expiryDateSTR.isEmpty()) {
				DateTime expiryDatetime = Utils.convertToDateTime(expiryDateSTR);
				expiryTimestamp = new Timestamp(expiryDatetime.getMillis());
			}
			
			String parentIdTag = null;
			if (parentIdTagSTR != null && !parentIdTagSTR.isEmpty()) {
				parentIdTag = parentIdTagSTR;
			}			
			ClientDBAccess.addUser(idTag, parentIdTag, expiryTimestamp);
			
		} else if (command.equals("/delete")){
			String idTag = request.getParameter("idTag");
			if (idTag == null || idTag.isEmpty()) {
				throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
			}			
			ClientDBAccess.deleteUser(idTag);			
		}		
		response.sendRedirect(contextPath + servletPath);
		return;
	}	

	private String printUsers() {
		StringBuilder builder = new StringBuilder(
				"<h3><span>Registered Users</span></h3>\n"
				+ "<center>\n"
				+ "<table class=\"res\">\n"
				+ "<tr><th>idTag</th><th>parentIdTag</th><th>expiryDate</th><th>inTransaction</th><th>blocked</th></tr>\n");

		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT * FROM user;");
			rs = pt.executeQuery();

			while ( rs.next() ) {
				
				Timestamp ts = rs.getTimestamp(3);
				String str = "null";
				if (ts != null) str = Utils.convertToString(ts);
							
				builder.append("<tr>"
						+ "<td>" + rs.getString(1) + "</td>"
						+ "<td>" + rs.getString(2) + "</td>"
						+ "<td>" + str + "</td>"
						+ "<td>" + rs.getBoolean(4) + "</td>"
						+ "<td>" + rs.getBoolean(5) + "</td>"
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

	private String printAddUser() {		
		StringBuilder builder = new StringBuilder(
				"<h3><span>Add A New User</span></h3>\n"
				+ "<center>\n"
				+ "<form method=\"POST\" action=\"" + contextPath + servletPath + "/add\">\n"
				+ "<table class=\"bc\">\n"		
				+ "<tr><td>idTag (string):</td><td><input type=\"text\" name=\"idTag\"></td></tr>\n"
				+ "<tr><td>parentIdTag (string):</td><td><input type=\"text\" name=\"parentIdTag\" placeholder=\"optional\"></td></tr>\n"
				+ "<tr><td>Expiry date and time (ex: 2011-12-21 11:30):</td><td><input type=\"text\" name=\"expiryDate\" placeholder=\"optional\"></td></tr>\n"
				+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Add\"></td></tr>\n" 	   	
				+ "</table>\n"		
				+ "</form>\n"
				+ "</center>\n<br>\n");		
		return builder.toString();
	}

	private String printDeleteUser() {
		StringBuilder builder = new StringBuilder(
				"<h3><span>Delete A User</span></h3>\n"
				+ "<center>\n"	
				+ "<form method=\"POST\" action=\""+ contextPath + servletPath + "/delete\">\n"
				+ "<table class=\"bc\">\n"
				+ "<tr><td>idTag (string):</td><td><input type=\"text\" name=\"idTag\"></td></tr>\n"
				+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Delete\"></td></tr>\n"
				+ "</table>\n"
				+ "</form>\n"
				+ "</center>\n");
		return builder.toString();
	}
}
