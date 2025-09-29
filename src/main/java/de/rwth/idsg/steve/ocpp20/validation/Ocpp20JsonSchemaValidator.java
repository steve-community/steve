package de.rwth.idsg.steve.ocpp20.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class Ocpp20JsonSchemaValidator {

    private final Map<String, JsonSchema> schemaCache = new HashMap<>();
    private final JsonSchemaFactory factory;
    private final ObjectMapper objectMapper;
    private final boolean validationEnabled;

    public Ocpp20JsonSchemaValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        this.validationEnabled = loadSchemas();
    }

    private boolean loadSchemas() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:ocpp20/schemas/*.json");

            if (resources.length == 0) {
                log.warn("No OCPP 2.0 JSON schemas found - validation disabled");
                return false;
            }

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null && filename.endsWith(".json")) {
                    String actionName = filename.replace(".json", "");
                    try (InputStream is = resource.getInputStream()) {
                        JsonSchema schema = factory.getSchema(is);
                        schemaCache.put(actionName, schema);
                        log.debug("Loaded OCPP 2.0 schema for: {}", actionName);
                    }
                }
            }

            log.info("Loaded {} OCPP 2.0 JSON schemas", schemaCache.size());
            return true;

        } catch (Exception e) {
            log.error("Failed to load OCPP 2.0 JSON schemas - validation disabled", e);
            return false;
        }
    }

    public ValidationResult validate(String action, JsonNode payloadNode) {
        if (!validationEnabled) {
            return ValidationResult.skipped();
        }

        JsonSchema schema = schemaCache.get(action + "Request");
        if (schema == null) {
            log.debug("No schema found for action: {}", action);
            return ValidationResult.skipped();
        }

        try {
            Set<ValidationMessage> errors = schema.validate(payloadNode);
            if (errors.isEmpty()) {
                return ValidationResult.valid();
            }

            StringBuilder errorMsg = new StringBuilder("Schema validation failed for " + action + ": ");
            errors.forEach(error -> errorMsg.append(error.getMessage()).append("; "));
            return ValidationResult.invalid(errorMsg.toString());

        } catch (Exception e) {
            log.error("Error during schema validation for action '{}'", action, e);
            return ValidationResult.error("Validation error: " + e.getMessage());
        }
    }

    public boolean isValidationEnabled() {
        return validationEnabled;
    }

    public static class ValidationResult {
        private final boolean valid;
        private final boolean skipped;
        private final String errorMessage;

        private ValidationResult(boolean valid, boolean skipped, String errorMessage) {
            this.valid = valid;
            this.skipped = skipped;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, false, null);
        }

        public static ValidationResult invalid(String errorMessage) {
            return new ValidationResult(false, false, errorMessage);
        }

        public static ValidationResult skipped() {
            return new ValidationResult(true, true, null);
        }

        public static ValidationResult error(String errorMessage) {
            return new ValidationResult(false, false, errorMessage);
        }

        public boolean isValid() {
            return valid;
        }

        public boolean isSkipped() {
            return skipped;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}