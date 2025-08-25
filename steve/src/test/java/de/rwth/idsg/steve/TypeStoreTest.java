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
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16TypeStore;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.03.2018
 */
public class TypeStoreTest {

    @Test
    public void ocpp12Test() {
        var typeStore = new Ocpp12TypeStore();

        var actionResponse = typeStore.findActionResponse(new ocpp.cp._2010._08.ResetRequest());
        assertThat(actionResponse).isNotNull();
        assertThat(actionResponse.getAction()).isEqualTo("Reset");
        assertThat(actionResponse.getResponseClass()).isEqualTo(ocpp.cp._2010._08.ResetResponse.class);

        var requestClass = typeStore.findRequestClass("BootNotification");
        assertThat(requestClass).isSameAs(ocpp.cs._2010._08.BootNotificationRequest.class);
    }

    @Test
    public void ocpp15Test() {
        var typeStore = new Ocpp15TypeStore();

        var actionResponse = typeStore.findActionResponse(new ocpp.cp._2012._06.UpdateFirmwareRequest());
        assertThat(actionResponse).isNotNull();
        assertThat(actionResponse.getAction()).isEqualTo("UpdateFirmware");
        assertThat(actionResponse.getResponseClass()).isEqualTo(ocpp.cp._2012._06.UpdateFirmwareResponse.class);

        var requestClass = typeStore.findRequestClass("BootNotification");
        assertThat(requestClass).isSameAs(ocpp.cs._2012._06.BootNotificationRequest.class);
    }

    @Test
    public void ocpp16Test() {
        var typeStore = new Ocpp16TypeStore();

        var actionResponse = typeStore.findActionResponse(new ocpp.cp._2015._10.UpdateFirmwareRequest());
        assertThat(actionResponse).isNotNull();
        assertThat(actionResponse.getAction()).isEqualTo("UpdateFirmware");
        assertThat(actionResponse.getResponseClass()).isEqualTo(ocpp.cp._2015._10.UpdateFirmwareResponse.class);

        var requestClass = typeStore.findRequestClass("BootNotification");
        assertThat(requestClass).isSameAs(ocpp.cs._2015._10.BootNotificationRequest.class);
    }
}
