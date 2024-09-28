/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
 * All Rights Reserved.
 *
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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

import de.rwth.idsg.steve.config.WebEnvironment;
import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.service.TransactionStopService;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import net.parkl.ocpp.service.cs.ChargePointService;
import net.parkl.ocpp.service.cs.ReservationService;
import net.parkl.ocpp.service.cs.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;


/**
 * One controller for transactions and reservations pages
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.08.2014
 */
@Controller
@RequestMapping(value = "/manager", method = RequestMethod.GET)
public class TransactionsReservationsController {

    @Autowired private TransactionService transactionService;
    @Autowired private ReservationService reservationService;
    @Autowired private ChargePointService chargePointService;
    @Autowired private OcppTagService ocppTagService;
    @Autowired private TransactionStopService transactionStopService;

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

    @RequestMapping(value = TRANSACTIONS_PATH)
    public String getTransactions(Model model) {
        TransactionQueryForm params = new TransactionQueryForm();
        initList(model);

        model.addAttribute("transList", transactionService.getTransactions(params));
        model.addAttribute(PARAMS, params);
        return "data-man/transactions";
    }

    @RequestMapping(value = TRANSACTION_STOP_PATH, method = RequestMethod.POST)
    public String stopTransaction(@PathVariable("transactionPk") int transactionPk) {
        transactionStopService.stop(transactionPk);
        return "redirect:"+ WebEnvironment.getContextRoot()+"/manager/transactions";
    }

    @RequestMapping(value = TRANSACTIONS_DETAILS_PATH)
    public String getTransactionDetails(@PathVariable("transactionPk") int transactionPk, Model model) {
        model.addAttribute("details", transactionService.getDetails(transactionPk));
        return "data-man/transactionDetails";
    }

    @RequestMapping(value = TRANSACTIONS_QUERY_PATH)
    public String getTransactionsQuery(@Valid @ModelAttribute(PARAMS) TransactionQueryForm params,
                                       BindingResult result, Model model,
                                       HttpServletResponse response) throws IOException {
        if (result.hasErrors()) {
            initList(model);
            model.addAttribute(PARAMS, params);
            return "data-man/transactions";
        }

        if (params.isReturnCSV()) {
            String fileName = "transactions.csv";
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", fileName);
            response.setContentType("text/csv");
            response.setHeader(headerKey, headerValue);
            transactionService.writeTransactionsCSV(params, response.getWriter());
            return null;

        } else {
            model.addAttribute("transList", transactionService.getTransactions(params));
            initList(model);
            model.addAttribute(PARAMS, params);
            return "data-man/transactions";
        }
    }

    @RequestMapping(value = RESERVATIONS_PATH)
    public String getReservations(Model model) {
        ReservationQueryForm params = new ReservationQueryForm();
        initResList(model);

        model.addAttribute("reservList", reservationService.getReservations(params));
        model.addAttribute(PARAMS, params);
        return "data-man/reservations";
    }

    @RequestMapping(value = RESERVATIONS_QUERY_PATH)
    public String getReservationsQuery(@Valid @ModelAttribute(PARAMS) ReservationQueryForm params,
                                      BindingResult result, Model model) throws IOException {
        if (!result.hasErrors()) {
            model.addAttribute("reservList", reservationService.getReservations(params));
        }

        initResList(model);
        model.addAttribute(PARAMS, params);
        return "data-man/reservations";
    }

    private void initList(Model model) {
        model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());
        model.addAttribute("idTagList", ocppTagService.getIdTags());
    }

    private void initResList(Model model) {
        initList(model);
        model.addAttribute("statusList", ReservationStatus.getValues());
    }

}
