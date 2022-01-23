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

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.rwth.idsg.steve.utils.StringUtils.splitByComma;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 02.01.2015
 */
@Setter
@Getter
public class GetConfigurationParams extends MultipleChargePointSelect {

    private List<String> confKeyList;

    private String commaSeparatedCustomConfKeys;

    public List<String> getAllKeys() {
        List<String> fromPredefined = Objects.requireNonNullElse(confKeyList, Collections.emptyList());
        List<String> fromCustom = splitByComma(commaSeparatedCustomConfKeys);

        return Stream.of(fromPredefined, fromCustom)
                     .flatMap(Collection::stream)
                     .collect(Collectors.toList());
    }
}
