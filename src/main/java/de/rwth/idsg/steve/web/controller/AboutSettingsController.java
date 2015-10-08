package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.ocpp.OcppConstants;
import de.rwth.idsg.steve.repository.GenericRepository;
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
    @Autowired private OcppConstants ocppConstants;

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
        model.addAttribute("version", SteveConfiguration.STEVE_VERSION);
        model.addAttribute("db", genericRepository.getDBVersion());
        model.addAttribute("logFile", logController.getLogFilePath());
        model.addAttribute("systemTime", DateTime.now());
        model.addAttribute("systemTimeZone", DateTimeZone.getDefault());
        return "about";
    }

    @RequestMapping(value = SETTINGS_PATH, method = RequestMethod.GET)
    public String getSettings(Model model) {
        int heartbeat = ocppConstants.getHeartbeatIntervalInMinutes();
        int expiration = ocppConstants.getHoursToExpire();

        model.addAttribute("currentHeartbeat", heartbeat);
        model.addAttribute("currentExpiration", expiration);
        model.addAttribute("settingsForm", new SettingsForm(heartbeat, expiration));
        return "settings";
    }

    @RequestMapping(value = SETTINGS_PATH, method = RequestMethod.POST)
    public String postSettings(@Valid @ModelAttribute("settingsForm") SettingsForm settingsForm,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("currentHeartbeat", ocppConstants.getHeartbeatIntervalInMinutes());
            model.addAttribute("currentExpiration", ocppConstants.getHoursToExpire());
            return "settings";
        }

        ocppConstants.setHeartbeatIntervalInMinutes(settingsForm.getHeartbeat());
        ocppConstants.setHoursToExpire(settingsForm.getExpiration());
        return "redirect:/manager/settings";
    }
}
