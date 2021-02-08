/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2020 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.ocpp.soap;

import com.oneandone.compositejks.SslContextBuilder;
import de.rwth.idsg.steve.SteveConfiguration;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.soap.SOAPBinding;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.10.2015
 */
@Component
public class ClientProvider {

    @Nullable private TLSClientParameters tlsClientParams;

    @Autowired
    private SteveConfiguration config;

    @PostConstruct
    private void init() {
        if (shouldInitSSL()) {
            tlsClientParams = new TLSClientParameters();
            tlsClientParams.setSSLSocketFactory(setupSSL());
        } else {
            tlsClientParams = null;
        }
    }

    public <T> T createClient(Class<T> clazz, String endpointAddress) {
        JaxWsProxyFactoryBean bean = getBean(endpointAddress);
        bean.setServiceClass(clazz);
        T clientObject = clazz.cast(bean.create());

        if (tlsClientParams != null) {
            Client client = ClientProxy.getClient(clientObject);
            HTTPConduit http = (HTTPConduit) client.getConduit();
            http.setTlsClientParameters(tlsClientParams);
        }

        return clientObject;
    }

    private static JaxWsProxyFactoryBean getBean(String endpointAddress) {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.getFeatures().add(LoggingFeatureProxy.INSTANCE.get());
        f.getFeatures().add(new WSAddressingFeature());
        f.setAddress(endpointAddress);
        return f;
    }

    private boolean shouldInitSSL() {
        return !StringUtils.isEmpty(config.getKeystorePath()) && !StringUtils.isEmpty(config.getKeystorePassword());
    }

    private SSLSocketFactory setupSSL() {
        SSLContext ssl;
        try {
            String keyStorePath = config.getKeystorePath();
            String keyStorePwd = config.getKeystorePassword();
            ssl = SslContextBuilder.builder()
                                   .keyStoreFromFile(keyStorePath, keyStorePwd)
                                   .usingTLS()
                                   .usingDefaultAlgorithm()
                                   .usingKeyManagerPasswordFromKeyStore()
                                   .buildMergedWithSystem();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ssl.getSocketFactory();
    }
}
