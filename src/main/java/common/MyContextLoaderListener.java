package common;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.context.ContextCleanupListener;
import org.springframework.web.context.ContextLoader;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

/**
 * Note: Slightly modified version to shutdown AbandonedConnectionCleanupThread
 * 
 * 
 * 
 * Bootstr ap listener to start up and shut down Spring's root {@link WebApplicationContext}.
 * Simply delegates to {@link ContextLoader} as well as to {@link ContextCleanupListener}.
 *
 * <p>This listener should be registered after
 * {@link org.springframework.web.util.Log4jConfigListener}
 * in <code>web.xml</code>, if the latter is used.
 *
 * @author Juergen Hoeller
 * @since 17.02.2003
 * @see org.springframework.web.util.Log4jConfigListener
 */
public class MyContextLoaderListener extends ContextLoader implements ServletContextListener {

	private static final Log logger = LogFactory.getLog(MyContextLoaderListener.class);

	private ContextLoader contextLoader;


	/**
	 * Initialize the root web application context.
	 */
	public void contextInitialized(ServletContextEvent event) {
		this.contextLoader = createContextLoader();
		if (this.contextLoader == null) {
			this.contextLoader = this;
		}
		this.contextLoader.initWebApplicationContext(event.getServletContext());
	}

	/**
	 * Create the ContextLoader to use. Can be overridden in subclasses.
	 * @return the new ContextLoader
	 * @deprecated in favor of simply subclassing ContextLoaderListener itself
	 * (which extends ContextLoader, as of Spring 3.0)
	 */
	@Deprecated
	protected ContextLoader createContextLoader() {
		return null;
	}

	/**
	 * Return the ContextLoader used by this listener.
	 * @return the current ContextLoader
	 * @deprecated in favor of simply subclassing ContextLoaderListener itself
	 * (which extends ContextLoader, as of Spring 3.0)
	 */
	@Deprecated
	public ContextLoader getContextLoader() {
		return this.contextLoader;
	}


	/**
	 * Close the root web application context.
	 */
	public void contextDestroyed(ServletContextEvent event) {		
		if (this.contextLoader != null) {
			this.contextLoader.closeWebApplicationContext(event.getServletContext());
		}
		cleanupAttributes(event.getServletContext());

		// http://docs.oracle.com/cd/E17952_01/connector-j-relnotes-en/news-5-1-23.html
		// From Tomcat log: The web application ... appears to have started a thread named [Abandoned connection cleanup thread] 
		// but has failed to stop it. This is very likely to create a memory leak.
		try {
			AbandonedConnectionCleanupThread.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Find all ServletContext attributes which implement {@link DisposableBean}
	 * and destroy them, removing all affected ServletContext attributes eventually.
	 * @param sc the ServletContext to check
	 */
	static void cleanupAttributes(ServletContext sc) {
		Enumeration<String> attrNames = sc.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String attrName = (String) attrNames.nextElement();
			if (attrName.startsWith("org.springframework.")) {
				Object attrValue = sc.getAttribute(attrName);
				if (attrValue instanceof DisposableBean) {
					try {
						((DisposableBean) attrValue).destroy();
					}
					catch (Throwable ex) {
						logger.error("Couldn't invoke destroy method of attribute with name '" + attrName + "'", ex);
					}
				}
			}
		}
	}
}

