package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.repository.RequestTaskStore;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
@Controller
@RequestMapping(value = "/manager/operations/tasks")
public class RequestTaskController {

    @Autowired private RequestTaskStore requestTaskStore;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String TASK_ID_PATH = "/{taskId}";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String getOverview(Model model) {
        model.addAttribute("taskList", requestTaskStore.getOverview());
        return "tasks";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String clearFinished(Model model) {
        requestTaskStore.clearFinished();
        return getOverview(model);
    }

    @RequestMapping(value = TASK_ID_PATH, method = RequestMethod.GET)
    public String getTaskDetails(@PathVariable("taskId") Integer taskId, Model model) {
        CommunicationTask r = requestTaskStore.get(taskId);
        model.addAttribute("task", r);
        return "taskResult";
    }
}
