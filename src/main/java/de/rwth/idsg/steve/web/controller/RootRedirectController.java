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

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.09.2025
 */
@Controller
@RequestMapping("/")
public class RootRedirectController {

    @GetMapping
    public RedirectView redirectToManager(HttpServletRequest request) {
        // Use RedirectView to generate absolute URLs that include the port
        // This respects X-Forwarded-* headers when server.forward-headers-strategy = native
        RedirectView redirectView = new RedirectView("/manager", false);
        redirectView.setContextRelative(false);
        return redirectView;
    }
}
