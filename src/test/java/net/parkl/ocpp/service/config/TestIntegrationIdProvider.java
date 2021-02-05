package net.parkl.ocpp.service.config;

import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.singletonList;

@Component
public class TestIntegrationIdProvider implements IntegratedIdTagProvider {
    @Override
    public List<String> integratedTags() {
        return singletonList("TEST_ID_TAG");
    }
}
