package de.rwth.idsg.steve.web.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Slf4j
@Controller
@ResponseBody
@RequestMapping(
        value = "/manager/ajax/{chargeBoxId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AjaxCallController {

    @Autowired private ChargePointRepository chargePointRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private ReservationRepository reservationRepository;

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
        String s = serializeArray(chargePointRepository.getNonZeroConnectorIds(chargeBoxId));
        writeOutput(response, s);
    }

    @RequestMapping(value = TRANSACTION_IDS_PATH)
    public void getTransactionIds(@PathVariable("chargeBoxId") String chargeBoxId,
                                  HttpServletResponse response) throws IOException {
        String s = serializeArray(transactionRepository.getActiveTransactionIds(chargeBoxId));
        writeOutput(response, s);
    }

    @RequestMapping(value = RESERVATION_IDS_PATH)
    public void getReservationIds(@PathVariable("chargeBoxId") String chargeBoxId,
                                  HttpServletResponse response) throws IOException {
        String s = serializeArray(reservationRepository.getActiveReservationIds(chargeBoxId));
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
