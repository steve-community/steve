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

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.12.2025
 */
public class OcppVersionEnabledCondition {

    public static class V12 implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return isEnabled(
                context,
                "steve.ocpp.enabled-protocols.v12.json",
                "steve.ocpp.enabled-protocols.v12.soap"
            );
        }
    }

    public static class V15 implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return isEnabled(
                context,
                "steve.ocpp.enabled-protocols.v15.json",
                "steve.ocpp.enabled-protocols.v15.soap"
            );
        }
    }

    public static class V16 implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return isEnabled(
                context,
                "steve.ocpp.enabled-protocols.v16.json",
                "steve.ocpp.enabled-protocols.v16.soap"
            );
        }
    }

    private static boolean isEnabled(ConditionContext context, String jsonKey, String soapKey) {
        var env = context.getEnvironment();
        boolean json = env.getProperty(jsonKey, Boolean.class, false);
        boolean soap = env.getProperty(soapKey, Boolean.class, false);
        return json || soap;
    }
}

