package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.repository.TaskStore;
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
public class TaskController {

    @Autowired private TaskStore taskStore;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String TASK_ID_PATH = "/{taskId}";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String getOverview(Model model) {
        model.addAttribute("taskList", taskStore.getOverview());
        return "tasks";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String clearFinished(Model model) {
        taskStore.clearFinished();
        return getOverview(model);
    }

    @RequestMapping(value = TASK_ID_PATH, method = RequestMethod.GET)
    public String getTaskDetails(@PathVariable("taskId") Integer taskId, Model model) {
        CommunicationTask r = taskStore.get(taskId);
        model.addAttribute("task", r);
        return "taskResult";
    }
}
