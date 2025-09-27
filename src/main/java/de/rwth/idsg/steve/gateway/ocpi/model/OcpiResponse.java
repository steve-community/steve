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
package de.rwth.idsg.steve.gateway.ocpi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcpiResponse<T> {

    private T data;

    @JsonProperty("status_code")
    private Integer statusCode;

    @JsonProperty("status_message")
    private String statusMessage;

    private DateTime timestamp;

    public static <T> OcpiResponse<T> success(T data) {
        return OcpiResponse.<T>builder()
            .data(data)
            .statusCode(1000)
            .statusMessage("Success")
            .timestamp(DateTime.now())
            .build();
    }

    public static <T> OcpiResponse<T> error(Integer code, String message) {
        return OcpiResponse.<T>builder()
            .statusCode(code)
            .statusMessage(message)
            .timestamp(DateTime.now())
            .build();
    }
}