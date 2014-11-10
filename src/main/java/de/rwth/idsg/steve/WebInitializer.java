package de.rwth.idsg.steve;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.logging.Slf4jLogger;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
public class WebInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(BeanConfiguration.class);
        ctx.setServletContext(servletContext);

        // -------------------------------------------------------------------------
        // Web Manager
        // -------------------------------------------------------------------------

        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
        servlet.addMapping(SteveConfiguration.SPRING_WEB_MAPPING);
        servlet.setLoadOnStartup(1);

        // -------------------------------------------------------------------------
        // Apache CXF
        // -------------------------------------------------------------------------

        LogUtils.setLoggerClass(Slf4jLogger.class);

        List<Feature> list = new ArrayList<>();
        list.add(new LoggingFeature()); // Log incoming/outgoing messages

        Bus bus = BusFactory.newInstance().createBus();
        bus.setFeatures(list);
        BusFactory.setDefaultBus(bus);

        CXFServlet s = new CXFServlet();
        s.setBus(bus);

        ServletRegistration.Dynamic cxf = servletContext.addServlet("cxf", s);
        cxf.addMapping(SteveConfiguration.CXF_MAPPING);
        cxf.setLoadOnStartup(1);
    }
}