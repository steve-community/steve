package de.rwth.idsg.steve.html;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
@WebServlet("/manager/signout")
public class ServletSignout extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		/*
		 * First step : Invalidate user session
		 */
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		
		/*
		 * Second step : Invalidate all cookies by, for each cookie received,
		 * overwriting value and instructing browser to deletes it
		 */
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				cookie.setValue("-");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
		
		// For HTTP 1.1
	    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	    // For HTTP 1.0
	    response.setHeader("Pragma", "no-cache");
	    response.setDateHeader("Expires", 0); 
		response.sendRedirect(request.getContextPath());
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}

}
