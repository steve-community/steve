/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.parkl.ocpp.service.cs.ChargePointService;
import net.parkl.ocpp.service.cs.ReservationService;
import net.parkl.ocpp.service.cs.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.08.2014
 */
@Controller
@ResponseBody
@RequestMapping(
        value = "/manager/ajax/{chargeBoxId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AjaxCallController {
	private static final Logger log=LoggerFactory.getLogger(AjaxCallController.class);

    @Autowired private ChargePointService chargePointService;
    @Autowired private TransactionService transactionService;
    @Autowired private ReservationService reservationService;

    private ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String CONNECTOR_IDS_PATH      = "/connectorIds";
    private static final String TRANSACTION_IDS_PATH    = "/transactionIds";
    private static final String RESERVATION_IDS_PATH    = "/reservationIds";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(value = CONNECTOR_IDS_PATH)
    public void getConnectorIds(@PathVariable("chargeBoxId") String chargeBoxId,
                                HttpServletResponse response) throws IOException {
        String s = serializeArray(chargePointService.getNonZeroConnectorIds(chargeBoxId));
        writeOutput(response, s);
    }

    @RequestMapping(value = TRANSACTION_IDS_PATH)
    public void getTransactionIds(@PathVariable("chargeBoxId") String chargeBoxId,
                                  HttpServletResponse response) throws IOException {
        String s = serializeArray(transactionService.getActiveTransactionIds(chargeBoxId));
        writeOutput(response, s);
    }

    @RequestMapping(value = RESERVATION_IDS_PATH)
    public void getReservationIds(@PathVariable("chargeBoxId") String chargeBoxId,
                                  HttpServletResponse response) throws IOException {
        String s = serializeArray(reservationService.getActiveReservationIds(chargeBoxId));
        writeOutput(response, s);
    }

    private String serializeArray(List<?> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            // As fallback return empty array, do not let the frontend hang
            log.error("Error occurred during serialization of response. Returning empty array instead!", e);
            return "[]";
        }
    }

    /**
     * We want to handle this JSON conversion locally, and do not want to register an application-wide
     * HttpMessageConverter just for this little class. Otherwise, it might have unwanted side effects due to
     * different serialization/deserialization needs of different APIs.
     *
     * That's why we are directly accessing the low-level HttpServletResponse and manually writing to output.
     */
    private void writeOutput(HttpServletResponse response, String str) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(str);
    }

}
