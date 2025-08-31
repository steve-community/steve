/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve.jooq.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.repository.dto.WebUser;
import de.rwth.idsg.steve.service.dto.WebUserOverview;
import de.rwth.idsg.steve.web.dto.WebUserAuthority;
import jooq.steve.db.tables.records.WebUserRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.JSON;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebUserMapper {

    public static WebUser fromRecord(WebUserRecord r, ObjectMapper objectMapper) {
        return WebUser.builder()
                .webUserPk(r.getWebUserPk())
                .login(r.getUsername())
                .password(r.getPassword())
                .apiPassword(r.getApiPassword())
                .enabled(r.getEnabled())
                .authorities(fromJSON(r.getAuthorities(), objectMapper))
                .build();
    }

    public static WebUserOverview overviewFromRecord(WebUserRecord r, ObjectMapper objectMapper) {
        return WebUserOverview.builder()
                .webUserPk(r.getWebUserPk())
                .webUsername(r.getUsername())
                .enabled(r.getEnabled())
                .authorities(WebUserAuthority.fromJsonValue(
                        objectMapper,
                        r.getAuthorities() == null ? null : r.getAuthorities().data()))
                .build();
    }

    private static Set<WebUserAuthority> fromJSON(JSON json, ObjectMapper objectMapper) {
        if (json == null) {
            return Collections.emptySet();
        }
        try {
            Set<String> strings = objectMapper.readValue(
                    json.data(), objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
            return strings.stream().map(WebUserAuthority::fromAuthority).collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Failed to deserialize authorities from JSON", e);
            return new HashSet<>();
        }
    }
}
