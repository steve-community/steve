/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2020 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import de.rwth.idsg.steve.SteveConfiguration;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import javax.xml.ws.soap.SOAPBinding;
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

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTP_HOST = "localhost";
    private static final int HTTP_PORT = 8080;
    private static final String HTTP_CONTEXT_PATH="";

    public static List<String> getRandomStrings(int size) {
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(getRandomString());
        }
        return list;
    }

    public static String getPath() {

       /* if (CONFIG.getJetty().isHttpEnabled()) {
            prefix = "http://";
            port = CONFIG.getJetty().getHttpPort();
        } else if (CONFIG.getJetty().isHttpsEnabled()) {
            prefix = "https://";
            port = CONFIG.getJetty().getHttpsPort();
        } else {
            throw new RuntimeException();
        }*/

        return HTTP_PREFIX + HTTP_HOST + ":" + HTTP_PORT
                + HTTP_CONTEXT_PATH + "/services" + SteveConfiguration.getRouterEndpointPath();
    }

    public static String getJsonPath() {
        /*String prefix;
        int port;

        if (CONFIG.getJetty().isHttpEnabled()) {
            prefix = "ws://";
            port = CONFIG.getJetty().getHttpPort();
        } else if (CONFIG.getJetty().isHttpsEnabled()) {
            prefix = "wss://";
            port = CONFIG.getJetty().getHttpsPort();
        } else {
            throw new RuntimeException();
        }*/

        return HTTP_PREFIX + HTTP_HOST + ":" + HTTP_PORT
                + HTTP_CONTEXT_PATH + "/websocket/CentralSystemService/";
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
