package de.rwth.idsg.steve.ocpp.ws.custom;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2012._06.MeterValue;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom deserializer to work around broken charging station implementations,
 * that set the field name wrongfully to "values" rather than the correct "value".
 * We handle the situation by looking both fields up in the message.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 01.09.2015
 */
@Slf4j
public class MeterValue15Deserializer extends JsonDeserializer<List<MeterValue>> {

    @Override
    public List<MeterValue> deserialize(JsonParser jp, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {

        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = mapper.readTree(jp);
        return parseListMeterValue(mapper, node);
    }

    // List<MeterValue>
    private List<MeterValue> parseListMeterValue(ObjectMapper mapper, JsonNode listNode)
            throws JsonProcessingException {

        if (listNode.isMissingNode()) {
            return Collections.emptyList();
        } else {
            List<MeterValue> rootList = new ArrayList<>();
            for (JsonNode node : listNode) {
                rootList.add(buildMeterValue(mapper, node));
            }
            return rootList;
        }
    }

    // MeterValue
    private MeterValue buildMeterValue(ObjectMapper mapper, JsonNode node)
            throws JsonProcessingException {

        MeterValue meterValue = new MeterValue();
        List<MeterValue.Value> list = meterValue.getValue();

        parseValue(mapper, list, node.path("value"), true);
        parseValue(mapper, list, node.path("values"), false);
        parseDateTime(meterValue, node.path("timestamp"));

        return meterValue;
    }

    // List<MeterValue.Value>
    private void parseValue(ObjectMapper mapper, List<MeterValue.Value> list, JsonNode listNode, boolean isCorrect)
            throws JsonProcessingException {

        if (!listNode.isMissingNode()) {
            logBroken(isCorrect);
            for (JsonNode node : listNode) {
                list.add(mapper.treeToValue(node, MeterValue.Value.class));
            }
        }
    }

    private void parseDateTime(MeterValue meterValue, JsonNode node) {
        if (!node.isMissingNode()) {
            meterValue.setTimestamp(new DateTime(node.asText()));
        }
    }

    private void logBroken(boolean isCorrect) {
        if (isCorrect) {
            return;
        }

        log.warn("Received an invalid 'MeterValues' message from a charging station with a broken implementation, " +
                 "but still can process it. Please contact the manufacturer/vendor to report the bug.");
    }
}
