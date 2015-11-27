package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import jooq.steve.db.tables.records.OcppTagRecord;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 26.11.2015
 */
@Controller
@RequestMapping(value = "/manager/ocppTags")
public class OcppTagsController {

    @Autowired private OcppTagRepository ocppTagRepository;

    private static final String PARAMS = "params";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String QUERY_PATH = "/query";

    private static final String DETAILS_PATH = "/details/{idTag}";
    private static final String DELETE_PATH = "/delete/{idTag}";
    private static final String UPDATE_PATH = "/update";
    private static final String ADD_PATH = "/add";


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
    public String getDetails(@PathVariable("idTag") String idTag, Model model) {
        OcppTagRecord record = ocppTagRepository.getRecord(idTag);

        OcppTagForm form = new OcppTagForm();
        form.setIdTag(record.getIdTag());

        DateTime expiryDate = record.getExpiryDate();
        if (expiryDate != null) {
            form.setExpiration(expiryDate.toLocalDateTime());
        }

        form.setBlocked(record.getBlocked());
        form.setNote(record.getNote());

        String parentIdTag = record.getParentIdTag();
        if (parentIdTag == null) {
            parentIdTag = ControllerHelper.EMPTY_OPTION;
        }
        form.setParentIdTag(parentIdTag);

        model.addAttribute("inTransaction", record.getInTransaction());
        model.addAttribute("ocppTagForm", form);
        setTags(model);
        return "data-man/ocppTagDetails";
    }

    @RequestMapping(value = ADD_PATH, method = RequestMethod.GET)
    public String addGet(Model model) {
        setTags(model);
        model.addAttribute("ocppTagForm", new OcppTagForm());
        return "data-man/ocppTagAdd";
    }

    @RequestMapping(params = "add", value = ADD_PATH, method = RequestMethod.POST)
    public String addPost(@Valid @ModelAttribute("ocppTagForm") OcppTagForm ocppTagForm,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            setTags(model);
            return "data-man/ocppTagAdd";
        }

        ocppTagRepository.addOcppTag(ocppTagForm);
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
    public String delete(@PathVariable("idTag") String idTag) {
        ocppTagRepository.deleteOcppTag(idTag);
        return toOverview();
    }

    private void initList(Model model, OcppTagQueryForm params) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("idTagList", ocppTagRepository.getIdTags());
        model.addAttribute("parentIdTagList", ocppTagRepository.getParentIdTags());
        model.addAttribute("ocppTagList", ocppTagRepository.getTags(params));
    }

    private void setTags(Model model) {
        model.addAttribute("idTagList", ControllerHelper.idTagEnhancer(ocppTagRepository.getIdTags()));
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
        return "redirect:/manager/ocppTags";
    }
}
