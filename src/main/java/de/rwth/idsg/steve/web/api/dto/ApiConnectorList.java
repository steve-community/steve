/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
package de.rwth.idsg.steve.web.api.dto;

//import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.utils.ConnectorStatusCountFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author fnkbsi
 * @since 20.10.2023
 */

@Getter
@Setter
@RequiredArgsConstructor
public class ApiConnectorList {

    @Schema(description = "List of charge boxes")
    private List<String> chargeBoxList = new ArrayList<>();

    @Schema(description = "List of possible states")
    private final Set<String> statusFilterValues = ConnectorStatusCountFilter.ALL_STATUS_VALUES;

    @Schema(description = "List of connectors is filtered")
    private Boolean isFiltered = false;

    @Schema(description = "List of connectors")
    private List<ConnectorStatus> connectors = new ArrayList<>();
}
