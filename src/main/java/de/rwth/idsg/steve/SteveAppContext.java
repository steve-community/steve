package de.rwth.idsg.steve;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 07.04.2015
 */
public class SteveAppContext {

    private AnnotationConfigWebApplicationContext springContext;

    public SteveAppContext() {
        springContext = new AnnotationConfigWebApplicationContext();
        springContext.scan("de.rwth.idsg.steve.config");
    }

    public HandlerCollection getHandlers() throws IOException {
        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(
                new Handler[]{
                        getRedirectHandler(),
                        getWebApp()
                });
        return handlerList;
    }

    private Handler getWebApp() throws IOException {
        if (CONFIG.getJetty().isGzipEnabled()) {
            return enableGzip(initWebApp());
        } else {
            return initWebApp();
        }
    }

    /**
     * Wraps the whole web app in a gzip handler to make Jetty return compressed content
     *
     * http://www.eclipse.org/jetty/documentation/current/gzip-filter.html
     */
    private Handler enableGzip(WebAppContext ctx) {
        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(ctx);
        return gzipHandler;
    }

    private WebAppContext initWebApp() throws IOException {
        WebAppContext ctx = new WebAppContext();
        ctx.setContextPath(CONFIG.getContextPath());
        ctx.setResourceBase(new ClassPathResource("webapp").getURI().toString());

        // Disable directory listings if no index.html is found.
        ctx.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        ServletHolder web = new ServletHolder("spring-dispatcher", new DispatcherServlet(springContext));
        ServletHolder cxf = new ServletHolder("cxf", new CXFServlet());

        ctx.addEventListener(new ContextLoaderListener(springContext));
        ctx.addServlet(web, CONFIG.getSpringMapping());
        ctx.addServlet(cxf, CONFIG.getCxfMapping());

        if (CONFIG.getProfile().isProd()) {
            addSecurityFilter(ctx);
        }

        initJSP(ctx);
        return ctx;
    }

    private void addSecurityFilter(WebAppContext ctx) {
        // The bean name is not arbitrary, but is as expected by Spring
        Filter f = new DelegatingFilterProxy(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME);

        ctx.addFilter(
                new FilterHolder(f),
                CONFIG.getSpringManagerMapping(),
                EnumSet.allOf(DispatcherType.class)
        );
    }

    private Handler getRedirectHandler() {
        RewriteHandler rewrite = new RewriteHandler();
        rewrite.setRewriteRequestURI(true);
        rewrite.setRewritePathInfo(true);

        String root = CONFIG.getContextPath();

        String[] redirectArray = {
                "",
//                root + "",
//                root + "/",
        };

        for (String redirect : redirectArray) {
            RedirectPatternRule rule = new RedirectPatternRule();
            rule.setTerminating(true);
            rule.setPattern(redirect);
            rule.setLocation(root + "/manager/home");
            rewrite.addRule(rule);
        }
        return rewrite;
    }

    // -------------------------------------------------------------------------
    // JSP stuff
    //
    // Help by:
    //
    // https://github.com/jetty-project/embedded-jetty-jsp
    // https://github.com/jasonish/jetty-springmvc-jsp-template
    // http://examples.javacodegeeks.com/enterprise-java/jetty/jetty-jsp-example
    // -------------------------------------------------------------------------

    private void initJSP(WebAppContext ctx) throws IOException {
        ctx.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
        ctx.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
        ctx.addBean(new ServletContainerInitializersStarter(ctx), true);
    }

    /**
     * Ensure the JSP engine is initialized correctly
     */
    private List<ContainerInitializer> jspInitializers() {
        List<ContainerInitializer> initializers = new ArrayList<>();
        initializers.add(new ContainerInitializer(new JettyJasperInitializer(), null));
        return initializers;
    }
}
