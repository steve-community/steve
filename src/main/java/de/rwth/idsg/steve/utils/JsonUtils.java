/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.jooq.JSON;
import tools.jackson.databind.node.ObjectNode;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.05.2026
 */
public class JsonUtils {

    public static String getPropertyValueAsString(JSON jsonFromDB, String key) {
        return getPropertyValueAsString(toObjectNode(jsonFromDB), key);
    }

    public static String getPropertyValueAsString(ObjectNode jsonNode, String key) {
        var node = jsonNode.get(key);
        return node == null ? null : node.asString();
    }

    public static ObjectNode toObjectNode(JSON jsonFromDB) {
        var mapper = JsonObjectMapper.INSTANCE.getMapper();

        if (jsonFromDB == null || StringUtils.isEmpty(jsonFromDB.data())) {
            return mapper.createObjectNode();
        }

        var node = mapper.readTree(jsonFromDB.data());
        if (!node.isObject()) {
            throw new SteveException("Existing OCPP configuration is not a JSON object"); // should not happen
        }
        return (ObjectNode) node;
    }
}
