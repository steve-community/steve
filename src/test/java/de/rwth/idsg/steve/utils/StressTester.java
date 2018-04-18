package de.rwth.idsg.steve.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
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

    public void test(Runnable action) throws InterruptedException {
        final CountDownLatch doneSignal = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    for (int j = 0; j < perThreadRepeatCount; j++) {
                        action.run();
                    }
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
}
