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
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.service.OcppTagService;
import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.utils.mapper.UserFormMapper;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 25.11.2015
 */
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/manager/users")
public class UsersController {

    private final OcppTagService ocppTagService;
    private final UserRepository userRepository;

    private static final String PARAMS = "params";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String QUERY_PATH = "/query";

    private static final String DETAILS_PATH = "/details/{userPk}";
    private static final String DELETE_PATH = "/delete/{userPk}";
    private static final String UPDATE_PATH = "/update";
    private static final String ADD_PATH = "/add";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String getOverview(Model model) {
        initList(model, new UserQueryForm());
        return "data-man/users";
    }

    @RequestMapping(value = QUERY_PATH, method = RequestMethod.GET)
    public String getQuery(@ModelAttribute(PARAMS) UserQueryForm params, Model model) {
        initList(model, params);
        return "data-man/users";
    }

    private void initList(Model model, UserQueryForm params) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("userList", userRepository.getOverview(params));
        model.addAttribute("features", NotificationFeature.getUserValues());
    }

    @RequestMapping(value = DETAILS_PATH, method = RequestMethod.GET)
    public String getDetails(@PathVariable("userPk") int userPk, Model model) {
        User.Details details = userRepository.getDetails(userPk);
        UserForm form = UserFormMapper.toForm(details);

        model.addAttribute("userForm", form);
        model.addAttribute("features", NotificationFeature.getUserValues());
        setTags(model, form.getIdTagList());
        return "data-man/userDetails";
    }

    @RequestMapping(value = ADD_PATH, method = RequestMethod.GET)
    public String addGet(Model model) {
        setTags(model, List.of());
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("features", NotificationFeature.getUserValues());
        return "data-man/userAdd";
    }

    @RequestMapping(params = "add", value = ADD_PATH, method = RequestMethod.POST)
    public String addPost(@Valid @ModelAttribute("userForm") UserForm userForm,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            setTags(model, userForm.getIdTagList());
            return "data-man/userAdd";
        }

        userRepository.add(userForm);
        return toOverview();
    }

    @RequestMapping(params = "update", value = UPDATE_PATH, method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute("userForm") UserForm userForm,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            setTags(model, userForm.getIdTagList());
            return "data-man/userDetails";
        }

        userRepository.update(userForm);
        return toOverview();
    }

    @RequestMapping(value = DELETE_PATH, method = RequestMethod.POST)
    public String delete(@PathVariable("userPk") int userPk) {
        userRepository.delete(userPk);
        return toOverview();
    }

    private void setTags(Model model, List<String> idTagsFromUser) {
        List<String> fromDB = ocppTagService.getIdTagsWithoutUser();

        // new temp list because we want to have a specific order
        List<String> idTagList = new ArrayList<>(fromDB.size() + idTagsFromUser.size());
        idTagList.addAll(idTagsFromUser);
        idTagList.addAll(fromDB);

        model.addAttribute("countryCodes", ControllerHelper.COUNTRY_DROPDOWN);
        model.addAttribute("idTagList", idTagList);
    }

    // -------------------------------------------------------------------------
    // Back to Overview
    // -------------------------------------------------------------------------

    @RequestMapping(params = "backToOverview", value = ADD_PATH, method = RequestMethod.POST)
    public String addBackToOverview() {
        return toOverview();
    }

    @RequestMapping(params = "backToOverview", value = UPDATE_PATH, method = RequestMethod.POST)
    public String updateBackToOverview() {
        return toOverview();
    }

    private String toOverview() {
        return "redirect:/manager/users";
    }
}
