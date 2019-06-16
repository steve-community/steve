/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2019 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
package de.rwth.idsg.steve.utils.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 28.08.2018
 */
public interface KeyStoreStep {

    ProtocolStep keyStoreFromStream(InputStream stream, String password) throws IOException, GeneralSecurityException;

    default ProtocolStep keyStoreFromStream(InputStream stream) throws IOException, GeneralSecurityException {
        return keyStoreFromStream(stream, null);
    }

    default ProtocolStep keyStoreFromFile(String path, String password) throws IOException, GeneralSecurityException {
        try (FileInputStream stream = new FileInputStream(path)) {
            return keyStoreFromStream(stream, password);
        }
    }

    default ProtocolStep keyStoreFromFile(String path) throws IOException, GeneralSecurityException {
        return keyStoreFromFile(path, null);
    }
}
