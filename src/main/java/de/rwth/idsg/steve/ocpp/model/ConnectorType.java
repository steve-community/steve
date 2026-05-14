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
package de.rwth.idsg.steve.ocpp.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.05.2026
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ConnectorType {

    CHADEMO("CHAdeMO"),
    CHAOJI("ChaoJi"),
    DOMESTIC_A("Domestic A"),
    DOMESTIC_B("Domestic B"),
    DOMESTIC_C("Domestic C"),
    DOMESTIC_D("Domestic D"),
    DOMESTIC_E("Domestic E"),
    DOMESTIC_F("Domestic F"),
    DOMESTIC_G("Domestic G"),
    DOMESTIC_H("Domestic H"),
    DOMESTIC_I("Domestic I"),
    DOMESTIC_J("Domestic J"),
    DOMESTIC_K("Domestic K"),
    DOMESTIC_L("Domestic L"),
    DOMESTIC_M("Domestic M"),
    DOMESTIC_N("Domestic N"),
    DOMESTIC_O("Domestic O"),
    GBT_AC("Guobiao AC"),
    GBT_DC("Guobiao DC"),
    IEC_60309_2_single_16("IEC 60309-2 1-Single 16A"),
    IEC_60309_2_three_16("IEC 60309-2 3-Phase 16A"),
    IEC_60309_2_three_32("IEC 60309-2 3-Phase 32A"),
    IEC_60309_2_three_64("IEC 60309-2 3-Phase 64A"),
    IEC_62196_T1("IEC 62196 Type 1"),
    IEC_62196_T1_COMBO("IEC 62196 Type 1 Combo DC"),
    IEC_62196_T2("IEC 62196 Type 2"),
    IEC_62196_T2_COMBO("IEC 62196 Type 2 Combo DC"),
    IEC_62196_T3A("IEC 62196 Type 3A"),
    IEC_62196_T3C("IEC 62196 Type 3C"),
    NEMA_5_20("NEMA 5-20"),
    NEMA_6_30("NEMA 6-30"),
    NEMA_6_50("NEMA 6-50"),
    NEMA_10_30("NEMA 10-30"),
    NEMA_10_50("NEMA 10-50"),
    NEMA_14_30("NEMA 14-30"),
    NEMA_14_50("NEMA 14-50"),
    PANTOGRAPH_BOTTOM_UP("Pantograph Bottom-Up"),
    PANTOGRAPH_TOP_DOWN("Pantograph Top-Down"),
    TESLA_R("Tesla Roadster"),
    TESLA_S("Tesla Model S");

    private final String text;

    public static ConnectorType fromNullable(String enumName) {
        return StringUtils.isEmpty(enumName) ? null : ConnectorType.valueOf(enumName);
    }
}
