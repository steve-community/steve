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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 08.03.2018
 */
@RequiredArgsConstructor
public class BackgroundService {

    private final TaskExecutor taskExecutor;

    public static BackgroundService with(TaskExecutor taskExecutor) {
        return new BackgroundService(taskExecutor);
    }

    public Runner forFirst(List<ChargePointSelect> list) {
        return new BackgroundSingleRunner(list.get(0));
    }

    public Runner forEach(List<ChargePointSelect> list) {
        return new BackgroundListRunner(list);
    }

    public interface Runner {
        void execute(Consumer<ChargePointSelect> consumer);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private class BackgroundSingleRunner implements Runner {
        private final ChargePointSelect cps;

        @Override
        public void execute(Consumer<ChargePointSelect> consumer) {
            taskExecutor.execute(() -> consumer.accept(cps));
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private class BackgroundListRunner implements Runner {
        private final List<ChargePointSelect> list;

        @Override
        public void execute(Consumer<ChargePointSelect> consumer) {
            taskExecutor.execute(() -> list.forEach(consumer));
        }
    }
}
