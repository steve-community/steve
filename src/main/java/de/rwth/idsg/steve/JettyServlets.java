package de.rwth.idsg.steve;

import de.rwth.idsg.steve.config.BeanConfiguration;
import de.rwth.idsg.steve.config.OcppConfiguration;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.logging.Slf4jLogger;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.12.2014
 */
public class JettyServlets {

    private static WebApplicationContext getSpringContext() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(BeanConfiguration.class);
        ctx.register(OcppConfiguration.class);
        return ctx;
    }

    public static Servlet getWebManager() {
        return new DispatcherServlet(getSpringContext());
    }

    public static Servlet getApacheCXF() {
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
}