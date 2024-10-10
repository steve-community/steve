package net.parkl.ocpp.service.config;

import lombok.RequiredArgsConstructor;
import net.parkl.ocpp.service.middleware.receiver.AsyncMessageReceiver;
import net.parkl.ocpp.service.middleware.receiver.AsyncMessageReceiverLocator;
import net.parkl.ocpp.service.middleware.receiver.InMemoryAsyncMessageReceiver;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestAsyncMessageReceiverLocator implements AsyncMessageReceiverLocator {
    private final InMemoryAsyncMessageReceiver inMemoryAsyncMessageReceiver;

    @Override
    public AsyncMessageReceiver get() {
        return inMemoryAsyncMessageReceiver;
    }
}
