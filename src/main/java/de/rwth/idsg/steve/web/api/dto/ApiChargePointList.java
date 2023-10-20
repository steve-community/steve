/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2023 SteVe Community Team
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
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author fnkbsi
 * @since 18.10.2023
 */

@Getter
@Setter
@RequiredArgsConstructor
public class ApiChargePointList {
    @ApiModelProperty(value = "List of charge points")
    List<ChargePointInfo> chargePointList =  new ArrayList<>();;

    public void addCP(String chargeBoxId, List<Integer> connectorIds){
        ChargePointInfo cp = new ChargePointInfo(chargeBoxId, connectorIds);
        this.chargePointList.add(cp);
    }

    @Getter
    @Setter
    class ChargePointInfo {
        @ApiModelProperty(value = "Charge Box ID")
        private String chargeBoxId;
        @ApiModelProperty(value = "List of the charge box connectors")
        private List<Integer> connectorIds;

        public ChargePointInfo (String chargeBoxId, List<Integer> connectorIds) {
            this.chargeBoxId = chargeBoxId;
            this.connectorIds = connectorIds;
        }
    }

}
