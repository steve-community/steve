package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.service.UserService;
import de.rwth.idsg.steve.web.dto.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.sql.Timestamp;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toTimestamp;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Controller
@RequestMapping(value = "/manager/users")
public class UsersController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String ADD_PATH = "/add";
    private static final String UPDATE_PATH = "/update";
    private static final String DELETE_PATH = "/delete";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String get(Model model) {
        model.addAttribute("userList", userService.getUsers());
        model.addAttribute("userAddForm", new UserForm());
        model.addAttribute("userUpdateForm", new UserForm());
        return "data-man/users";
    }

    @RequestMapping(value = ADD_PATH, method = RequestMethod.POST)
    public String add(@Valid @ModelAttribute("userAddForm") UserForm u,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("userList", userService.getUsers());
            model.addAttribute("userUpdateForm", new UserForm());
            return "data-man/users";
        }

        Timestamp expiryTimestamp = toTimestamp(u.getExpiration());
        userRepository.addUser(u.getIdTag(), u.getParentIdTag(), expiryTimestamp);
        return "redirect:/manager/users";
    }

    @RequestMapping(value = UPDATE_PATH, method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute("userUpdateForm") UserForm u,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("userList", userService.getUsers());
            model.addAttribute("userAddForm", new UserForm());
            return "data-man/users";
        }

        Timestamp expiryTimestamp = toTimestamp(u.getExpiration());
        userRepository.updateUser(u.getIdTag(), u.getParentIdTag(), expiryTimestamp, u.getBlocked());
        return "redirect:/manager/users";
    }

    @RequestMapping(value = DELETE_PATH, method = RequestMethod.POST)
    public String delete(@RequestParam String idTag) {
        userRepository.deleteUser(idTag);
        return "redirect:/manager/users";
    }
}
