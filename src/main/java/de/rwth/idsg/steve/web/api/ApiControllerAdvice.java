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
package de.rwth.idsg.steve.web.api;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.web.LocalDateTimeEditor;
import de.rwth.idsg.steve.web.api.exception.BadRequestException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.09.2022
 */
@RestControllerAdvice(basePackages = "de.rwth.idsg.steve.web.api")
@Slf4j
public class ApiControllerAdvice {

    @InitBinder
    public void binder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(LocalDateTime.class, LocalDateTimeEditor.forApi());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBindException(HttpServletRequest req, BindException exception) {
        String url = req.getRequestURL().toString();
        log.error("Request: {} raised following exception.", url, exception);
        return createResponse(url, HttpStatus.BAD_REQUEST, "Error understanding the request");
    }

    @ExceptionHandler(SteveException.NotFound.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFoundException(HttpServletRequest req, SteveException.NotFound exception) {
        String url = req.getRequestURL().toString();
        log.error("Request: {} raised following exception.", url, exception);
        return createResponse(url, HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(SteveException.AlreadyExists.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiErrorResponse handleAlreadyExistsException(HttpServletRequest req, SteveException.AlreadyExists exception) {
        String url = req.getRequestURL().toString();
        log.error("Request: {} raised following exception.", url, exception);
        return createResponse(url, HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequestException(HttpServletRequest req, BadRequestException exception) {
        String url = req.getRequestURL().toString();
        log.error("Request: {} raised following exception.", url, exception);
        return createResponse(url, HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMethodArgumentTypeMismatchException(HttpServletRequest req, MethodArgumentTypeMismatchException exception) {
        String url = req.getRequestURL().toString();
        log.error("Request: {} raised following exception.", url, exception);
        return createResponse(url, HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleException(HttpServletRequest req, Exception exception) {
        String url = req.getRequestURL().toString();
        log.error("Request: {} raised following exception.", url, exception);
        return createResponse(url, HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    public static ApiErrorResponse createResponse(String url, HttpStatus status, String message) {
        ApiErrorResponse result = new ApiErrorResponse();

        result.setTimestamp(DateTime.now());
        result.setStatus(status.value());
        result.setError(status.getReasonPhrase());
        result.setMessage(message);
        result.setPath(url);

        return result;
    }

    @Data
    public static class ApiErrorResponse {
        private DateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
    }

}
