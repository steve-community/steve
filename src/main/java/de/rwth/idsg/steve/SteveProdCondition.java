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

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * We might also have used {@link Profile} for registering beans depending on profile,
 * but it only accepts String as value (which is not type safe) and we use enums
 * in {@link ApplicationProfile}. The newer {@link Condition} and {@link Conditional} APIs
 * are more flexible by being programmatic anyway.
 *
 * Typing the String value of the enum is not an option, because it might cause problems
 * in future when we change or refactor something. This is how technical debt starts
 * and then everything falls apart.
 *
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 28.12.2015
 */
public class SteveProdCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return CONFIG.getProfile().isProd();
    }
}
