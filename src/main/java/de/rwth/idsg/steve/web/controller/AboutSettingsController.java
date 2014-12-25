package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.OcppConstants;
import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.repository.GenericRepository;
import de.rwth.idsg.steve.web.dto.OcppSettings;
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
 * 
 */
@Controller
public class AboutSettingsController {

    @Autowired private GenericRepository genericRepository;
    @Autowired private LogController logController;
    @Autowired private OcppConstants ocppConstants;

    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String getAbout(Model model) {
        model.addAttribute("version", SteveConfiguration.STEVE_VERSION);
        model.addAttribute("db", genericRepository.getDBVersion());
        model.addAttribute("logFile", logController.getLogFilePath());
        return "about";
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String getSettings(Model model) {
        int heartbeat = ocppConstants.getHeartbeatIntervalInMinutes();
        int expiration = ocppConstants.getHoursToExpire();

        model.addAttribute("currentHeartbeat", heartbeat);
        model.addAttribute("currentExpiration", expiration);
        model.addAttribute("settingsForm", new OcppSettings(heartbeat, expiration));
        return "settings";
    }

    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public String postSettings(@Valid @ModelAttribute("settingsForm") OcppSettings settingsForm,
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