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
package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.EventRepository;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.web.dto.SecurityEventsQueryForm;
import de.rwth.idsg.steve.web.dto.StatusEventType;
import de.rwth.idsg.steve.web.dto.StatusEventsQueryForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import java.util.Collections;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 07.11.2025
 */
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/manager/events")
public class EventsController {

    private final EventRepository eventRepository;
    private final ChargePointService chargePointService;

    private static final String PARAMS = "params";

    @GetMapping("/security")
    public String getSecurityEvents(@Valid @ModelAttribute(PARAMS) SecurityEventsQueryForm params,
                                    BindingResult result, Model model) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("cpList", chargePointService.getChargeBoxIds());

        if (result.hasErrors()) {
            model.addAttribute("events", Collections.emptyList());
        } else {
            model.addAttribute("events", eventRepository.getSecurityEvents(params));
        }

        return "events-certs/securityEvents";
    }

    @GetMapping("/status")
    public String getStatusEvents(@Valid @ModelAttribute(PARAMS) StatusEventsQueryForm params,
                                  BindingResult result, Model model) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("cpList", chargePointService.getChargeBoxIds());

        if (result.hasErrors()) {
            model.addAttribute("events", Collections.emptyList());
        } else {
            model.addAttribute("events", eventRepository.getStatusEvents(params));
        }

        return "events-certs/statusEvents";
    }

    @GetMapping("/status/{eventType}/{jobId}")
    public String getStatusEventJobDetails(@PathVariable("eventType") StatusEventType eventType,
                                           @PathVariable("jobId") int jobId,
                                           Model model) {
        var details = switch (eventType) {
            case FirmwareUpdate -> eventRepository.getFirmwareUpdateDetails(jobId);
            case LogUpload -> eventRepository.getLogUploadDetails(jobId);
        };

        model.addAttribute("eventType", eventType.name());
        model.addAttribute("details", details);
        return "events-certs/statusEventJobDetails";
    }
}
