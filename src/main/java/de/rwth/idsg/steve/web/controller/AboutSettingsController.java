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
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.repository.GenericRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.service.DataImportExportService;
import de.rwth.idsg.steve.service.MailService;
import de.rwth.idsg.steve.service.ReleaseCheckService;
import de.rwth.idsg.steve.web.dto.DataExportForm;
import de.rwth.idsg.steve.web.dto.EndpointInfo;
import de.rwth.idsg.steve.web.dto.SettingsForm;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;

/**
 * One controller for about and settings pages
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 */
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/manager")
public class AboutSettingsController {

    private final GenericRepository genericRepository;
    private final LogController logController;
    private final SettingsRepository settingsRepository;
    private final MailService mailService;
    private final ReleaseCheckService releaseCheckService;
    private final SteveProperties steveProperties;
    private final DataImportExportService dataImportExportService;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String ABOUT_PATH = "/about";
    private static final String SETTINGS_PATH = "/settings";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @GetMapping(ABOUT_PATH)
    public String getAbout(Model model, @RequestHeader(HttpHeaders.HOST) String host, HttpServletRequest request) {
        String scheme = request.getScheme();
        String contextPath = request.getContextPath();

        model.addAttribute("version", steveProperties.getVersion());
        model.addAttribute("db", genericRepository.getDBVersion());
        model.addAttribute("logFile", logController.getLogFilePath());
        model.addAttribute("systemTime", DateTime.now());
        model.addAttribute("systemTimeZone", DateTimeZone.getDefault());
        model.addAttribute("releaseReport", releaseCheckService.check());
        model.addAttribute("endpointInfo", EndpointInfo.fromRequest(scheme, host, contextPath));

        model.addAttribute("exportForm", new DataExportForm());
        model.addAttribute("masterDataTableNames", String.join(", ", dataImportExportService.getMasterDataTableNames()));
        return "about";
    }

    @GetMapping(SETTINGS_PATH)
    public String getSettings(Model model) {
        SettingsForm form = settingsRepository.getForm();
        model.addAttribute("features", NotificationFeature.values());
        model.addAttribute("settingsForm", form);
        return "settings";
    }

    @PostMapping(params = "change", value = SETTINGS_PATH)
    public String postSettings(@Valid @ModelAttribute("settingsForm") SettingsForm settingsForm,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("features", NotificationFeature.values());
            return "settings";
        }

        settingsRepository.update(settingsForm);
        return "redirect:/manager/settings";
    }

    @PostMapping(params = "testMail", value = SETTINGS_PATH)
    public String testMail(@Valid @ModelAttribute("settingsForm") SettingsForm settingsForm,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("features", NotificationFeature.values());
            return "settings";
        }

        settingsRepository.update(settingsForm);
        mailService.sendTestMail();

        return "redirect:/manager/settings";
    }

    @GetMapping(value = ABOUT_PATH + "/export")
    public void exportZip(@ModelAttribute("exportForm") DataExportForm exportForm,
                          HttpServletResponse response) throws IOException {
        String fileName = "data-export_" + System.currentTimeMillis() + ".zip";
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", fileName);
        response.setHeader(headerKey, headerValue);
        response.setContentType("application/zip");

        dataImportExportService.exportZip(response.getOutputStream(), exportForm.getExportType());
    }

    @PostMapping(value = ABOUT_PATH + "/import")
    public String importZip(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (file.isEmpty()) {
            throw new SteveException.BadRequest("File is empty");
        }

        if (!file.getOriginalFilename().endsWith(".zip")) {
            throw new SteveException.BadRequest("File must be a ZIP archive");
        }

        dataImportExportService.importZip(file.getInputStream());
        return "redirect:/manager/home";
    }
}
