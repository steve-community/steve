package de.rwth.idsg.steve.html;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
* This class filters and logs all exceptions. It prevents printing raw exception details to the browser.
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class ExceptionFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(ExceptionFilter.class);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			
			chain.doFilter(request, response);
			
		} catch (InputException ex) {
			LOG.error("Exception happened", ex);
			response.setContentType("text/plain");
			response.getWriter().write(ex.getHTMLMessage());
			
		} catch (Exception ex) {
			LOG.error("Exception happened", ex);
			response.setContentType("text/plain");
			response.getWriter().write("Something bad happened. Check the log for details.");
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}
}