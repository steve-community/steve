package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.service.UserService;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Controller
@RequestMapping(value = "/manager/users")
public class UsersController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    private static final String PARAMS = "params";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String USERS_QUERY_PATH = "/query";

    private static final String ADD_PATH = "/add";
    private static final String UPDATE_PATH = "/update";
    private static final String DELETE_PATH = "/delete";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String get(Model model) {
        initGetList(model, new UserQueryForm());
        return "data-man/users";
    }

    @RequestMapping(value = USERS_QUERY_PATH, method = RequestMethod.GET)
    public String getQuery(@ModelAttribute(PARAMS) UserQueryForm params, Model model) {
        initGetList(model, params);
        return "data-man/users";
    }

    @RequestMapping(value = ADD_PATH, method = RequestMethod.POST)
    public String add(@Valid @ModelAttribute("userAddForm") UserForm u,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            initList(model, new UserQueryForm());
            model.addAttribute("userUpdateForm", new UserForm());
            return "data-man/users";
        }

        userRepository.addUser(u);
        return "redirect:/manager/users";
    }

    @RequestMapping(value = UPDATE_PATH, method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute("userUpdateForm") UserForm u,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            initList(model, new UserQueryForm());
            model.addAttribute("userAddForm", new UserForm());
            return "data-man/users";
        }

        userRepository.updateUser(u);
        return "redirect:/manager/users";
    }

    @RequestMapping(value = DELETE_PATH, method = RequestMethod.POST)
    public String delete(@RequestParam String idTag) {
        userRepository.deleteUser(idTag);
        return "redirect:/manager/users";
    }

    private void initGetList(Model model, UserQueryForm params) {
        initList(model, params);
        model.addAttribute("userAddForm", new UserForm());
        model.addAttribute("userUpdateForm", new UserForm());
    }

    private void initList(Model model, UserQueryForm params) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("idTagList", userRepository.getUserIdTags());
        model.addAttribute("parentIdTagList", userRepository.getParentIdTags());
        model.addAttribute("userList", userRepository.getUsers(params));
    }
}
