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

import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URISyntaxException;
import jakarta.xml.ws.WebServiceException;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp12;
import static de.rwth.idsg.steve.utils.Helpers.getForOcpp15;
import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static de.rwth.idsg.steve.utils.Helpers.getSoapPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.03.2018
 */
@Slf4j
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ApplicationTest {

    private static final String REGISTERED_CHARGE_BOX_ID = __DatabasePreparer__.getRegisteredChargeBoxId();
    private static final String REGISTERED_OCPP_TAG = __DatabasePreparer__.getRegisteredOcppTag();

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private SteveProperties steveProperties;

    @Autowired
    private DSLContext dslContext;

    private __DatabasePreparer__ databasePreparer;
    private URI soapPath;

    @BeforeEach
    public void setUp() throws URISyntaxException {
        databasePreparer = new __DatabasePreparer__(dslContext, steveProperties);
        databasePreparer.prepare();
        soapPath = getSoapPath(serverProperties, steveProperties);
    }

    @AfterEach
    public void tearDown() {
        databasePreparer.cleanUp();
    }

    @Test
    public void testOcpp12() {
        var client = getForOcpp12(soapPath);

        var boot = client.bootNotification(
                new ocpp.cs._2010._08.BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                REGISTERED_CHARGE_BOX_ID);
        assertThat(boot).isNotNull();
        assertThat(boot.getStatus()).isEqualTo(ocpp.cs._2010._08.RegistrationStatus.ACCEPTED);

        var auth = client.authorize(
                new ocpp.cs._2010._08.AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG), REGISTERED_CHARGE_BOX_ID);
        assertThat(auth).isNotNull();
        assertThat(auth.getIdTagInfo().getStatus()).isEqualTo(ocpp.cs._2010._08.AuthorizationStatus.ACCEPTED);
    }

    @Test
    public void testOcpp15() {
        var client = getForOcpp15(soapPath);

        var boot = client.bootNotification(
                new ocpp.cs._2012._06.BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                REGISTERED_CHARGE_BOX_ID);
        assertThat(boot).isNotNull();
        assertThat(boot.getStatus()).isEqualTo(ocpp.cs._2012._06.RegistrationStatus.ACCEPTED);

        var auth = client.authorize(
                new ocpp.cs._2012._06.AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG), REGISTERED_CHARGE_BOX_ID);
        assertThat(auth).isNotNull();
        assertThat(auth.getIdTagInfo().getStatus()).isEqualTo(ocpp.cs._2012._06.AuthorizationStatus.ACCEPTED);
    }

    /**
     * WebServiceException because we are sending an AuthorizeRequest from a random/unknown station.
     */
    @Test
    public void testOcpp16() {
        var client = getForOcpp16(soapPath);

        assertThatExceptionOfType(WebServiceException.class).isThrownBy(() -> {
            var boot = client.bootNotification(
                    new ocpp.cs._2015._10.BootNotificationRequest()
                            .withChargePointVendor(getRandomString())
                            .withChargePointModel(getRandomString()),
                    getRandomString());
            assertThat(boot).isNotNull();
            assertThat(boot.getStatus()).isEqualTo(ocpp.cs._2015._10.RegistrationStatus.REJECTED);

            var auth = client.authorize(
                    new ocpp.cs._2015._10.AuthorizeRequest().withIdTag(getRandomString()), getRandomString());
            assertThat(auth).isNotNull();
            assertThat(auth.getIdTagInfo().getStatus()).isEqualTo(ocpp.cs._2015._10.AuthorizationStatus.INVALID);
        });
    }
}
