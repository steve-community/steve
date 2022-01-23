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
package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.rwth.idsg.steve.ocpp.ws.custom.EnumMixin;
import de.rwth.idsg.steve.ocpp.ws.custom.EnumProcessor;
import de.rwth.idsg.steve.ocpp.ws.custom.MeterValue15Mixin;
import ocpp.cs._2012._06.MeterValuesRequest;

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
    public void setupModule(Module.SetupContext sc) {
        super.setupModule(sc);

        sc.setMixInAnnotations(MeterValuesRequest.class, MeterValue15Mixin.class);

        EnumProcessor.apply(
                Arrays.asList(
                        ocpp.cs._2012._06.ObjectFactory.class.getPackage().getName(),
                        ocpp.cp._2012._06.ObjectFactory.class.getPackage().getName()
                ),
                clazz -> sc.setMixInAnnotations(clazz, EnumMixin.class)
        );
    }
}
