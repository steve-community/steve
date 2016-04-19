package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.repository.GenericRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.service.MailService;
import de.rwth.idsg.steve.service.ReleaseCheckService;
import de.rwth.idsg.steve.web.dto.SettingsForm;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * One controller for about and settings pages
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
@Controller
@RequestMapping(value = "/manager")
public class AboutSettingsController {

    @Autowired private GenericRepository genericRepository;
    @Autowired private LogController logController;
    @Autowired private SettingsRepository settingsRepository;
    @Autowired private MailService mailService;
    @Autowired private ReleaseCheckService releaseCheckService;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String ABOUT_PATH = "/about";
    private static final String SETTINGS_PATH = "/settings";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(value = ABOUT_PATH, method = RequestMethod.GET)
    public String getAbout(Model model) {
        model.addAttribute("version", CONFIG.getSteveVersion());
        model.addAttribute("db", genericRepository.getDBVersion());
        model.addAttribute("logFile", logController.getLogFilePath());
        model.addAttribute("systemTime", DateTime.now());
        model.addAttribute("systemTimeZone", DateTimeZone.getDefault());
        model.addAttribute("releaseReport", releaseCheckService.check());
        return "about";
    }

    @RequestMapping(value = SETTINGS_PATH, method = RequestMethod.GET)
    public String getSettings(Model model) {
        SettingsForm form = settingsRepository.getForm();
        model.addAttribute("features", NotificationFeature.values());
        model.addAttribute("settingsForm", form);
        return "settings";
    }

    @RequestMapping(params = "change", value = SETTINGS_PATH, method = RequestMethod.POST)
    public String postSettings(@Valid @ModelAttribute("settingsForm") SettingsForm settingsForm,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("features", NotificationFeature.values());
            return "settings";
        }

        settingsRepository.update(settingsForm);
        mailService.loadSettingsFromDB();
        return "redirect:/manager/settings";
    }

    @RequestMapping(params = "testMail", value = SETTINGS_PATH, method = RequestMethod.POST)
    public String testMail(@Valid @ModelAttribute("settingsForm") SettingsForm settingsForm,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("features", NotificationFeature.values());
            return "settings";
        }

        settingsRepository.update(settingsForm);
        mailService.loadSettingsFromDB();
        mailService.sendTestMail();

        return "redirect:/manager/settings";
    }
}
