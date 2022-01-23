/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.service.OcppTagService;
import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.utils.mapper.OcppTagFormMapper;
import de.rwth.idsg.steve.web.dto.OcppTagBatchInsertForm;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 26.11.2015
 */
@Controller
@RequestMapping(value = "/manager/ocppTags")
public class OcppTagsController {

    @Autowired protected OcppTagRepository ocppTagRepository;
    @Autowired protected OcppTagService ocppTagService;

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

    @RequestMapping(method = RequestMethod.GET)
    public String get(Model model) {
        initList(model, new OcppTagQueryForm());
        return "data-man/ocppTags";
    }

    @RequestMapping(value = QUERY_PATH, method = RequestMethod.GET)
    public String getQuery(@ModelAttribute(PARAMS) OcppTagQueryForm params, Model model) {
        initList(model, params);
        return "data-man/ocppTags";
    }

    @RequestMapping(value = DETAILS_PATH, method = RequestMethod.GET)
    public String getDetails(@PathVariable("ocppTagPk") int ocppTagPk, Model model) {
        OcppTagActivityRecord record = ocppTagRepository.getRecord(ocppTagPk);
        OcppTagForm form = OcppTagFormMapper.toForm(record);

        model.addAttribute("activeTransactionCount", record.getActiveTransactionCount());
        model.addAttribute("ocppTagForm", form);
        setTags(model);
        return "data-man/ocppTagDetails";
    }

    @RequestMapping(value = ADD_PATH, method = RequestMethod.GET)
    public String addGet(Model model) {
        setTags(model);
        model.addAttribute("ocppTagForm", new OcppTagForm());
        model.addAttribute("batchInsertForm", new OcppTagBatchInsertForm());
        return "data-man/ocppTagAdd";
    }

    @RequestMapping(params = "add", value = ADD_SINGLE_PATH, method = RequestMethod.POST)
    public String addSinglePost(@Valid @ModelAttribute("ocppTagForm") OcppTagForm ocppTagForm,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            setTags(model);
            model.addAttribute("batchInsertForm", new OcppTagBatchInsertForm());
            return "data-man/ocppTagAdd";
        }

        add(ocppTagForm);
        return toOverview();
    }

    @RequestMapping(value = ADD_BATCH_PATH, method = RequestMethod.POST)
    public String addBatchPost(@Valid @ModelAttribute("batchInsertForm") OcppTagBatchInsertForm form,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            setTags(model);
            model.addAttribute("ocppTagForm", new OcppTagForm());
            return "data-man/ocppTagAdd";
        }

        add(form.getIdList());
        return toOverview();
    }

    @RequestMapping(params = "update", value = UPDATE_PATH, method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute("ocppTagForm") OcppTagForm ocppTagForm,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            setTags(model);
            return "data-man/ocppTagDetails";
        }

        ocppTagRepository.updateOcppTag(ocppTagForm);
        return toOverview();
    }

    @RequestMapping(value = DELETE_PATH, method = RequestMethod.POST)
    public String delete(@PathVariable("ocppTagPk") int ocppTagPk) {
        ocppTagRepository.deleteOcppTag(ocppTagPk);
        return toOverview();
    }

    @RequestMapping(value = UNKNOWN_ADD_PATH, method = RequestMethod.POST)
    public String addUnknownIdTag(@PathVariable("idTag") String idTag) {
        add(Collections.singletonList(idTag));
        return toOverview();
    }

    @RequestMapping(value = UNKNOWN_REMOVE_PATH, method = RequestMethod.POST)
    public String removeUnknownIdTag(@PathVariable("idTag") String idTag) {
        ocppTagService.removeUnknown(idTag);
        return toOverview();
    }

    private void initList(Model model, OcppTagQueryForm params) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("idTagList", ocppTagRepository.getIdTags());
        model.addAttribute("parentIdTagList", ocppTagRepository.getParentIdTags());
        model.addAttribute("ocppTagList", ocppTagRepository.getOverview(params));
        model.addAttribute("unknownList", ocppTagService.getUnknownOcppTags());
    }

    protected void setTags(Model model) {
        model.addAttribute("idTagList", ControllerHelper.idTagEnhancer(ocppTagRepository.getIdTags()));
    }

    // -------------------------------------------------------------------------
    // Back to Overview
    // -------------------------------------------------------------------------

    @RequestMapping(params = "backToOverview", value = ADD_SINGLE_PATH, method = RequestMethod.POST)
    public String addBackToOverview() {
        return toOverview();
    }

    @RequestMapping(params = "backToOverview", value = UPDATE_PATH, method = RequestMethod.POST)
    public String updateBackToOverview() {
        return toOverview();
    }

    protected String toOverview() {
        return "redirect:/manager/ocppTags";
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void add(OcppTagForm form) {
        ocppTagRepository.addOcppTag(form);
        ocppTagService.removeUnknown(form.getIdTag());
    }

    private void add(List<String> idTagList) {
        ocppTagRepository.addOcppTagList(idTagList);
        ocppTagService.removeUnknown(idTagList);
    }

}
