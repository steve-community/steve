package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Controller
@ResponseBody
@RequestMapping(value = "/ajax", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
public class AjaxCallController {

    @Autowired private ChargePointRepository chargePointRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private ReservationRepository reservationRepository;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String CP_DETAILS_PATH = "/getCPDetails";
    private static final String CONNECTOR_IDS_PATH = "/getConnectorIds";
    private static final String TRANSACTION_IDS_PATH = "/getTransactionIds";
    private static final String RESERVATION_IDS_PATH = "/getReservationIds";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(value = CP_DETAILS_PATH)
    public ChargePoint getCPDetails(@RequestParam String chargeBoxId) {
        return chargePointRepository.getChargePointDetails(chargeBoxId);
    }

    @RequestMapping(value = CONNECTOR_IDS_PATH)
    public List<Integer> getConnectorIds(@RequestParam String chargeBoxId) {
        return chargePointRepository.getConnectorIds(chargeBoxId);
    }

    @RequestMapping(value = TRANSACTION_IDS_PATH)
    public List<Integer> getTransactionIds(@RequestParam String chargeBoxId) {
        return transactionRepository.getActiveTransactionIds(chargeBoxId);
    }

    @RequestMapping(value = RESERVATION_IDS_PATH)
    public List<Integer> getReservationIds(@RequestParam String chargeBoxId) {
        return reservationRepository.getExistingReservationIds(chargeBoxId);
    }

}