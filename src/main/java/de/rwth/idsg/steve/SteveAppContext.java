package de.rwth.idsg.steve;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.logging.Slf4jLogger;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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

    private WebAppContext getWebApp() throws IOException {
        WebAppContext ctx = new WebAppContext();
        ctx.setContextPath(SteveConfiguration.CONTEXT_PATH);
        ctx.setResourceBase(new ClassPathResource("webapp").getURI().toString());

        // Disable directory listings if no index.html is found.
        ctx.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        ServletHolder web = new ServletHolder("spring-dispatcher", new DispatcherServlet(springContext));
        ServletHolder cxf = new ServletHolder("cxf", getApacheCXF());

        ctx.addEventListener(new ContextLoaderListener(springContext));
        ctx.addServlet(web, SteveConfiguration.SPRING_MAPPING);
        ctx.addServlet(cxf, SteveConfiguration.CXF_MAPPING);

        // Register Spring's filter chain for security. The name is not arbitrary, but is as expected by Spring.
        ctx.addFilter(
                new FilterHolder(new DelegatingFilterProxy("springSecurityFilterChain")),
                SteveConfiguration.SPRING_MANAGER_MAPPING,
                EnumSet.allOf(DispatcherType.class)
        );

        return ctx;
    }

    private Servlet getApacheCXF() {
        LogUtils.setLoggerClass(Slf4jLogger.class);

        List<Feature> list = new ArrayList<>();
        list.add(new LoggingFeature()); // Log incoming/outgoing messages

        Bus bus = BusFactory.newInstance().createBus();
        bus.setFeatures(list);
        BusFactory.setDefaultBus(bus);

        CXFServlet cxfServlet = new CXFServlet();
        cxfServlet.setBus(bus);

        return cxfServlet;
    }

    private Handler getRedirectHandler() {
        RewriteHandler rewrite = new RewriteHandler();
        rewrite.setRewriteRequestURI(true);
        rewrite.setRewritePathInfo(true);

        String[] redirectArray = {"", "/steve", "/steve/manager"};
        for (String redirect : redirectArray) {
            RedirectPatternRule rule = new RedirectPatternRule();
            rule.setTerminating(true);
            rule.setPattern(redirect);
            rule.setLocation("/steve/manager/home");
            rewrite.addRule(rule);
        }
        return rewrite;
    }
}
