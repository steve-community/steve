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
package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.ocpp.model.ConnectorFormat;
import de.rwth.idsg.steve.ocpp.model.ConnectorType;
import de.rwth.idsg.steve.ocpp.model.PowerType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jooq.steve.db.enums.EvseTopologySource;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.05.2026
 */
@Getter
@Setter
public class ChargePointDeviceModelForm {

    @Valid
    private List<EvseForm> evses = new ArrayList<>();

    @Getter
    @Setter
    public static class EvseForm {

        // Internal database id
        @NotNull(message = "evsePk is required")
        private Integer evsePk;

        @NotNull(message = "evseId is required")
        private Integer evseId;

        @Schema(accessMode = AccessMode.READ_ONLY)
        private EvseTopologySource topologySource;

        // the only editable field
        private String evseIdExternal;

        @Valid
        private List<EvseConnectorForm> connectors = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class EvseConnectorForm {

        // Internal database id
        @NotNull(message = "evseConnectorPk is required")
        private Integer evseConnectorPk;

        @NotNull(message = "connectorId is required")
        private Integer connectorId;

        // editable fields

        private ConnectorType connectorType;
        private ConnectorFormat connectorFormat;
        private PowerType powerType;

        @Positive
        private Integer maxVoltage;

        @Positive
        private Integer maxAmperage;

        @Positive
        private Integer maxElectricPower;
    }
}
