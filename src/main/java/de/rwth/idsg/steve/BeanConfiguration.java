package de.rwth.idsg.steve;

import de.rwth.idsg.steve.service.CentralSystemService12_Server;
import de.rwth.idsg.steve.service.CentralSystemService15_Server;
import de.rwth.idsg.steve.web.GsonHttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPBinding;
import java.util.List;

/**
 * Configuration and beans of Spring Framework.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Slf4j
@Configuration
@EnableWebMvc
@ComponentScan({
        "de.rwth.idsg.steve.repository",
        "de.rwth.idsg.steve.service",
        "de.rwth.idsg.steve.web"})
public class BeanConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    @Qualifier("jooqConfig")
    public org.jooq.Configuration jooqConfig() throws NamingException {

        // Get the datasource
        InitialContext ic = new InitialContext();
        DataSource dataSource = (DataSource) ic.lookup(SteveConfiguration.DB.DS_JNDI_NAME);

        // Configuration for JOOQ
        return new DefaultConfiguration()
                .set(SteveConfiguration.DB.JOOQ_DIALECT)
                .set(new DataSourceConnectionProvider(dataSource))
                .set(new Settings().withExecuteLogging(SteveConfiguration.DB.JOOQ_EXEC_LOGGING));
    }

    /**
     * Resolver for JSP views/templates. Controller classes process the requests
     * and forward to JSP files for rendering.
     *
     */
    @Bean
    public UrlBasedViewResolver urlBasedViewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }

    /**
     * Resource path for static content of the Web interface.
     *
     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    /**
     * GsonHttpMessageConverter is needed to return plain JSON objects to AJAX calls.
     *
     */
    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.add(new GsonHttpMessageConverter());
    }

    /**
     * Just a dummy service to route incoming messages to the appropriate service version.
     *
     */
    @Bean
    public JaxWsServerFactoryBean routerService() {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setServiceBean(centralSystemService12());
        f.setAddress("/CentralSystemService");
        f.getInInterceptors().add(new MediatorInInterceptor());
        f.create();
        return f;
    }

    // -------------------------------------------------------------------------
    // OCPP 1.2 Beans
    // -------------------------------------------------------------------------

    /**
     * For OCPP 1.2 SOAP clients
     *
     */
    @Bean
    @Qualifier("ocpp12")
    public JaxWsProxyFactoryBean ocpp12ClientFactory() {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.setServiceClass(ocpp.cp._2010._08.ChargePointService.class);
        return f;
    }

    @Bean
    public ocpp.cs._2010._08.CentralSystemService centralSystemService12() {
        return new CentralSystemService12_Server();
    }

    @Bean
    public JaxWsServerFactoryBean centralSystemServiceOCPP12() {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setServiceBean(centralSystemService12());
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.setEndpointName(new QName("urn://Ocpp/Cs/2010/08/", "CentralSystemServiceSoap12"));
        f.setServiceName(new QName("urn://Ocpp/Cs/2010/08/", "CentralSystemService"));
        f.setAddress("/CentralSystemServiceOCPP12");
        f.getFeatures().add(new WSAddressingFeature());
        f.create();
        return f;
    }

    // -------------------------------------------------------------------------
    // OCPP 1.5 Beans
    // -------------------------------------------------------------------------

    /**
     * For OCPP 1.5 SOAP clients
     *
     */
    @Bean
    @Qualifier("ocpp15")
    public JaxWsProxyFactoryBean ocpp15ClientFactory() {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.setServiceClass(ocpp.cp._2012._06.ChargePointService.class);
        return f;
    }

    @Bean
    public ocpp.cs._2012._06.CentralSystemService centralSystemService15() {
        return new CentralSystemService15_Server();
    }

    @Bean
    public JaxWsServerFactoryBean centralSystemServiceOCPP15() {
        JaxWsServerFactoryBean f = new JaxWsServerFactoryBean();
        f.setServiceBean(centralSystemService15());
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.setEndpointName(new QName("urn://Ocpp/Cs/2012/06/", "CentralSystemServiceSoap12"));
        f.setServiceName(new QName("urn://Ocpp/Cs/2012/06/", "CentralSystemService"));
        f.setAddress("/CentralSystemServiceOCPP15");
        f.getFeatures().add(new WSAddressingFeature());
        f.create();
        return f;
    }
}