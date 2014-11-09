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

    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String getAbout(Model model) {
        model.addAttribute("version", SteveConfiguration.STEVE_VERSION);
        model.addAttribute("db", genericRepository.getDBVersion());
        return "about";
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String getSettings(Model model) {
        OcppSettings os = new OcppSettings();
        os.setExpiration(OcppConstants.getHoursToExpire());
        os.setHeartbeat(OcppConstants.getHeartbeatInterval());

        model.addAttribute("settingsForm", os);
        return "settings";
    }

    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public String postSettings(@Valid @ModelAttribute("settingsForm") OcppSettings settingsForm,
                               Model model, BindingResult result) {

        if (result.hasErrors()) {
            return "settings";
        }

        OcppConstants.setHeartbeatInterval(settingsForm.getHeartbeat());
        OcppConstants.setHoursToExpire(settingsForm.getExpiration());
        return "redirect:/manager/settings";
    }
}