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
package de.rwth.idsg.steve.web;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.08.2014
 */
@ControllerAdvice(basePackages = "de.rwth.idsg.steve.web.controller")
@Slf4j
public class GlobalControllerAdvice {

    @InitBinder
    public void binder(WebDataBinder binder, NativeWebRequest webRequest) {
        BatchInsertConverter batchInsertConverter = new BatchInsertConverter();

        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(LocalDate.class, new LocalDateEditor());
        binder.registerCustomEditor(Instant.class, InstantEditor.fromRequest(webRequest));
        binder.registerCustomEditor(ChargePointSelect.class, new ChargePointSelectEditor());

        binder.registerCustomEditor(List.class, "idList", batchInsertConverter);
        binder.registerCustomEditor(List.class, "recipients", batchInsertConverter);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletRequest req, Exception exception) {
        log.error("Request: {} raised following exception.", req.getRequestURL(), exception);

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", exception);
        mav.setViewName("00-error");
        return mav;
    }
}
