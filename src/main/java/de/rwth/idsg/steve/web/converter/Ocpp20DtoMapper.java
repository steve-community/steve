package de.rwth.idsg.steve.web.converter;

import de.rwth.idsg.steve.ocpp20.model.Component;
import de.rwth.idsg.steve.ocpp20.model.ComponentVariable;
import de.rwth.idsg.steve.ocpp20.model.EVSE;
import de.rwth.idsg.steve.ocpp20.model.HashAlgorithmEnum;
import de.rwth.idsg.steve.ocpp20.model.MessageContent;
import de.rwth.idsg.steve.ocpp20.model.MessageFormatEnum;
import de.rwth.idsg.steve.ocpp20.model.MessageInfo;
import de.rwth.idsg.steve.ocpp20.model.MessagePriorityEnum;
import de.rwth.idsg.steve.ocpp20.model.MessageStateEnum;
import de.rwth.idsg.steve.ocpp20.model.MonitorEnum;
import de.rwth.idsg.steve.ocpp20.model.MonitoringCriterionEnum;
import de.rwth.idsg.steve.ocpp20.model.OCSPRequestData;
import de.rwth.idsg.steve.ocpp20.model.SetMonitoringData;
import de.rwth.idsg.steve.ocpp20.model.Variable;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility methods to map OCPP 2.0 UI DTOs to generated schema model objects.
 */
public final class Ocpp20DtoMapper {

    private Ocpp20DtoMapper() {
    }

    public static MessageInfo toMessageInfo(de.rwth.idsg.steve.web.dto.ocpp20.MessageInfo dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Message details are required");
        }

        MessageInfo target = new MessageInfo();
        target.setId(dto.getId());
        target.setPriority(parseEnum(MessagePriorityEnum::fromValue, dto.getPriority(), "message priority"));

        if (dto.getState() != null && !dto.getState().isBlank()) {
            target.setState(parseEnum(MessageStateEnum::fromValue, dto.getState(), "message state"));
        }

        target.setStartDateTime(parseDateTime(dto.getStartDateTime(), "message start date"));
        target.setEndDateTime(parseDateTime(dto.getEndDateTime(), "message end date"));
        if (dto.getTransactionId() != null && !dto.getTransactionId().isBlank()) {
            target.setTransactionId(dto.getTransactionId());
        }

        target.setMessage(toMessageContent(dto.getMessage()));
        Component display = toDisplayComponent(dto.getDisplay());
        if (display != null) {
            target.setDisplay(display);
        }

        return target;
    }

    private static MessageContent toMessageContent(de.rwth.idsg.steve.web.dto.ocpp20.MessageContent dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Message content is required");
        }

        MessageContent content = new MessageContent();
        content.setFormat(parseEnum(MessageFormatEnum::fromValue, dto.getFormat(), "message format"));
        content.setLanguage(dto.getLanguage());
        content.setContent(dto.getContent());
        return content;
    }

    private static Component toDisplayComponent(de.rwth.idsg.steve.web.dto.ocpp20.Display dto) {
        if (dto == null) {
            return null;
        }

        boolean hasName = dto.getName() != null && !dto.getName().isBlank();
        boolean hasEvse = dto.getEvseId() != null;
        if (!hasName && !hasEvse) {
            return null;
        }

        Component component = new Component();
        component.setName(hasName ? dto.getName() : "Display");
        if (hasEvse) {
            EVSE evse = new EVSE();
            evse.setId(dto.getEvseId());
            component.setEvse(evse);
        }
        return component;
    }

    public static List<SetMonitoringData> toSetMonitoringData(List<de.rwth.idsg.steve.web.dto.ocpp20.SetMonitoringData> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }

        List<SetMonitoringData> result = new ArrayList<>(dtos.size());
        for (int index = 0; index < dtos.size(); index++) {
            de.rwth.idsg.steve.web.dto.ocpp20.SetMonitoringData dto = dtos.get(index);
            if (dto == null) {
                continue;
            }

            SetMonitoringData data = new SetMonitoringData();
            data.setId(dto.getId());
            if (dto.getTransaction() != null) {
                data.setTransaction(dto.getTransaction());
            }
            data.setValue(dto.getValue());
            data.setType(parseEnum(MonitorEnum::fromValue, dto.getType(), "monitor type", index));
            data.setSeverity(dto.getSeverity());
            data.setComponent(toComponent(dto.getComponent(), index));
            data.setVariable(toVariable(dto.getVariable(), index));
            result.add(data);
        }

        return result;
    }

    private static Component toComponent(de.rwth.idsg.steve.web.dto.ocpp20.ComponentDto dto, int index) {
        if (dto == null) {
            throw new IllegalArgumentException("Component data is required for monitoring entry " + (index + 1));
        }

        Component component = new Component();
        component.setName(dto.getName());
        component.setInstance(emptyToNull(dto.getInstance()));

        if (dto.getEvseId() != null || dto.getConnectorId() != null) {
            if (dto.getEvseId() == null) {
                throw new IllegalArgumentException("Entry " + (index + 1) + " EVSE ID is required when connector ID is provided");
            }
            EVSE evse = new EVSE();
            evse.setId(dto.getEvseId());
            evse.setConnectorId(dto.getConnectorId());
            component.setEvse(evse);
        }

        return component;
    }

    private static Variable toVariable(de.rwth.idsg.steve.web.dto.ocpp20.VariableDto dto, int index) {
        if (dto == null) {
            throw new IllegalArgumentException("Variable data is required for monitoring entry " + (index + 1));
        }

        Variable variable = new Variable();
        variable.setName(dto.getName());
        variable.setInstance(emptyToNull(dto.getInstance()));
        return variable;
    }

    public static List<ComponentVariable> toComponentVariables(List<de.rwth.idsg.steve.web.dto.ocpp20.ComponentVariableDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }

        return dtos.stream()
            .filter(Objects::nonNull)
            .filter(dto -> dto.getComponent() != null
                && dto.getComponent().getName() != null
                && !dto.getComponent().getName().isBlank())
            .map(Ocpp20DtoMapper::toComponentVariable)
            .collect(Collectors.toList());
    }

    private static ComponentVariable toComponentVariable(de.rwth.idsg.steve.web.dto.ocpp20.ComponentVariableDto dto) {
        ComponentVariable cv = new ComponentVariable();
        cv.setComponent(toComponent(dto.getComponent(), 0));
        if (dto.getVariable() != null && dto.getVariable().getName() != null && !dto.getVariable().getName().isBlank()) {
            cv.setVariable(toVariable(dto.getVariable(), 0));
        }
        return cv;
    }

    public static List<MonitoringCriterionEnum> toMonitoringCriteria(List<String> criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return Collections.emptyList();
        }

        return criteria.stream()
            .filter(value -> value != null && !value.isBlank())
            .map(value -> parseEnum(MonitoringCriterionEnum::fromValue, value, "monitoring criterion"))
            .collect(Collectors.toList());
    }

    public static OCSPRequestData toOcspRequestData(de.rwth.idsg.steve.web.dto.ocpp20.OCSPRequestData dto) {
        OCSPRequestData data = new OCSPRequestData();
        data.setHashAlgorithm(parseEnum(HashAlgorithmEnum::fromValue, dto.getHashAlgorithm(), "hash algorithm"));
        data.setIssuerNameHash(dto.getIssuerNameHash());
        data.setIssuerKeyHash(dto.getIssuerKeyHash());
        data.setSerialNumber(dto.getSerialNumber());
        data.setResponderURL(dto.getResponderUrl());
        return data;
    }

    public static OffsetDateTime parseDateTime(String value, String fieldLabel) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();
        try {
            return OffsetDateTime.parse(trimmed);
        } catch (DateTimeParseException ex) {
            try {
                LocalDateTime local = LocalDateTime.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return local.atOffset(ZoneOffset.UTC);
            } catch (DateTimeParseException nested) {
                throw new IllegalArgumentException(String.format(Locale.ENGLISH,
                    "Invalid %s. Please use ISO-8601 format (e.g. 2024-03-15T10:15:30Z).", fieldLabel));
            }
        }
    }

    private static <E> E parseEnum(EnumParser<E> parser, String value, String label) {
        return parseEnum(parser, value, label, -1);
    }

    private static <E> E parseEnum(EnumParser<E> parser, String value, String label, int index) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException((index >= 0 ? "Entry " + (index + 1) + " " : "") + label + " is required");
        }

        try {
            return parser.apply(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException((index >= 0 ? "Entry " + (index + 1) + " " : "")
                + "Invalid " + label + ": " + value);
        }
    }

    private static String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    @FunctionalInterface
    private interface EnumParser<E> {
        E apply(String value);
    }
}
