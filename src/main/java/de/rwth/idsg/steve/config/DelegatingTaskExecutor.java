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
package de.rwth.idsg.steve.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 02.02.2025
 */
@Slf4j
@RequiredArgsConstructor
public class DelegatingTaskExecutor implements Closeable {

    private final ThreadPoolTaskExecutor delegate;

    @Override
    public void close() throws IOException {
        log.info("Shutting down");
        delegate.shutdown();
    }

    public void execute(Runnable task) {
        delegate.execute(task);
    }

}
