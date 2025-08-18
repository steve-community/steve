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

import de.rwth.idsg.steve.service.WebUserService;
import de.rwth.idsg.steve.web.dto.WebUserAuthority;
import de.rwth.idsg.steve.web.dto.WebUserBaseForm;
import de.rwth.idsg.steve.web.dto.WebUserForm;
import de.rwth.idsg.steve.web.dto.WebUserQueryForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/manager/webusers")
public class WebUsersController {

    private final WebUserService webUserService;

    private static final String PARAMS = "params";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String QUERY_PATH = "/query";

    private static final String DETAILS_PATH = "/details/{webUserPk}";
    private static final String DELETE_PATH = "/delete/{webUserPk}";
    private static final String UPDATE_PATH = "/update";
    private static final String ADD_PATH = "/add";
    private static final String PASSWORD_PATH = "/password/{webUserName}";
    private static final String API_PASSWORD_PATH = "/apipassword/{webUserName}";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @GetMapping
    public String getOverview(Model model) {
        initList(model, new WebUserQueryForm());
        return "data-man/webusers";
    }

    @GetMapping(value = QUERY_PATH)
    public String getQuery(@ModelAttribute(PARAMS) WebUserQueryForm params, Model model) {
        initList(model, params);
        return "data-man/webusers";
    }

    private void initList(Model model, WebUserQueryForm params) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("webuserList", webUserService.getOverview(params));
    }

    @GetMapping(value = DETAILS_PATH)
    public String getDetails(@PathVariable("webUserPk") Integer webUserPk, Model model) {
       WebUserBaseForm form = webUserService.getDetails(webUserPk);

        model.addAttribute("webuserForm", form);
        model.addAttribute("availableAuthorities", WebUserAuthority.values());
        return "data-man/webuserDetails";
    }

    @GetMapping(value = ADD_PATH)
    public String addGet(Model model) {
        WebUserForm webUserForm = new WebUserForm();
        webUserForm.setAuthorities(WebUserAuthority.USER);
        model.addAttribute("webuserForm", webUserForm);
        model.addAttribute("availableAuthorities", WebUserAuthority.values());
        return "data-man/webuserAdd";
    }

    @PostMapping(params = "add", value = ADD_PATH)
    public String addPost(@Valid @ModelAttribute("webuserForm") WebUserForm webuserForm,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("availableAuthorities", WebUserAuthority.values());
            return "data-man/webuserAdd";
        }

        webUserService.add(webuserForm);
        return toOverview();
    }

    @PostMapping(params = "update", value = UPDATE_PATH)
    public String update(@Valid @ModelAttribute("webuserForm") WebUserBaseForm webuserBaseForm,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("availableAuthorities", WebUserAuthority.values());
            return "data-man/webuserDetails";
        }

        webUserService.update(webuserBaseForm);
        return toOverview();
    }

    @GetMapping(value = PASSWORD_PATH)
    public String passwordChangeGet(@PathVariable("webUserName") String webUserName, Model model) {
        WebUserBaseForm base = webUserService.getDetails(webUserName);
        WebUserForm webUserForm = fromBase(base);
        model.addAttribute("webuserForm", webUserForm);
        return "data-man/webuserPassword";
    }

    private static WebUserForm fromBase(WebUserBaseForm webUserBaseForm) {
        WebUserForm webUserForm = new WebUserForm();
        webUserForm.setWebUserPk(webUserBaseForm.getWebUserPk());
        webUserForm.setWebUsername(webUserBaseForm.getWebUsername());
        webUserForm.setAuthorities(webUserBaseForm.getAuthorities());
        webUserForm.setEnabled(webUserBaseForm.getEnabled());
        return webUserForm;
    }

    @PostMapping(params = "change", value = PASSWORD_PATH)
    public String passwordChange(@Valid @ModelAttribute("webuserForm") WebUserForm webuserForm,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "data-man/webuserPassword";
        }

        webUserService.updatePassword(webuserForm);
        return toDetails(webuserForm.getWebUserPk());
    }

    @GetMapping(value = API_PASSWORD_PATH)
    public String apiPasswordChangeGet(@PathVariable("webUserName") String webUserName, Model model) {
        WebUserBaseForm base = webUserService.getDetails(webUserName);
        WebUserForm webUserForm = fromBase(base);
        model.addAttribute("webuserForm", webUserForm);
        return "data-man/webuserApiPassword";
    }

    @PostMapping(params = "change", value = API_PASSWORD_PATH)
    public String apiPasswordChange(@Valid @ModelAttribute("webuserForm") WebUserForm webuserForm,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "data-man/webuserApiPassword";
        }

        webUserService.updateApiPassword(webuserForm);
        return toDetails(webuserForm.getWebUserPk());
    }

    @PostMapping(value = DELETE_PATH)
    public String delete(@PathVariable("webUserPk") Integer webUserPk) {
        webUserService.deleteUser(webUserPk);
        return toOverview();
    }

    // -------------------------------------------------------------------------
    // Back to Overview
    // -------------------------------------------------------------------------

    @PostMapping(params = "backToOverview", value = PASSWORD_PATH)
    public String passwordBackToOverview(@Valid @ModelAttribute("webuserForm") WebUserForm webuserForm) {
        return toDetails(webuserForm.getWebUserPk());
    }

    @PostMapping(params = "backToOverview", value = API_PASSWORD_PATH)
    public String apiPasswordBackToOverview(@Valid @ModelAttribute("webuserForm") WebUserForm webuserForm) {
        return toDetails(webuserForm.getWebUserPk());
    }

    @PostMapping(params = "backToOverview", value = ADD_PATH)
    public String addBackToOverview() {
        return toOverview();
    }

    @PostMapping(params = "backToOverview", value = UPDATE_PATH)
    public String updateBackToOverview() {
        return toOverview();
    }

    private static String toOverview() {
        return "redirect:/manager/webusers";
    }

    private static String toDetails(Integer userPk) {
      return String.format("redirect:/manager/webusers/details/%s", userPk);
    }
}
