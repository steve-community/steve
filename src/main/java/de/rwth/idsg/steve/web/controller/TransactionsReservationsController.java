package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * One controller for transactions and reservations pages
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Controller
@RequestMapping(value = "/manager", method = RequestMethod.GET)
public class TransactionsReservationsController {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private ChargePointRepository chargePointRepository;
    @Autowired private OcppTagRepository ocppTagRepository;

    private static final String PARAMS = "params";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String TRANSACTIONS_PATH = "/transactions";
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

        model.addAttribute("transList", transactionRepository.getTransactions(params));
        model.addAttribute(PARAMS, params);
        return "data-man/transactions";
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
            transactionRepository.writeTransactionsCSV(params, response.getWriter());
            return null;

        } else {
            model.addAttribute("transList", transactionRepository.getTransactions(params));
            initList(model);
            model.addAttribute(PARAMS, params);
            return "data-man/transactions";
        }
    }

    @RequestMapping(value = RESERVATIONS_PATH)
    public String getReservations(Model model) {
        ReservationQueryForm params = new ReservationQueryForm();
        initResList(model);

        model.addAttribute("reservList", reservationRepository.getReservations(params));
        model.addAttribute(PARAMS, params);
        return "data-man/reservations";
    }

    @RequestMapping(value = RESERVATIONS_QUERY_PATH)
    public String getReservationsQuery(@Valid @ModelAttribute(PARAMS) ReservationQueryForm params,
                                      BindingResult result, Model model) throws IOException {
        if (!result.hasErrors()) {
            model.addAttribute("reservList", reservationRepository.getReservations(params));
        }

        initResList(model);
        model.addAttribute(PARAMS, params);
        return "data-man/reservations";
    }

    private void initList(Model model) {
        model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());
        model.addAttribute("idTagList", ocppTagRepository.getIdTags());
    }

    private void initResList(Model model) {
        initList(model);
        model.addAttribute("statusList", ReservationStatus.getValues());
    }

}
