package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.web.dto.User;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.sql.Timestamp;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Controller
public class UsersController {

    @Autowired UserRepository userRepository;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String get(Model model) {
        model.addAttribute("userList", userRepository.getUsers());
        model.addAttribute("user", new User());
        return "data-man/users";
    }

    @RequestMapping(value = "/users/add", method = RequestMethod.POST)
    public String add(@Valid @ModelAttribute("user") User user) {

//        Timestamp expiryTimestamp = null;
//        if (!InputUtils.isNullOrEmpty(expiryDate)) {
//            if (InputUtils.isNullOrEmpty(expiryTime)) expiryTime = "00:00";
//            String dateTime = expiryDate + " " + expiryTime;
//            expiryTimestamp = DateTimeUtils.convertToTimestamp(dateTime);

        Timestamp expiryTimestamp = null;
        DateTime dt = user.getExpiryDate().toDateTimeAtStartOfDay();

        if (user.getExpiryTime() == null) {
            expiryTimestamp = new Timestamp(dt.getMillis());
        } else {
            LocalTime l = user.getExpiryTime();
            dt = dt.withField(DateTimeFieldType.hourOfDay(), l.getHourOfDay());
            // TODO TODO
        }

        //userRepository.addUser(user.getIdTag(), user.getParentIdTag(), user.getExpiryTime());
        return "redirect:/manager/users";
    }

    @RequestMapping(value = "/users/update", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute("user") User user) {

//        Timestamp expiryTimestamp = null;
//        if (!InputUtils.isNullOrEmpty(expiryDate)) {
//            if (InputUtils.isNullOrEmpty(expiryTime)) expiryTime = "00:00";
//            String dateTime = expiryDate + " " + expiryTime;
//            expiryTimestamp = DateTimeUtils.convertToTimestamp(dateTime);
//        }
//
//        userRepository.updateUser(idTag, parentIdTag, expiryTimestamp, blockUser);
        return "redirect:/manager/users";
    }

    @RequestMapping(value = "/users/delete", method = RequestMethod.POST)
    public String delete(@RequestParam String idTag) throws SteveException {
        userRepository.deleteUser(idTag);
        return "redirect:/manager/users";
    }
}
