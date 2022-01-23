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
package de.rwth.idsg.steve.ocpp.ws.custom;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.03.2018
 */
public final class EnumProcessor {

    private EnumProcessor() { }

    public static void apply(List<String> packageNames, Consumer<Class<?>> clazzConsumer) {
        packageNames.forEach(p -> apply(p, clazzConsumer));
    }

    public static void apply(String packageName, Consumer<Class<?>> clazzConsumer) {
        try {
            ImmutableSet<ClassPath.ClassInfo> classInfos =
                    ClassPath.from(Thread.currentThread().getContextClassLoader())
                             .getTopLevelClasses(packageName);

            for (ClassPath.ClassInfo classInfo : classInfos) {
                Class<?> clazz = classInfo.load();
                if (clazz.isEnum()) {
                    clazzConsumer.accept(clazz);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
