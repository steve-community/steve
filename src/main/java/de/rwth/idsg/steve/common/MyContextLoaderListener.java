package de.rwth.idsg.steve.common;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

/**
 * Starting point for SteVe
 * 
 */
public class MyContextLoaderListener extends ContextLoaderListener {
	
	/**
	 * Initialize the root web application context.
	 */
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		new PropertiesFileManager().readPropFile();
	}

	/**
	 * Close the root web application context.
	 */
	public void contextDestroyed(ServletContextEvent event) {
		new PropertiesFileManager().writePropFile();
		super.contextDestroyed(event);
		
		// http://docs.oracle.com/cd/E17952_01/connector-j-relnotes-en/news-5-1-23.html
		// From Tomcat log: The web application ... appears to have started a thread named [Abandoned connection cleanup thread] 
		// but has failed to stop it. This is very likely to create a memory leak.
		try {
			AbandonedConnectionCleanupThread.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}