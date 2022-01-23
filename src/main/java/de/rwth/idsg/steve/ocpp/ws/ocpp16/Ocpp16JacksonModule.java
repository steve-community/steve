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
package de.rwth.idsg.steve.ocpp.ws.ocpp16;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.rwth.idsg.steve.ocpp.ws.custom.EnumMixin;
import de.rwth.idsg.steve.ocpp.ws.custom.EnumProcessor;

import java.util.Arrays;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.04.2015
 */
public class Ocpp16JacksonModule extends SimpleModule {

    public Ocpp16JacksonModule() {
        super("Ocpp16JacksonModule", new Version(0, 0, 1, null, "de.rwth.idsg", "steve"));
    }

    @Override
    public void setupModule(SetupContext sc) {
        super.setupModule(sc);

        EnumProcessor.apply(
                Arrays.asList(
                        ocpp.cs._2015._10.ObjectFactory.class.getPackage().getName(),
                        ocpp.cp._2015._10.ObjectFactory.class.getPackage().getName()
                ),
                clazz -> sc.setMixInAnnotations(clazz, EnumMixin.class)
        );
    }
}
