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
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.utils.OcppJsonChargePoint;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import lombok.extern.slf4j.Slf4j;
import ocpp._2022._02.security.ExtendedTriggerMessage;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.HeartbeatResponse;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.RegistrationStatus;
import org.eclipse.jetty.websocket.api.exceptions.UpgradeException;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static de.rwth.idsg.steve.ocpp.ws.data.ErrorCode.PropertyConstraintViolation;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.03.2018
 */
@Slf4j
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment =  WebEnvironment.DEFINED_PORT)
public class ApplicationJsonTest {

    private static final String PATH = "ws://localhost:8080/steve/websocket/CentralSystemService/";

    private static final String REGISTERED_CHARGE_BOX_ID = __DatabasePreparer__.getRegisteredChargeBoxId();
    private static final String REGISTERED_OCPP_TAG =  __DatabasePreparer__.getRegisteredOcppTag();

    @Autowired
    private DSLContext dslContext;

    private __DatabasePreparer__ databasePreparer;

    @BeforeEach
    public void setup() {
        databasePreparer = new __DatabasePreparer__(dslContext);
        databasePreparer.prepare();
    }

    @AfterEach
    public void teardown() {
        databasePreparer.cleanUp();
    }

    @Test
    public void testOcpp12() {
        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_12, REGISTERED_CHARGE_BOX_ID, PATH).start();

        ocpp.cs._2010._08.BootNotificationRequest boot = new ocpp.cs._2010._08.BootNotificationRequest()
            .withChargePointVendor(getRandomString())
            .withChargePointModel(getRandomString());
        var bootResponse = chargePoint.send(boot, ocpp.cs._2010._08.BootNotificationResponse.class);
        Assertions.assertEquals(ocpp.cs._2010._08.RegistrationStatus.ACCEPTED, bootResponse.getStatus());

        ocpp.cs._2010._08.AuthorizeRequest auth = new ocpp.cs._2010._08.AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authResponse = chargePoint.send(auth, ocpp.cs._2010._08.AuthorizeResponse.class);
        Assertions.assertEquals(ocpp.cs._2010._08.AuthorizationStatus.ACCEPTED, authResponse.getIdTagInfo().getStatus());

        chargePoint.close();
    }

    @Test
    public void testOcpp15() {
        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_15, REGISTERED_CHARGE_BOX_ID, PATH).start();

        ocpp.cs._2012._06.BootNotificationRequest boot = new ocpp.cs._2012._06.BootNotificationRequest()
            .withChargePointVendor(getRandomString())
            .withChargePointModel(getRandomString());

        var bootResponse = chargePoint.send(boot, ocpp.cs._2012._06.BootNotificationResponse.class);
        Assertions.assertEquals(ocpp.cs._2012._06.RegistrationStatus.ACCEPTED, bootResponse.getStatus());

        ocpp.cs._2012._06.AuthorizeRequest auth = new ocpp.cs._2012._06.AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);

        var authResponse = chargePoint.send(auth, ocpp.cs._2012._06.AuthorizeResponse.class);
        Assertions.assertEquals(ocpp.cs._2012._06.AuthorizationStatus.ACCEPTED, authResponse.getIdTagInfo().getStatus());

        chargePoint.close();
    }

    @Test
    public void testOcpp16() {
        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        BootNotificationRequest boot = new BootNotificationRequest()
                .withChargePointVendor(getRandomString())
                .withChargePointModel(getRandomString());

        var bootResponse = chargePoint.send(boot, BootNotificationResponse.class);
        Assertions.assertEquals(RegistrationStatus.ACCEPTED, bootResponse.getStatus());

        AuthorizeRequest auth = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);

        var authResponse = chargePoint.send(auth, AuthorizeResponse.class);
        Assertions.assertEquals(AuthorizationStatus.ACCEPTED, authResponse.getIdTagInfo().getStatus());

        chargePoint.close();
    }

    @Test
    public void testWithMissingVersion() {
        RuntimeException e = Assertions.assertThrows(RuntimeException.class, () -> {
            OcppJsonChargePoint chargePoint = new OcppJsonChargePoint((List<String>) null, REGISTERED_CHARGE_BOX_ID, PATH);
            chargePoint.start();
        });

        Assertions.assertTrue(e.getCause().getCause() instanceof UpgradeException);

        UpgradeException actualCause = (UpgradeException) e.getCause().getCause();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), actualCause.getResponseStatusCode());
    }

    @Test
    public void testWithWrongVersion() {
        RuntimeException e = Assertions.assertThrows(RuntimeException.class, () -> {
            OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(List.of("ocpp1234"), REGISTERED_CHARGE_BOX_ID, PATH);
            chargePoint.start();
        });

        Assertions.assertTrue(e.getCause().getCause() instanceof UpgradeException);

        UpgradeException actualCause = (UpgradeException) e.getCause().getCause();

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actualCause.getResponseStatusCode());
    }

    @Test
    public void tesWithUnauthorizedStation() {
        RuntimeException e = Assertions.assertThrows(RuntimeException.class, () -> {
            OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, "unauth1234", PATH);
            chargePoint.start();
        });

        Assertions.assertTrue(e.getCause().getCause() instanceof UpgradeException);

        UpgradeException actualCause = (UpgradeException) e.getCause().getCause();

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actualCause.getResponseStatusCode());
    }

    /**
     * https://github.com/steve-community/steve/issues/1109
     */
    @Test
    public void testWithNullPayload() {
        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var response = chargePoint.send(null, "Heartbeat", HeartbeatResponse.class);
        Assertions.assertNotNull(response.getCurrentTime());

        chargePoint.close();
    }

    @Test
    public void testValidation_Ocpp12IdTagMissing() {
        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_12, REGISTERED_CHARGE_BOX_ID, PATH).start();

        ocpp.cs._2010._08.AuthorizeRequest auth = new ocpp.cs._2010._08.AuthorizeRequest().withIdTag(null);
        var error = chargePoint.send(auth);
        Assertions.assertEquals(PropertyConstraintViolation, error.getErrorCode());

        chargePoint.close();
    }

    @Test
    public void testValidation_Ocpp15IdTagTooLong() {
        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_15, REGISTERED_CHARGE_BOX_ID, PATH).start();

        ocpp.cs._2012._06.AuthorizeRequest auth = new ocpp.cs._2012._06.AuthorizeRequest().withIdTag("1234567890:1234567890:abc");
        var error = chargePoint.send(auth);
        Assertions.assertEquals(PropertyConstraintViolation, error.getErrorCode());

        chargePoint.close();
    }

    @Test
    public void testValidation_Ocpp16MeterValueCascade() {
        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        MeterValuesRequest req = new MeterValuesRequest()
            .withConnectorId(1)
            .withMeterValue(new MeterValue().withTimestamp(DateTime.now()));

        var error = chargePoint.send(req);
        Assertions.assertEquals(PropertyConstraintViolation, error.getErrorCode());

        chargePoint.close();
    }

    @Test
    public void testValidation_Ocpp16Security() {
        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var req = new ExtendedTriggerMessage()
            .withRequestedMessage(null);

        var error = chargePoint.send(req);
        Assertions.assertEquals(PropertyConstraintViolation, error.getErrorCode());

        chargePoint.close();
    }

}
