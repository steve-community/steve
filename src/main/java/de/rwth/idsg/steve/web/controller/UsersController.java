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

import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.service.OcppTagService;
import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.utils.mapper.UserFormMapper;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 25.11.2015
 */
@AllArgsConstructor
@Controller
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

    @GetMapping
    public String getOverview(Model model) {
        initList(model, new UserQueryForm());
        return "data-man/users";
    }

    @GetMapping(value = QUERY_PATH)
    public String getQuery(@ModelAttribute(PARAMS) UserQueryForm params, Model model) {
        initList(model, params);
        return "data-man/users";
    }

    private void initList(Model model, UserQueryForm params) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("userList", userRepository.getOverview(params));
    }

    @GetMapping(value = DETAILS_PATH)
    public String getDetails(@PathVariable("userPk") int userPk, Model model) {
        var details = userRepository.getDetails(userPk);
        UserForm form = UserFormMapper.toForm(details);

        model.addAttribute("userForm", form);
        setTags(model, form.getIdTagList());
        return "data-man/userDetails";
    }

    @GetMapping(value = ADD_PATH)
    public String addGet(Model model) {
        setTags(model, List.of());
        model.addAttribute("userForm", new UserForm());
        return "data-man/userAdd";
    }

    @PostMapping(params = "add", value = ADD_PATH)
    public String addPost(@Valid @ModelAttribute("userForm") UserForm userForm,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            setTags(model, userForm.getIdTagList());
            return "data-man/userAdd";
        }

        userRepository.add(userForm);
        return toOverview();
    }

    @PostMapping(params = "update", value = UPDATE_PATH)
    public String update(@Valid @ModelAttribute("userForm") UserForm userForm,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            setTags(model, userForm.getIdTagList());
            return "data-man/userDetails";
        }

        userRepository.update(userForm);
        return toOverview();
    }

    @PostMapping(value = DELETE_PATH)
    public String delete(@PathVariable("userPk") int userPk) {
        userRepository.delete(userPk);
        return toOverview();
    }

    private void setTags(Model model, List<String> idTagsFromUser) {
        var fromDB = ocppTagService.getIdTagsWithoutUser();

        // new temp list because we want to have a specific order
        var idTagList = new ArrayList<>(fromDB.size() + idTagsFromUser.size());
        idTagList.addAll(idTagsFromUser);
        idTagList.addAll(fromDB);

        model.addAttribute("countryCodes", ControllerHelper.COUNTRY_DROPDOWN);
        model.addAttribute("idTagList", idTagList);
    }

    // -------------------------------------------------------------------------
    // Back to Overview
    // -------------------------------------------------------------------------

    @PostMapping(params = "backToOverview", value = ADD_PATH)
    public String addBackToOverview() {
        return toOverview();
    }

    @PostMapping(params = "backToOverview", value = UPDATE_PATH)
    public String updateBackToOverview() {
        return toOverview();
    }

    private static String toOverview() {
        return "redirect:/manager/users";
    }
}
