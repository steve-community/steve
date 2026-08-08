/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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

import de.rwth.idsg.steve.service.OcppTagService;
import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.utils.mapper.OcppTagFormMapper;
import de.rwth.idsg.steve.web.dto.OcppTagBatchInsertForm;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

import java.util.Collections;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 26.11.2015
 */
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/manager/ocppTags")
public class OcppTagsController {

    protected final OcppTagService ocppTagService;

    protected static final String PARAMS = "params";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    protected static final String QUERY_PATH = "/query";

    protected static final String DETAILS_PATH = "/details/{ocppTagPk}";
    protected static final String DELETE_PATH = "/delete/{ocppTagPk}";
    protected static final String UPDATE_PATH = "/update";
    protected static final String ADD_PATH = "/add";

    protected static final String ADD_SINGLE_PATH = "/add/single";
    protected static final String ADD_BATCH_PATH = "/add/batch";

    protected static final String UNKNOWN_REMOVE_PATH = "/unknown/remove/{idTag}/";
    protected static final String UNKNOWN_ADD_PATH = "/unknown/add/{idTag}/";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @GetMapping
    public String get(Model model) {
        initList(model, new OcppTagQueryForm());
        return "data-man/ocppTags";
    }

    @GetMapping(QUERY_PATH)
    public String getQuery(@ModelAttribute(PARAMS) OcppTagQueryForm params, Model model) {
        initList(model, params);
        return "data-man/ocppTags";
    }

    @GetMapping(DETAILS_PATH)
    public String getDetails(@PathVariable("ocppTagPk") int ocppTagPk, Model model) {
        OcppTagActivityRecord record = ocppTagService.getRecord(ocppTagPk);
        OcppTagForm form = OcppTagFormMapper.toForm(record);

        model.addAttribute("activeTransactionCount", record.getActiveTransactionCount());
        model.addAttribute("ocppTagForm", form);
        setTags(model);
        return "data-man/ocppTagDetails";
    }

    @GetMapping(ADD_PATH)
    public String addGet(Model model) {
        setTags(model);
        model.addAttribute("ocppTagForm", new OcppTagForm());
        model.addAttribute("batchInsertForm", new OcppTagBatchInsertForm());
        return "data-man/ocppTagAdd";
    }

    @PostMapping(params = "add", value = ADD_SINGLE_PATH)
    public String addSinglePost(@Valid @ModelAttribute("ocppTagForm") OcppTagForm ocppTagForm,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            setTags(model);
            model.addAttribute("batchInsertForm", new OcppTagBatchInsertForm());
            return "data-man/ocppTagAdd";
        }

        ocppTagService.addOcppTag(ocppTagForm);
        return toOverview();
    }

    @PostMapping(ADD_BATCH_PATH)
    public String addBatchPost(@Valid @ModelAttribute("batchInsertForm") OcppTagBatchInsertForm form,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            setTags(model);
            model.addAttribute("ocppTagForm", new OcppTagForm());
            return "data-man/ocppTagAdd";
        }

        ocppTagService.addOcppTagList(form.getIdList());
        return toOverview();
    }

    @PostMapping(params = "update", value = UPDATE_PATH)
    public String update(@Valid @ModelAttribute("ocppTagForm") OcppTagForm ocppTagForm,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            setTags(model);
            return "data-man/ocppTagDetails";
        }

        ocppTagService.updateOcppTag(ocppTagForm);
        return toOverview();
    }

    @PostMapping(DELETE_PATH)
    public String delete(@PathVariable("ocppTagPk") int ocppTagPk) {
        ocppTagService.deleteOcppTag(ocppTagPk);
        return toOverview();
    }

    @PostMapping(UNKNOWN_ADD_PATH)
    public String addUnknownIdTag(@PathVariable("idTag") String idTag) {
        ocppTagService.addOcppTagList(Collections.singletonList(idTag));
        return toOverview();
    }

    @PostMapping(UNKNOWN_REMOVE_PATH)
    public String removeUnknownIdTag(@PathVariable("idTag") String idTag) {
        ocppTagService.removeUnknown(Collections.singletonList(idTag));
        return toOverview();
    }

    private void initList(Model model, OcppTagQueryForm params) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("idTagList", ocppTagService.getIdTags());
        model.addAttribute("parentIdTagList", ocppTagService.getParentIdTags());
        model.addAttribute("ocppTagList", ocppTagService.getOverview(params));
        model.addAttribute("unknownList", ocppTagService.getUnknownOcppTags());
    }

    protected void setTags(Model model) {
        model.addAttribute("idTagList", ControllerHelper.idTagEnhancer(ocppTagService.getIdTags()));
    }

    // -------------------------------------------------------------------------
    // Back to Overview
    // -------------------------------------------------------------------------

    @PostMapping(params = "backToOverview", value = ADD_SINGLE_PATH)
    public String addBackToOverview() {
        return toOverview();
    }

    @PostMapping(params = "backToOverview", value = UPDATE_PATH)
    public String updateBackToOverview() {
        return toOverview();
    }

    protected String toOverview() {
        return "redirect:/manager/ocppTags";
    }

}
