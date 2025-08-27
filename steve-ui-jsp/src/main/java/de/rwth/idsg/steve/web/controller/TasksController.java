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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.ocpp.task.GetCompositeScheduleTask;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.repository.TaskStore;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2015._10.GetCompositeScheduleResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.12.2014
 */
@Controller
@RequestMapping(value = "/manager/operations/tasks")
@RequiredArgsConstructor
public class TasksController {

    private final TaskStore taskStore;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String TASK_ID_PATH = "/{taskId}";
    private static final String TASK_DETAILS_PATH = TASK_ID_PATH + "/details/{chargeBoxId}/";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @GetMapping
    public String getOverview(Model model) {
        model.addAttribute("taskList", taskStore.getOverview());
        return "tasks";
    }

    @PostMapping(params = "finished")
    public String clearFinished(Model model) {
        taskStore.clearFinished();
        return "redirect:/manager/operations/tasks";
    }

    @PostMapping(params = "unfinished")
    public String clearUnfinished(Model model) {
        taskStore.clearUnfinished();
        return "redirect:/manager/operations/tasks";
    }

    @GetMapping(value = TASK_ID_PATH)
    public String getTaskDetails(@PathVariable("taskId") Integer taskId, Model model) {
        var task = taskStore.get(taskId);
        model.addAttribute("taskId", taskId);
        model.addAttribute("task", task);
        return "taskResult";
    }

    @GetMapping(value = TASK_DETAILS_PATH)
    public String getDetailsForChargeBox(
            @PathVariable("taskId") Integer taskId, @PathVariable("chargeBoxId") String chargeBoxId, Model model) {

        var task = taskStore.get(taskId);

        if (task instanceof GetCompositeScheduleTask) {
            return processForGetCompositeScheduleTask((GetCompositeScheduleTask) task, chargeBoxId, model);
        }
        if (task instanceof GetConfigurationTask) {
            return processForGetConfigurationTask((GetConfigurationTask) task, chargeBoxId, model);
        }
        throw new SteveException("Task not found");
    }

    private static String processForGetCompositeScheduleTask(
            GetCompositeScheduleTask task, String chargeBoxId, Model model) {
        RequestResult result = extractResult(task, chargeBoxId);
        GetCompositeScheduleResponse response = result.getDetails();

        model.addAttribute("chargeBoxId", chargeBoxId);
        model.addAttribute("response", response);
        return "op16/GetCompositeScheduleResponse";
    }

    private static String processForGetConfigurationTask(GetConfigurationTask k, String chargeBoxId, Model model) {
        RequestResult result = extractResult(k, chargeBoxId);
        GetConfigurationTask.ResponseWrapper response = result.getDetails();

        model.addAttribute("chargeBoxId", chargeBoxId);
        model.addAttribute("response", response);
        return "GetConfigurationResponse";
    }

    private static RequestResult extractResult(CommunicationTask<?, ?> task, String chargeBoxId) {
        RequestResult result = task.getResultMap().get(chargeBoxId);
        if (result == null) {
            throw new SteveException("Result not found for chargeBoxId '" + chargeBoxId + "'");
        }
        return result;
    }
}
