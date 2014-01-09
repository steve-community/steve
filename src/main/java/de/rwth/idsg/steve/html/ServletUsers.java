package de.rwth.idsg.steve.html;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.rwth.idsg.steve.common.ClientDBAccess;
import de.rwth.idsg.steve.common.utils.DateTimeUtils;
import de.rwth.idsg.steve.common.utils.InputUtils;

/**
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class ServletUsers extends HttpServlet {

	private static final long serialVersionUID = 1L;
	String contextPath, servletPath;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details			
		contextPath = request.getContextPath();
		servletPath = contextPath + request.getServletPath();	

		request.setAttribute("contextPath", contextPath );
		request.setAttribute("servletPath", servletPath );
		request.setAttribute("userList", ClientDBAccess.getUsers() );
		
		request.getRequestDispatcher("/WEB-INF/jsp/data-man/users.jsp").forward(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String command = request.getPathInfo();	
		
		if (command.equals("/add")){
			processAddUser(request);
			
		} else if (command.equals("/delete")){
			processDeleteUser(request);
			
		} else if (command.equals("/update")) {
			processUpdateUser(request);
		}			
		response.sendRedirect(servletPath);
		return;
	}
	
	private void processAddUser(HttpServletRequest request) {		
		String idTag = request.getParameter("idTag");
		String parentIdTagSTR = request.getParameter("parentIdTag");
		String expiryDateSTR = request.getParameter("expiryDate");
		InputUtils.checkNullOrEmpty(idTag);
					
		Timestamp expiryTimestamp = null;
		if ( !InputUtils.isNullOrEmpty(expiryDateSTR) ) {
			expiryTimestamp = DateTimeUtils.convertToTimestamp(expiryDateSTR);
		}
		
		String parentIdTag = null;
		if ( !InputUtils.isNullOrEmpty(parentIdTagSTR) ) {
			parentIdTag = parentIdTagSTR;
		}			
		ClientDBAccess.addUser(idTag, parentIdTag, expiryTimestamp);		
	}	

	private void processUpdateUser(HttpServletRequest request) {
		String idTag = request.getParameter("idTag");
		String parentIdTagSTR = request.getParameter("parentIdTag");
		String expiryDateSTR = request.getParameter("expiryDate");
		String blockUserSTR = request.getParameter("blockUser");
		
		Timestamp expiryTimestamp = null;
		if ( !InputUtils.isNullOrEmpty(expiryDateSTR) ) {
			expiryTimestamp = DateTimeUtils.convertToTimestamp(expiryDateSTR);
		}
		
		String parentIdTag = null;
		if ( !InputUtils.isNullOrEmpty(parentIdTagSTR) ) {
			parentIdTag = parentIdTagSTR;
		}
		
		boolean blockUser = false;
		if ( blockUserSTR.equals("true") ) {
			blockUser = true;
		}		
		ClientDBAccess.updateUser(idTag, parentIdTag, expiryTimestamp, blockUser);		
	}	

	private void processDeleteUser(HttpServletRequest request) {
		String idTag = request.getParameter("idTag");
		InputUtils.checkNullOrEmpty(idTag);
		
		ClientDBAccess.deleteUser(idTag);		
	}
}