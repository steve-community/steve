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
package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.config.SteveProperties;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.boot.autoconfigure.web.ServerProperties;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.xml.ws.soap.SOAPBinding;

/**
 * @author Andreas Heuvels <andreas.heuvels@rwth-aachen.de>
 * @since 06.04.18
 */
public class Helpers {

    public static String getRandomString() {
        return UUID.randomUUID().toString();
    }

    public static List<String> getRandomStrings(int size) {
        var list = new ArrayList<String>(size);
        for (int i = 0; i < size; i++) {
            list.add(getRandomString());
        }
        return list;
    }

    public static URI getSoapPath(ServerProperties serverProperties, SteveProperties steveProperties)
            throws URISyntaxException {
        var scheme = "http";
        if (serverProperties.getSsl().isEnabled()) {
            scheme = "https";
        }

        return new URI(
                scheme,
                null,
                serverProperties.getAddress().getHostName(),
                serverProperties.getPort(),
                serverProperties.getServlet().getContextPath()
                        + steveProperties.getPaths().getSoapMapping()
                        + steveProperties.getPaths().getRouterEndpointPath() + "/",
                null,
                null);
    }

    public static URI getWsPath(ServerProperties serverProperties, SteveProperties steveProperties)
            throws URISyntaxException {
        var scheme = "ws";
        if (serverProperties.getSsl().isEnabled()) {
            scheme = "wss";
        }

        return new URI(
                scheme,
                null,
                serverProperties.getAddress().getHostName(),
                serverProperties.getPort(),
                serverProperties.getServlet().getContextPath()
                        + steveProperties.getPaths().getWebsocketMapping()
                        + steveProperties.getPaths().getRouterEndpointPath() + "/",
                null,
                null);
    }

    public static ocpp.cs._2015._10.CentralSystemService getForOcpp16(URI path) {
        return createBean(path, ocpp.cs._2015._10.CentralSystemService.class);
    }

    public static ocpp.cs._2012._06.CentralSystemService getForOcpp15(URI path) {
        return createBean(path, ocpp.cs._2012._06.CentralSystemService.class);
    }

    public static ocpp.cs._2010._08.CentralSystemService getForOcpp12(URI path) {
        return createBean(path, ocpp.cs._2010._08.CentralSystemService.class);
    }

    private static <T> T createBean(URI endpointAddress, Class<T> serviceClass) {
        var f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.getFeatures().add(new WSAddressingFeature());
        f.setAddress(endpointAddress.toString());
        f.setServiceClass(serviceClass);
        return (T) f.create();
    }
}
