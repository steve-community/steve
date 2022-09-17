/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2019 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import de.rwth.idsg.steve.web.LocalDateTimeEditor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.09.2022
 */
@ControllerAdvice(basePackages = "de.rwth.idsg.steve.web.api")
@Slf4j
public class ApiControllerAdvice {

    private final MappingJackson2JsonView jsonView = new MappingJackson2JsonView();

    @InitBinder
    public void binder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(LocalDateTime.class, new LocalDateTimeEditor());
    }

    @ExceptionHandler(BindException.class)
    public ModelAndView handleBindException(HttpServletRequest req, BindException exception) {
        StringBuffer url = req.getRequestURL();
        log.error("Request: {} raised following exception.", url, exception);
        return createResponse(url, HttpStatus.BAD_REQUEST, "Error understanding the request");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ModelAndView handleResponseStatusException(HttpServletRequest req, ResponseStatusException exception) {
        StringBuffer url = req.getRequestURL();
        log.error("Request: {} raised following exception.", url, exception);
        return createResponse(url, exception.getStatus(), exception.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest req, Exception exception) {
        StringBuffer url = req.getRequestURL();
        log.error("Request: {} raised following exception.", url, exception);
        return createResponse(url, HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private ModelAndView createResponse(StringBuffer url, HttpStatus status, String message) {
        ModelAndView result = new ModelAndView(jsonView);
        result.setStatus(status);

        result.addObject("timestamp", DateTime.now().toString());
        result.addObject("status", status.value());
        result.addObject("error", status.getReasonPhrase());
        result.addObject("message", message);
        result.addObject("path", url);

        return result;
    }

    /**
     * This is just here to be used by Swagger and for documentation purposes. It mirrors the fields used in
     * {@link ApiControllerAdvice#createResponse(StringBuffer, HttpStatus, String)}
     */
    @Data
    public static class ApiErrorResponse {
        private String timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
    }

}
