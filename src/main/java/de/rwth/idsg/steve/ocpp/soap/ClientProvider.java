/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.boot.web.server.Ssl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.xml.ws.soap.SOAPBinding;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.10.2015
 */
@Component
public class ClientProvider {

    @Nullable private final TLSClientParameters tlsClientParams;

    public ClientProvider(ServerProperties serverProperties) {
        Ssl ssl = serverProperties.getSsl();
        try {
            tlsClientParams = create(ssl);
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    private static TLSClientParameters create(Ssl ssl) throws Exception {
        if (ssl == null || !ssl.isEnabled()) {
            return null;
        }

        String keyStorePath = ssl.getKeyStore();
        String keyStorePwd = ssl.getKeyStorePassword();

        boolean shouldInit = StringUtils.hasLength(keyStorePath) && StringUtils.hasLength(keyStorePwd);
        if (!shouldInit) {
            return null;
        }

        var socketFactory = SslContextBuilder.builder()
            .keyStoreFromFile(keyStorePath, keyStorePwd)
            .usingTLS()
            .usingDefaultAlgorithm()
            .usingKeyManagerPasswordFromKeyStore()
            .buildMergedWithSystem()
            .getSocketFactory();

        var tlsClientParams = new TLSClientParameters();
        tlsClientParams.setSSLSocketFactory(socketFactory);
        return tlsClientParams;
    }
}
