/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
package de.rwth.idsg.steve;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16TypeStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.03.2018
 */
public class TypeStoreTest {

    @Test
    public void ocpp12Test() {
        Ocpp12TypeStore typeStore = Ocpp12TypeStore.INSTANCE;

        ActionResponsePair actionResponse = typeStore.findActionResponse(new ocpp.cp._2010._08.ResetRequest());
        Assertions.assertNotNull(actionResponse);
        Assertions.assertEquals("Reset", actionResponse.getAction());
        Assertions.assertEquals(ocpp.cp._2010._08.ResetResponse.class, actionResponse.getResponseClass());

        Class<? extends RequestType> requestClass = typeStore.findRequestClass("BootNotification");
        Assertions.assertSame(ocpp.cs._2010._08.BootNotificationRequest.class, requestClass);
    }

    @Test
    public void ocpp15Test() {
        Ocpp15TypeStore typeStore = Ocpp15TypeStore.INSTANCE;

        ActionResponsePair actionResponse = typeStore.findActionResponse(new ocpp.cp._2012._06.UpdateFirmwareRequest());
        Assertions.assertNotNull(actionResponse);
        Assertions.assertEquals("UpdateFirmware", actionResponse.getAction());
        Assertions.assertEquals(ocpp.cp._2012._06.UpdateFirmwareResponse.class, actionResponse.getResponseClass());

        Class<? extends RequestType> requestClass = typeStore.findRequestClass("BootNotification");
        Assertions.assertSame(ocpp.cs._2012._06.BootNotificationRequest.class, requestClass);
    }

    @Test
    public void ocpp16Test() {
        Ocpp16TypeStore typeStore = Ocpp16TypeStore.INSTANCE;

        ActionResponsePair actionResponse = typeStore.findActionResponse(new ocpp.cp._2015._10.UpdateFirmwareRequest());
        Assertions.assertNotNull(actionResponse);
        Assertions.assertEquals("UpdateFirmware", actionResponse.getAction());
        Assertions.assertEquals(ocpp.cp._2015._10.UpdateFirmwareResponse.class, actionResponse.getResponseClass());

        Class<? extends RequestType> requestClass = typeStore.findRequestClass("BootNotification");
        Assertions.assertSame(ocpp.cs._2015._10.BootNotificationRequest.class, requestClass);
    }
}
