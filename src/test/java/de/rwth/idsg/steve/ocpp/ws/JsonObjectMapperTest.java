/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.ocpp.ws;

import ocpp._2022._02.security.ExtendedTriggerMessage;
import ocpp.cs._2010._08.AuthorizeRequest;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.StatusNotificationRequest;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolationException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.12.2026
 */
public class JsonObjectMapperTest {

    private final ObjectMapper mapper = JsonObjectMapper.INSTANCE.getMapper();

    @Test
    public void testValidation_Ocpp12IdTagMissing() {
        var req = new ocpp.cs._2010._08.AuthorizeRequest()
            .withIdTag(null);

        var exception = assertThrows(ConstraintViolationException.class, () -> readBack(req, AuthorizeRequest.class));

        Assertions.assertEquals("idTag: must not be null", exception.getMessage());
    }

    @Test
    public void testValidation_Ocpp15IdTagTooLong() {
        var req = new ocpp.cs._2012._06.AuthorizeRequest()
            .withIdTag("1234567890:1234567890:abc");

        var exception = assertThrows(ConstraintViolationException.class, () -> readBack(req, ocpp.cs._2012._06.AuthorizeRequest.class));

        Assertions.assertEquals("idTag: size must be between 0 and 20", exception.getMessage());
    }

    @Test
    public void testValidation_Ocpp16MeterValueCascade() {
        var req = new MeterValuesRequest()
            .withConnectorId(1)
            .withMeterValue(new MeterValue().withTimestamp(DateTime.now()));

        var exception = assertThrows(DatabindException.class, () -> readBack(req, MeterValuesRequest.class));

        Assertions.assertEquals("sampledValue: must not be null", exception.getOriginalMessage());
    }

    @Test
    public void testValidation_Ocpp16Security() {
        var req = new ExtendedTriggerMessage()
            .withRequestedMessage(null);

        var exception = assertThrows(ConstraintViolationException.class, () -> readBack(req, ExtendedTriggerMessage.class));

        Assertions.assertEquals("requestedMessage: must not be null", exception.getMessage());
    }

    @Test
    public void testValidation_Ocpp16TypoInEnum() {
        var req = new StatusNotificationRequest()
            .withStatus(ChargePointStatus.FAULTED)
            .withTimestamp(DateTime.now())
            .withConnectorId(1);

        var exception = assertThrows(
            DatabindException.class,
            () -> readBack(
                req,
                StatusNotificationRequest.class,
                s -> s.replace("Faulted", "Faultd") // lets make a typo
            )
        );

        Assertions.assertEquals("Cannot deserialize value of type `ocpp.cs._2015._10.ChargePointStatus` from String \"Faultd\": not one of the values accepted for Enum class: [Unavailable, Charging, Preparing, Reserved, Available, SuspendedEV, SuspendedEVSE, Finishing, Faulted]", exception.getOriginalMessage());
    }

    private <T> T readBack(T req, Class<T> clazz) {
        return readBack(req, clazz, Function.identity());
    }

    private <T> T readBack(T req, Class<T> clazz, Function<String, String> rawStringModifier) {
        String str = mapper.writeValueAsString(req);
        String modified = rawStringModifier.apply(str);
        return mapper.readValue(modified, clazz);
    }
}
