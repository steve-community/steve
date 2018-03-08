package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 08.03.2018
 */
@RequiredArgsConstructor
public class BackgroundService {
    private final ExecutorService executorService;

    public static BackgroundService with(ExecutorService executorService) {
        return new BackgroundService(executorService);
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
            executorService.execute(() -> consumer.accept(cps));
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private class BackgroundListRunner implements Runner {
        private final List<ChargePointSelect> list;

        @Override
        public void execute(Consumer<ChargePointSelect> consumer) {
            executorService.execute(() -> list.forEach(consumer));
        }
    }
}
