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

import de.rwth.idsg.steve.ocpp.ws.AbstractTypeStore;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.03.2015
 */
public final class Ocpp15TypeStore extends AbstractTypeStore {

    public static final Ocpp15TypeStore INSTANCE = new Ocpp15TypeStore();

    private Ocpp15TypeStore() {
        super(
                ocpp.cs._2012._06.ObjectFactory.class.getPackage().getName(),
                ocpp.cp._2012._06.ObjectFactory.class.getPackage().getName()
        );
    }

}
