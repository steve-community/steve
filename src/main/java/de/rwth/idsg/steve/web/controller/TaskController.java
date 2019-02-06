package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.ocpp.task.GetCompositeScheduleTask;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.repository.TaskStore;
import ocpp.cp._2015._10.GetCompositeScheduleResponse;
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
    private static final String TASK_DETAILS_PATH = TASK_ID_PATH + "/details/{chargeBoxId}";

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
        model.addAttribute("taskId", taskId);
        model.addAttribute("task", r);
        return "taskResult";
    }

    @RequestMapping(value = TASK_DETAILS_PATH, method = RequestMethod.GET)
    public String getDetailsForChargeBox(@PathVariable("taskId") Integer taskId,
                                         @PathVariable("chargeBoxId") String chargeBoxId,
                                         Model model) {

        CommunicationTask r = taskStore.get(taskId);

        if (r instanceof GetCompositeScheduleTask) {
            return processForGetCompositeScheduleTask((GetCompositeScheduleTask) r, chargeBoxId, model);
        } else if (r instanceof GetConfigurationTask) {
            return processForGetConfigurationTask((GetConfigurationTask) r, chargeBoxId, model);
        } else {
            throw new SteveException("Task not found");
        }
    }

    private String processForGetCompositeScheduleTask(GetCompositeScheduleTask k, String chargeBoxId, Model model) {
        RequestResult result = extractResult(k, chargeBoxId);
        GetCompositeScheduleResponse response = result.getDetails();

        model.addAttribute("chargeBoxId", chargeBoxId);
        model.addAttribute("response", response);
        return "op16/GetCompositeScheduleResponse";
    }

    private String processForGetConfigurationTask(GetConfigurationTask k, String chargeBoxId, Model model) {
        RequestResult result = extractResult(k, chargeBoxId);
        GetConfigurationTask.ResponseWrapper response = result.getDetails();

        model.addAttribute("chargeBoxId", chargeBoxId);
        model.addAttribute("response", response);
        return "GetConfigurationResponse";
    }

    private static RequestResult extractResult(CommunicationTask<?, ?> task, String chargeBoxId) {
        RequestResult result = task.getResultMap().get(chargeBoxId);
        if (result == null) {
            throw new SteveException("Result not found");
        }
        return result;
    }
}
