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
package de.rwth.idsg.steve.web;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.08.2014
 */
@ControllerAdvice(basePackages = "de.rwth.idsg.steve.web")
@Slf4j
public class GlobalControllerAdvice {

    @InitBinder
    public void binder(WebDataBinder binder) {
        BatchInsertConverter batchInsertConverter = new BatchInsertConverter();

        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(LocalDate.class, new LocalDateEditor());
        binder.registerCustomEditor(LocalDateTime.class, new LocalDateTimeEditor());
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
