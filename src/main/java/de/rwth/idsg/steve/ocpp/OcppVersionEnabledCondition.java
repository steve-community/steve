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
package de.rwth.idsg.steve.ocpp;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.ConfigurationCondition;

/**
 * The JSON and SOAP conditions are OR'ed
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.12.2025
 */
public class OcppVersionEnabledCondition {

    public static class V12 extends BeanCondition {

        @ConditionalOnBooleanProperty("steve.ocpp.enabled-protocols.v12.soap")
        static class SoapEnabled { }

        @ConditionalOnBooleanProperty("steve.ocpp.enabled-protocols.v12.json")
        static class JsonEnabled { }
    }

    public static class V15 extends BeanCondition {

        @ConditionalOnBooleanProperty("steve.ocpp.enabled-protocols.v15.soap")
        static class SoapEnabled { }

        @ConditionalOnBooleanProperty("steve.ocpp.enabled-protocols.v15.json")
        static class JsonEnabled { }
    }

    public static class V16 extends BeanCondition {

        @ConditionalOnBooleanProperty("steve.ocpp.enabled-protocols.v16.soap")
        static class SoapEnabled { }

        @ConditionalOnBooleanProperty("steve.ocpp.enabled-protocols.v16.json")
        static class JsonEnabled { }
    }

    public static abstract class BeanCondition extends AnyNestedCondition {

        public BeanCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }
    }
}

