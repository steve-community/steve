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

import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.repository.GenericRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.service.MailService;
import de.rwth.idsg.steve.service.ReleaseCheckService;
import de.rwth.idsg.steve.web.dto.EndpointInfo;
import de.rwth.idsg.steve.web.dto.SettingsForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.time.ZoneId;
import jakarta.validation.Valid;

/**
 * One controller for about and settings pages
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 */
@Controller
@RequestMapping(value = "/manager")
@RequiredArgsConstructor
public class AboutSettingsController {

    private static final String PASSWORD_PLACEHOLDER = "********";

    private final GenericRepository genericRepository;
    private final LogController logController;
    private final SettingsRepository settingsRepository;
    private final MailService mailService;
    private final ReleaseCheckService releaseCheckService;
    private final SteveProperties steveProperties;
    private final EndpointInfo info;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String ABOUT_PATH = "/about";
    private static final String SETTINGS_PATH = "/settings";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @GetMapping(value = ABOUT_PATH)
    public String getAbout(Model model) {
        model.addAttribute("version", steveProperties.getSteveVersion());
        model.addAttribute("db", genericRepository.getDBVersion().orElse(null));
        model.addAttribute("logFile", logController.getLogFilePath());
        model.addAttribute("systemTime", Instant.now());
        model.addAttribute("systemTimeZone", ZoneId.systemDefault());
        model.addAttribute("releaseReport", releaseCheckService.check());
        model.addAttribute("endpointInfo", info);
        return "about";
    }

    @GetMapping(value = SETTINGS_PATH)
    public String getSettings(Model model) {
        var form = settingsRepository.getForm();
        if (form.getMailSettings().getPassword() != null) {
            form.getMailSettings().setPassword(PASSWORD_PLACEHOLDER);
        }
        model.addAttribute("features", NotificationFeature.values());
        model.addAttribute("settingsForm", form);
        return "settings";
    }

    @PostMapping(params = "change", value = SETTINGS_PATH)
    public String postSettings(
            @Valid @ModelAttribute("settingsForm") SettingsForm settingsForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("features", NotificationFeature.values());
            return "settings";
        }

        if (PASSWORD_PLACEHOLDER.equals(settingsForm.getMailSettings().getPassword())) {
            settingsForm.getMailSettings().setPassword(null);
        }
        settingsRepository.update(settingsForm);
        return "redirect:/manager/settings";
    }

    @PostMapping(params = "testMail", value = SETTINGS_PATH)
    public String testMail(
            @Valid @ModelAttribute("settingsForm") SettingsForm settingsForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("features", NotificationFeature.values());
            return "settings";
        }

        mailService.sendTestMail(settingsForm.getMailSettings());

        return "redirect:/manager/settings";
    }
}
