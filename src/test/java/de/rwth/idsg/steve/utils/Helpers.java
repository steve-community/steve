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

import jakarta.xml.ws.soap.SOAPBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Andreas Heuvels <andreas.heuvels@rwth-aachen.de>
 * @since 06.04.18
 */
public class Helpers {

    public static String getRandomString() {
        return UUID.randomUUID().toString();
    }

    public static List<String> getRandomStrings(int size) {
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(getRandomString());
        }
        return list;
    }

    /**
     * only <code>http:/</code> and <code>https:/</code> because serverProperties.getAddress() starts with a slash.
     */
    public static String getPath(ServerProperties serverProperties) {
        String prefix = "http:/";

        if (serverProperties.getSsl().isEnabled()) {
            prefix = "https:/";
        }

        return prefix
               + serverProperties.getAddress()
               + ":"
               + serverProperties.getPort()
               + serverProperties.getServlet().getContextPath()
               + "/services"
               + SteveProperties.ROUTER_ENDPOINT_PATH;
    }

    /**
     * only <code>ws:/</code> and <code>wss:/</code> because serverProperties.getAddress() starts with a slash.
     */
    public static String getJsonPath(ServerProperties serverProperties) {
        String prefix = "ws:/";

        if (serverProperties.getSsl().isEnabled()) {
            prefix = "wss:/";
        }

        return prefix
               + serverProperties.getAddress()
               + ":"
               + serverProperties.getPort()
               + serverProperties.getServlet().getContextPath()
               + "/websocket/CentralSystemService/";
    }

    public static ocpp.cs._2015._10.CentralSystemService getForOcpp16(String path) {
        JaxWsProxyFactoryBean f = getBean(path);
        f.setServiceClass(ocpp.cs._2015._10.CentralSystemService.class);
        return (ocpp.cs._2015._10.CentralSystemService) f.create();
    }

    public static ocpp.cs._2012._06.CentralSystemService getForOcpp15(String path) {
        JaxWsProxyFactoryBean f = getBean(path);
        f.setServiceClass(ocpp.cs._2012._06.CentralSystemService.class);
        return (ocpp.cs._2012._06.CentralSystemService) f.create();
    }

    public static ocpp.cs._2010._08.CentralSystemService getForOcpp12(String path) {
        JaxWsProxyFactoryBean f = getBean(path);
        f.setServiceClass(ocpp.cs._2010._08.CentralSystemService.class);
        return (ocpp.cs._2010._08.CentralSystemService) f.create();
    }

    private static JaxWsProxyFactoryBean getBean(String endpointAddress) {
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        f.getFeatures().add(new WSAddressingFeature());
        f.setAddress(endpointAddress);
        return f;
    }

}
