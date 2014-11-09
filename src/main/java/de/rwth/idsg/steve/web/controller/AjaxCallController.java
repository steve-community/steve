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
@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
public class AjaxCallController {

    @Autowired private ChargePointRepository chargePointRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private ReservationRepository reservationRepository;

    @RequestMapping(value = "/ajax/getCPDetails")
    public ChargePoint getCPDetails(@RequestParam String chargeBoxId) {
        return chargePointRepository.getChargePointDetails(chargeBoxId);
    }

    @RequestMapping(value = "/ajax/getConnectorIds")
    public List<Integer> getConnectorIds(@RequestParam String chargeBoxId) {
        return chargePointRepository.getConnectorIds(chargeBoxId);
    }

    @RequestMapping(value = "/ajax/getTransactionIds")
    public List<Integer> getTransactionIds(@RequestParam String chargeBoxId) {
        return transactionRepository.getActiveTransactionIds(chargeBoxId);
    }

    @RequestMapping(value = "/ajax/getReservationIds")
    public List<Integer> getReservationIds(@RequestParam String chargeBoxId) {
        return reservationRepository.getExistingReservationIds(chargeBoxId);
    }

}