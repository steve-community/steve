package de.rwth.idsg.steve.html;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class ServletError extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		
		// Get the exception
		InputException exc = (InputException) request.getAttribute("javax.servlet.error.exception");

		PrintWriter writer = response.getWriter();		
		response.setContentType("text/plain");
		
		writer.println(exc.getHTMLMessage());
		writer.close();	
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
