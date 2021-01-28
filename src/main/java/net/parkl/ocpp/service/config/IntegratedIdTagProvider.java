package net.parkl.ocpp.service.config;

import java.util.List;

/**
 * Interface for externally provided id tags
 */
public interface IntegratedIdTagProvider {

    List<String> integratedTags();

}
