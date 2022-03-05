/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.RegistrationStatus;
import org.eclipse.jetty.websocket.api.exceptions.UpgradeException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static de.rwth.idsg.steve.utils.Helpers.getRandomString;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.03.2018
 */
@Slf4j
public class ApplicationJsonTest {

    private static final String PATH = "ws://localhost:8080/steve/websocket/CentralSystemService/";

    private static final String REGISTERED_CHARGE_BOX_ID = __DatabasePreparer__.getRegisteredChargeBoxId();
    private static final String REGISTERED_OCPP_TAG =  __DatabasePreparer__.getRegisteredOcppTag();

    private static Application app;

    @BeforeAll
    public static void init() throws Exception {
        Assertions.assertEquals(ApplicationProfile.TEST, SteveConfiguration.CONFIG.getProfile());
        __DatabasePreparer__.prepare();

        app = new Application();
        app.start();
    }

    @AfterAll
    public static void destroy() throws Exception {
        if (app != null) {
            app.stop();
        }
        __DatabasePreparer__.cleanUp();
    }

    @Test
    public void testOcpp12() {
        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_12, REGISTERED_CHARGE_BOX_ID, PATH);
        chargePoint.start();

        ocpp.cs._2010._08.BootNotificationRequest boot = new ocpp.cs._2010._08.BootNotificationRequest()
            .withChargePointVendor(getRandomString())
            .withChargePointModel(getRandomString());

        chargePoint.prepare(boot, ocpp.cs._2010._08.BootNotificationResponse.class,
            bootResponse -> Assertions.assertEquals(ocpp.cs._2010._08.RegistrationStatus.ACCEPTED, bootResponse.getStatus()),
            error -> Assertions.fail()
        );

        ocpp.cs._2010._08.AuthorizeRequest auth = new ocpp.cs._2010._08.AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);

        chargePoint.prepare(auth, ocpp.cs._2010._08.AuthorizeResponse.class,
            authResponse -> Assertions.assertEquals(ocpp.cs._2010._08.AuthorizationStatus.ACCEPTED, authResponse.getIdTagInfo().getStatus()),
            error -> Assertions.fail()
        );

        chargePoint.processAndClose();
    }

    @Test
    public void testOcpp15() {
        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_15, REGISTERED_CHARGE_BOX_ID, PATH);
        chargePoint.start();

        ocpp.cs._2012._06.BootNotificationRequest boot = new ocpp.cs._2012._06.BootNotificationRequest()
            .withChargePointVendor(getRandomString())
            .withChargePointModel(getRandomString());

        chargePoint.prepare(boot, ocpp.cs._2012._06.BootNotificationResponse.class,
            bootResponse -> Assertions.assertEquals(ocpp.cs._2012._06.RegistrationStatus.ACCEPTED, bootResponse.getStatus()),
            error -> Assertions.fail()
        );

        ocpp.cs._2012._06.AuthorizeRequest auth = new ocpp.cs._2012._06.AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);

        chargePoint.prepare(auth, ocpp.cs._2012._06.AuthorizeResponse.class,
            authResponse -> Assertions.assertEquals(ocpp.cs._2012._06.AuthorizationStatus.ACCEPTED, authResponse.getIdTagInfo().getStatus()),
            error -> Assertions.fail()
        );

        chargePoint.processAndClose();
    }

    @Test
    public void testOcpp16() {
        OcppJsonChargePoint chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH);
        chargePoint.start();

        BootNotificationRequest boot = new BootNotificationRequest()
                .withChargePointVendor(getRandomString())
                .withChargePointModel(getRandomString());

        chargePoint.prepare(boot, BootNotificationResponse.class,
                bootResponse -> Assertions.assertEquals(RegistrationStatus.ACCEPTED, bootResponse.getStatus()),
                error -> Assertions.fail()
        );

        AuthorizeRequest auth = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);

        chargePoint.prepare(auth, AuthorizeResponse.class,
                authResponse -> Assertions.assertEquals(AuthorizationStatus.ACCEPTED, authResponse.getIdTagInfo().getStatus()),
                error -> Assertions.fail()
        );

        chargePoint.processAndClose();
    }

    @Test
    public void testWithMissingVersion() {
        RuntimeException e = Assertions.assertThrows(RuntimeException.class, () -> {
            OcppJsonChargePoint chargePoint = new OcppJsonChargePoint((String) null, REGISTERED_CHARGE_BOX_ID, PATH);
            chargePoint.start();
        });

        Assertions.assertTrue(e.getCause().getCause() instanceof UpgradeException);

        UpgradeException actualCause = (UpgradeException) e.getCause().getCause();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), actualCause.getResponseStatusCode());
    }

    @Test
    public void testWithWrongVersion() {
        RuntimeException e = Assertions.assertThrows(RuntimeException.class, () -> {
            OcppJsonChargePoint chargePoint = new OcppJsonChargePoint("ocpp1234", REGISTERED_CHARGE_BOX_ID, PATH);
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

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), actualCause.getResponseStatusCode());
    }

}
