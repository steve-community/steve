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

import de.rwth.idsg.steve.ocpp.ws.data.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record OcppOperationResponse<T>(
    int taskId,

    @Schema(description = """
        True when all stations have completed communication (either with a <i>CallResult</i>, <i>CallError</i>, or exception) before the platform timeout.
        If timeout occurs before completion, this will be false and only partial results will be available.
        """)
    boolean taskFinished,

    @Schema(description = """
        Responses where the station successfully received and processed the request, returning a <i>CallResult</i> without any communication errors.
        """)
    List<SuccessResponse<T>> successResponses,

    @Schema(description = """
        Responses where the station successfully received the request, but responded with a <i>CallError</i> without any communication errors.
        """)
    List<ErrorResponse> errorResponses,

    @Schema(description = """
        Exceptions encountered when this platform failed to send the request to the station or failed to process the station's response.
        """)
    List<CallException> exceptions
) {
    public record SuccessResponse<T>(
        String chargeBoxId,
        T response
    ) {
    }

    public record ErrorResponse(
        String chargeBoxId,
        ErrorCode errorCode,
        String errorDescription,
        String errorDetails
    ) {
    }

    public record CallException(
        String chargeBoxId,
        String exceptionMessage
    ) {
    }

    public static <T> OcppOperationResponse<T> from(RestCallback<T> callback) {
        List<SuccessResponse<T>> successResponses = callback.getSuccessResponsesByChargeBoxId().entrySet().stream()
            .map(e -> new SuccessResponse<>(e.getKey(), e.getValue()))
            .toList();

        List<ErrorResponse> errorResponses = callback.getErrorResponsesByChargeBoxId().entrySet().stream()
            .map(e -> new ErrorResponse(
                e.getKey(),
                e.getValue().getErrorCode(),
                e.getValue().getErrorDescription(),
                e.getValue().getErrorDetails()))
            .toList();

        List<CallException> exceptions = callback.getExceptionsByChargeBoxId().entrySet().stream()
            .map(e -> new CallException(e.getKey(), e.getValue().getMessage()))
            .toList();

        return new OcppOperationResponse<>(
            callback.getTaskId(),
            callback.isFinished(),
            successResponses,
            errorResponses,
            exceptions
        );
    }
}
