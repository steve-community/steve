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
package de.rwth.idsg.steve.web.dto.ocpp;

/**
 * Determines if a configuration key is read-only ("R") or read-write ("RW"). In case the key is read-only, the Central
 * System can read the value for the key using GetConfiguration, but not write it. In case the accessibility is
 * read-write, the Central System can also write the value for the key using ChangeConfiguration.
 *
 * This distinction was added in OCPP 1.6.
 */
public enum ConfigurationKeyReadWriteEnum {
    R,
    RW
}
