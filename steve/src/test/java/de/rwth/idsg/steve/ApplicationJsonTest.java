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
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.utils.OcppJsonChargePoint;
import de.rwth.idsg.steve.utils.SteveConfigurationReader;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.HeartbeatResponse;
import ocpp.cs._2015._10.RegistrationStatus;
import org.eclipse.jetty.websocket.core.exception.UpgradeException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.then;

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
        var config = SteveConfigurationReader.readSteveConfiguration("main.properties");
        assertThat(config.getProfile()).isEqualTo(ApplicationProfile.TEST);
        __DatabasePreparer__.prepare(config);

        app = new Application(config);
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
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_12, REGISTERED_CHARGE_BOX_ID, PATH);
        chargePoint.start();

        var boot = new ocpp.cs._2010._08.BootNotificationRequest()
            .withChargePointVendor(getRandomString())
            .withChargePointModel(getRandomString());

        chargePoint.prepare(boot, ocpp.cs._2010._08.BootNotificationResponse.class,
            bootResponse -> assertThat(bootResponse.getStatus()).isEqualTo(ocpp.cs._2010._08.RegistrationStatus.ACCEPTED),
            error -> fail()
        );

        var auth = new ocpp.cs._2010._08.AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);

        chargePoint.prepare(auth, ocpp.cs._2010._08.AuthorizeResponse.class,
            authResponse -> assertThat(authResponse.getIdTagInfo().getStatus()).isEqualTo(ocpp.cs._2010._08.AuthorizationStatus.ACCEPTED),
            error -> fail()
        );

        chargePoint.processAndClose();
    }

    @Test
    public void testOcpp15() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_15, REGISTERED_CHARGE_BOX_ID, PATH);
        chargePoint.start();

        var boot = new ocpp.cs._2012._06.BootNotificationRequest()
            .withChargePointVendor(getRandomString())
            .withChargePointModel(getRandomString());

        chargePoint.prepare(boot, ocpp.cs._2012._06.BootNotificationResponse.class,
            bootResponse -> assertThat(bootResponse.getStatus()).isEqualTo(ocpp.cs._2012._06.RegistrationStatus.ACCEPTED),
            error -> fail()
        );

        var auth = new ocpp.cs._2012._06.AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);

        chargePoint.prepare(auth, ocpp.cs._2012._06.AuthorizeResponse.class,
            authResponse -> assertThat(authResponse.getIdTagInfo().getStatus()).isEqualTo(ocpp.cs._2012._06.AuthorizationStatus.ACCEPTED),
            error -> fail()
        );

        chargePoint.processAndClose();
    }

    @Test
    public void testOcpp16() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH);
        chargePoint.start();

        var boot = new BootNotificationRequest()
                .withChargePointVendor(getRandomString())
                .withChargePointModel(getRandomString());

        chargePoint.prepare(boot, BootNotificationResponse.class,
                bootResponse -> assertThat(bootResponse.getStatus()).isEqualTo(RegistrationStatus.ACCEPTED),
                error -> fail()
        );

        var auth = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);

        chargePoint.prepare(auth, AuthorizeResponse.class,
                authResponse -> assertThat(authResponse.getIdTagInfo().getStatus()).isEqualTo(AuthorizationStatus.ACCEPTED),
                error -> fail()
        );

        chargePoint.processAndClose();
    }

    @Test
    public void testWithMissingVersion() {
        var chargePoint = new OcppJsonChargePoint((String) null, REGISTERED_CHARGE_BOX_ID, PATH);
        var thrown = catchThrowable(chargePoint::start);
        then(thrown)
                .isInstanceOf(RuntimeException.class)
                .hasRootCauseInstanceOf(UpgradeException.class)
                .rootCause().satisfies(c -> {;
                    var ue = (UpgradeException) c;
                    assertThat(ue.getResponseStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
    }

    @Test
    public void testWithWrongVersion() {
        var chargePoint = new OcppJsonChargePoint("ocpp1234", REGISTERED_CHARGE_BOX_ID, PATH);
        var thrown = catchThrowable(chargePoint::start);
        then(thrown)
                .isInstanceOf(RuntimeException.class)
                .hasRootCauseInstanceOf(UpgradeException.class)
                .rootCause().satisfies(c -> {
                    var ue = (UpgradeException) c;
                    assertThat(ue.getResponseStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    @Test
    public void tesWithUnauthorizedStation() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, "unauth1234", PATH);
        var thrown = catchThrowable(chargePoint::start);
        then(thrown)
                .isInstanceOf(RuntimeException.class)
                .hasRootCauseInstanceOf(UpgradeException.class)
                .rootCause().satisfies(c -> {
                    var ue = (UpgradeException) c;
                    assertThat(ue.getResponseStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    /**
     * https://github.com/steve-community/steve/issues/1109
     */
    @Test
    public void testWithNullPayload() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH);
        chargePoint.start();

        chargePoint.prepare(null, "Heartbeat", HeartbeatResponse.class,
            response -> assertThat(response.getCurrentTime()).isNotNull(),
            error -> fail()
        );

        chargePoint.processAndClose();
    }
}
