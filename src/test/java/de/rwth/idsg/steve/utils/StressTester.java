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
package de.rwth.idsg.steve.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 18.04.2018
 */
public class StressTester {

    private final int threadCount;
    private final int perThreadRepeatCount;
    private final ExecutorService executorService;

    public StressTester(int threadCount, int perThreadRepeatCount) {
        this.threadCount = threadCount;
        this.perThreadRepeatCount = perThreadRepeatCount;
        this.executorService = Executors.newCachedThreadPool();
    }

    public void test(StressTester.Runnable runnable) throws InterruptedException {
        final CountDownLatch doneSignal = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    runnable.beforeRepeat();
                    for (int j = 0; j < perThreadRepeatCount; j++) {
                        runnable.toRepeat();
                    }
                    runnable.afterRepeat();
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        doneSignal.await();
    }

    public void shutDown() {
        executorService.shutdown();
    }

    public interface Runnable {
        void beforeRepeat();
        void toRepeat();
        void afterRepeat();
    }
}
