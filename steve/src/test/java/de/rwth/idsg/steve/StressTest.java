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
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.SampledValue;
import org.jooq.DSLContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.rwth.idsg.steve.utils.Helpers.getSoapPath;
import static de.rwth.idsg.steve.utils.Helpers.getWsPath;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.05.2018
 */
public abstract class StressTest {

    // higher values -> more stress
    //
    protected static final int THREAD_COUNT = 50;
    protected static final int REPEAT_COUNT_PER_THREAD = 50;

    // lower values -> more stress
    //
    // reason: these only specify the size of the values "bag" from which a test picks a value randomly. if there are
    // less values to pick from, it is more likely that tests will use the same value at the same time. this produces
    // more overhead for steve (especially db) when multiple threads "fight" for inserting/updating a db row/cell.
    //
    protected static final int ID_TAG_COUNT = 50;
    protected static final int CHARGE_BOX_COUNT = THREAD_COUNT;
    protected static final int CONNECTOR_COUNT_PER_CHARGE_BOX = 25;

    protected URI soapPath;
    protected URI jsonPath;

    protected void attack() throws Exception {
        var spring = new SpringApplication(SteveApplication.class);
        spring.setAdditionalProfiles("test");

        __DatabasePreparer__ databasePreparer = null;
        ConfigurableApplicationContext app = null;

        try {
            app = spring.run();

            var environment = app.getEnvironment();
            assertThat(environment.getActiveProfiles()).hasSize(1).contains("test");
            assertThat(environment.getProperty("steve.ocpp.auto-register-unknown-stations"))
                    .isEqualTo("true");

            var serverProperties = app.getBean(ServerProperties.class);
            var steveProperties = app.getBean(SteveProperties.class);
            soapPath = getSoapPath(serverProperties, steveProperties);
            jsonPath = getWsPath(serverProperties, steveProperties);

            var dslContext = app.getBean(DSLContext.class);
            databasePreparer = new __DatabasePreparer__(dslContext, steveProperties);
            databasePreparer.prepare();

            attackInternal();
        } finally {
            try {
                if (app != null) {
                    app.close();
                }
            } finally {
                if (databasePreparer != null) {
                    databasePreparer.cleanUp();
                }
            }
        }
    }

    protected abstract void attackInternal() throws Exception;

    protected static List<MeterValue> getMeterValues(int transactionStart, int transactionStop) {
        final var size = 4;
        var delta = (transactionStop - transactionStart) / size;
        if (delta == 0) {
            return Collections.emptyList();
        }

        var list = new ArrayList<MeterValue>(size);
        for (var i = 0; i < size; i++) {
            var meterValue = transactionStart + delta * (i + 1);
            list.add(createMeterValue(meterValue));
        }
        return list;
    }

    protected static MeterValue createMeterValue(int val) {
        return new MeterValue()
                .withTimestamp(OffsetDateTime.now())
                .withSampledValue(new SampledValue().withValue(Integer.toString(val)));
    }
}
