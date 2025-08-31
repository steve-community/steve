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
import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.service.ChargePointsService;
import de.rwth.idsg.steve.service.OcppTagsService;
import de.rwth.idsg.steve.service.ReservationsService;
import de.rwth.idsg.steve.service.TransactionStopService;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * One controller for transactions and reservations pages
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.08.2014
 */
@Controller
@RequestMapping(value = "/manager")
@RequiredArgsConstructor
public class ReservationsController {

    private final TransactionRepository transactionRepository;
    private final ReservationsService reservationsService;
    private final ChargePointsService chargePointsService;
    private final OcppTagsService ocppTagsService;
    private final TransactionStopService transactionStopService;

    private static final String PARAMS = "params";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String TRANSACTIONS_PATH = "/transactions";
    private static final String TRANSACTION_STOP_PATH = "/transactions/stop/{transactionPk}";
    private static final String TRANSACTIONS_DETAILS_PATH = "/transactions/details/{transactionPk}";
    private static final String TRANSACTIONS_QUERY_PATH = "/transactions/query";
    private static final String RESERVATIONS_PATH = "/reservations";
    private static final String RESERVATIONS_QUERY_PATH = "/reservations/query";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @GetMapping(value = TRANSACTIONS_PATH)
    public String getTransactions(Model model) {
        var params = new TransactionQueryForm();
        initList(model);

        model.addAttribute("transList", transactionRepository.getTransactions(params));
        model.addAttribute(PARAMS, params);
        return "data-man/transactions";
    }

    @PostMapping(value = TRANSACTION_STOP_PATH)
    public String stopTransaction(@PathVariable("transactionPk") int transactionPk) {
        transactionStopService.stop(transactionPk);
        return "redirect:/manager/transactions";
    }

    @GetMapping(value = TRANSACTIONS_DETAILS_PATH)
    public String getTransactionDetails(@PathVariable("transactionPk") int transactionPk, Model model) {
        var details = transactionRepository
                .getDetails(transactionPk)
                .orElseThrow(() ->
                        new SteveException.NotFound("Transaction with pk %d not found.".formatted(transactionPk)));
        model.addAttribute("details", details);
        return "data-man/transactionDetails";
    }

    @GetMapping(value = TRANSACTIONS_QUERY_PATH)
    public @Nullable String getTransactionsQuery(
            @Valid @ModelAttribute(PARAMS) TransactionQueryForm params,
            BindingResult result,
            Model model,
            HttpServletResponse response)
            throws IOException {
        if (result.hasErrors()) {
            initList(model);
            model.addAttribute(PARAMS, params);
            return "data-man/transactions";
        }

        if (params.isReturnCSV()) {
            var fileName = "transactions.csv";
            var headerKey = "Content-Disposition";
            var headerValue = String.format("attachment; filename=\"%s\"", fileName);
            response.setContentType("text/csv");
            response.setHeader(headerKey, headerValue);
            transactionRepository.writeTransactionsCSV(params, response.getWriter());
            return null;

        } else {
            model.addAttribute("transList", transactionRepository.getTransactions(params));
            initList(model);
            model.addAttribute(PARAMS, params);
            return "data-man/transactions";
        }
    }

    @GetMapping(value = RESERVATIONS_PATH)
    public String getReservations(Model model) {
        var params = new ReservationQueryForm();
        initResList(model);

        model.addAttribute("reservList", reservationsService.getReservations(params));
        model.addAttribute(PARAMS, params);
        return "data-man/reservations";
    }

    @GetMapping(value = RESERVATIONS_QUERY_PATH)
    public String getReservationsQuery(
            @Valid @ModelAttribute(PARAMS) ReservationQueryForm params, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            model.addAttribute("reservList", reservationsService.getReservations(params));
        }

        initResList(model);
        model.addAttribute(PARAMS, params);
        return "data-man/reservations";
    }

    private void initList(Model model) {
        model.addAttribute("cpList", chargePointsService.getChargeBoxIds());
        model.addAttribute("idTagList", ocppTagsService.getIdTags());
    }

    private void initResList(Model model) {
        initList(model);
        model.addAttribute("statusList", ReservationStatus.getValues());
    }
}
