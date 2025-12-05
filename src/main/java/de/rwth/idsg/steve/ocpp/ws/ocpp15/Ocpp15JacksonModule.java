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
package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import de.rwth.idsg.steve.ocpp.ws.custom.EnumMixin;
import de.rwth.idsg.steve.ocpp.ws.custom.EnumProcessor;
import de.rwth.idsg.steve.ocpp.ws.custom.MeterValue15Mixin;
import ocpp.cs._2012._06.MeterValuesRequest;
import tools.jackson.core.Version;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleModule;

import java.util.Arrays;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.04.2015
 */
public class Ocpp15JacksonModule extends SimpleModule {

    public Ocpp15JacksonModule() {
        super("Ocpp15JacksonModule", new Version(0, 0, 1, null, "de.rwth.idsg", "steve"));
    }

    @Override
    public void setupModule(JacksonModule.SetupContext sc) {
        super.setupModule(sc);

        sc.setMixIn(MeterValuesRequest.class, MeterValue15Mixin.class);

        EnumProcessor.apply(
                Arrays.asList(
                        ocpp.cs._2012._06.ObjectFactory.class.getPackage().getName(),
                        ocpp.cp._2012._06.ObjectFactory.class.getPackage().getName()
                ),
                clazz -> sc.setMixIn(clazz, EnumMixin.class)
        );
    }
}
