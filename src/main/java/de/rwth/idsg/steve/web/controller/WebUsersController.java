/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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


import de.rwth.idsg.steve.service.WebUserService;
import de.rwth.idsg.steve.web.dto.WebUserForm;
import de.rwth.idsg.steve.web.dto.WebUserQueryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.validation.Valid;


@Controller
@RequestMapping(value = "/manager/webusers")
public class WebUsersController {

    @Autowired private WebUserService webUserService;

    private static final String PARAMS = "params";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String QUERY_PATH = "/query";

    private static final String DETAILS_PATH = "/details/{webuserpk}";
    private static final String DELETE_ALL_PATH = "/delete/{webuserpk}";
    private static final String UPDATE_PATH = "/update";
    private static final String ADD_PATH = "/add";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String getOverview(Model model) {
        initList(model, new WebUserQueryForm());
        return "data-man/webusers";
    }

    @RequestMapping(value = QUERY_PATH, method = RequestMethod.GET)
    public String getQuery(@ModelAttribute(PARAMS) WebUserQueryForm params, Model model) {
        initList(model, params);
        return "data-man/webusers";
    }

    private void initList(Model model, WebUserQueryForm params) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("webuserList", webUserService.getOverview(params));
    }

    @RequestMapping(value = DETAILS_PATH, method = RequestMethod.GET)
    public String getDetails(@PathVariable("webuserpk") Integer webuserpk, Model model) {
       WebUserForm form = webUserService.getDetails(webuserpk);

        model.addAttribute("webuserForm", form);
        return "data-man/webuserDetails";
    }

    @RequestMapping(value = ADD_PATH, method = RequestMethod.GET)
    public String addGet(Model model) {
        model.addAttribute("webuserForm", new WebUserForm());
        return "data-man/webuserAdd";
    }

    @RequestMapping(params = "add", value = ADD_PATH, method = RequestMethod.POST)
    public String addPost(@Valid @ModelAttribute("webuserForm") WebUserForm webuserForm,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "data-man/webuserAdd";
        }

        // password is Null, Blank/Empty or less than 8 Characters then don't add and show an Error
        if (webuserForm.getPassword() == null) {
            webuserForm.setPwerror(Boolean.TRUE);
            return "data-man/webuserAdd";

        }

        if ((webuserForm.getPassword().length() < 8) | webuserForm.getPassword().isBlank())
        /* | webuserForm.getPassword().isEmpty() in isBlank included */ {
            webuserForm.setPwerror(Boolean.TRUE);
            return "data-man/webuserAdd";
        }

        // Compare both the password inputs
        if (!webuserForm.getPassword().equals(webuserForm.getPasswordComparison())) {
            webuserForm.setPwerror(Boolean.TRUE);
            return "data-man/webuserAdd";
        }

        webuserForm.setPwerror(Boolean.FALSE);

        webUserService.add(webuserForm);
        return toOverview();
    }

    @RequestMapping(params = "update", value = UPDATE_PATH, method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute("webuserForm") WebUserForm webuserForm,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "data-man/webuserDetails";
        }

        if (webuserForm.getPassword() != null) {
            if (!webuserForm.getPassword().equals(webuserForm.getPasswordComparison())) {
                webuserForm.setPwerror(Boolean.TRUE);
                return "data-man/webuserDetails";
            }
            // password is Blank or less than 8 Characters then don't update and show an Error
            // --> WebUserRepositoryImpl: Null and Empty update without updating the password
            if (webuserForm.getPassword().isBlank() | webuserForm.getPassword().length() < 8) {
                webuserForm.setPwerror(Boolean.TRUE);
                return "data-man/webuserDetails";
            }
        }

        webUserService.update(webuserForm);
        return toOverview();
    }

    @RequestMapping(value = DELETE_ALL_PATH, method = RequestMethod.POST)
    public String delete(@PathVariable("webuserpk") Integer webuserpk) {
        webUserService.deleteUser(webuserpk);
        return toOverview();
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
        return "redirect:/manager/webusers";
    }
}
