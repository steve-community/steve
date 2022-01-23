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

import de.rwth.idsg.steve.ocpp.ws.AbstractTypeStore;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.03.2018
 */
public final class Ocpp16TypeStore extends AbstractTypeStore {

    public static final Ocpp16TypeStore INSTANCE = new Ocpp16TypeStore();

    private Ocpp16TypeStore() {
        super(
                ocpp.cs._2015._10.ObjectFactory.class.getPackage().getName(),
                ocpp.cp._2015._10.ObjectFactory.class.getPackage().getName()
        );
    }
}
