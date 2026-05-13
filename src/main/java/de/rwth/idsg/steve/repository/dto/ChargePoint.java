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
package de.rwth.idsg.steve.repository.dto;

import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum;
import jooq.steve.db.tables.records.AddressRecord;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import tools.jackson.databind.node.ObjectNode;

/**
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 *
 */
public final class ChargePoint {

    @Getter
    @Builder
    public static final class Overview {
        private final int chargeBoxPk;
        private final String chargeBoxId, description, ocppProtocol, lastHeartbeatTimestamp;
        private final DateTime lastHeartbeatTimestampDT;
    }

    @Getter
    @RequiredArgsConstructor
    public static final class Details {
        private final ChargeBoxRecord chargeBox;
        private final AddressRecord address;

        public String getCpoName() {
            return getConfigValueSafely(chargeBox, ConfigurationKeyEnum.CpoName.name());
        }

        private static String getConfigValueSafely(ChargeBoxRecord chargeBox, String key) {
            var value = chargeBox.getOcppConfiguration();
            if (value == null) {
                return null;
            }

            var mapper = JsonObjectMapper.INSTANCE.getMapper();
            var node = mapper.readTree(value.data());
            if (!node.isObject()) {
                return null;
            }

            var ocppConfiguration = (ObjectNode) node;
            var configNode = ocppConfiguration.get(key);
            return configNode == null ? null : configNode.asString();
        }
    }

}
