package de.rwth.idsg.steve.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Controller
public class SignOutController {

    // TODO not working
    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    public String signOut(HttpServletRequest request, HttpServletResponse response) {

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

        return "redirect:/manager/home";
    }
}
