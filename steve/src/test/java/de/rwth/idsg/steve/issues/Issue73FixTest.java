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
package de.rwth.idsg.steve.issues;

import com.google.common.collect.Lists;
import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.StartTransactionRequest;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static de.rwth.idsg.steve.utils.Helpers.getSoapPath;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://github.com/steve-community/steve/issues/73
 * https://github.com/steve-community/steve/issues/219
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 02.07.2018
 */
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Testcontainers
public class Issue73FixTest {

    private static final String REGISTERED_OCPP_TAG = __DatabasePreparer__.getRegisteredOcppTag();

    /*@Container
    @ServiceConnection
    private static final JdbcDatabaseContainer<?> DB_CONTAINER = new MySQLContainer("mysql:8.0");

    @DynamicPropertySource
    static void testProps(DynamicPropertyRegistry r) {
        r.add("steve.jooq.schema", DB_CONTAINER::getDatabaseName);
    }*/

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private SteveProperties steveProperties;

    @Autowired
    private DSLContext dslContext;

    private __DatabasePreparer__ databasePreparer;

    @BeforeEach
    public void setup() {
        databasePreparer = new __DatabasePreparer__(dslContext, steveProperties);
        databasePreparer.prepare();
    }

    @AfterEach
    public void teardown() {
        databasePreparer.cleanUp();
    }

    @Test
    public void test() throws URISyntaxException {
        var client = getForOcpp16(getSoapPath(serverProperties, steveProperties));

        var chargeBox1 = __DatabasePreparer__.getRegisteredChargeBoxId();
        var chargeBox2 = __DatabasePreparer__.getRegisteredChargeBoxId2();

        sendBoot(client, Lists.newArrayList(chargeBox1, chargeBox2));

        sendAuth(client, chargeBox1, AuthorizationStatus.ACCEPTED);

        sendStartTx(client, chargeBox1, AuthorizationStatus.ACCEPTED);

        sendAuth(client, chargeBox1, AuthorizationStatus.ACCEPTED);

        sendAuth(client, chargeBox2, AuthorizationStatus.ACCEPTED);

        sendStartTx(client, chargeBox2, AuthorizationStatus.CONCURRENT_TX);
    }

    private static void sendBoot(CentralSystemService client, List<String> chargeBoxIdList) {
        for (var chargeBoxId : chargeBoxIdList) {
            var boot = client.bootNotification(
                    new BootNotificationRequest()
                            .withChargePointVendor(getRandomString())
                            .withChargePointModel(getRandomString()),
                    chargeBoxId);
            assertThat(boot).isNotNull();
            assertThat(boot.getStatus()).isEqualTo(RegistrationStatus.ACCEPTED);
        }
    }

    private static void sendAuth(CentralSystemService client, String chargeBoxId, AuthorizationStatus expected) {
        var auth = client.authorize(new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG), chargeBoxId);
        assertThat(auth).isNotNull();
        assertThat(auth.getIdTagInfo().getStatus()).isEqualTo(expected);
    }

    private void sendStartTx(CentralSystemService client, String chargeBoxId, AuthorizationStatus expected) {
        var start = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(2)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(OffsetDateTime.now())
                        .withMeterStart(0),
                chargeBoxId);
        assertThat(start).isNotNull();
        assertThat(start.getIdTagInfo().getStatus()).isEqualTo(expected);
        assertThat(start.getTransactionId()).isGreaterThan(0);
        assertThat(databasePreparer.getOcppTagRecord(REGISTERED_OCPP_TAG).getInTransaction())
                .isTrue();
    }
}
