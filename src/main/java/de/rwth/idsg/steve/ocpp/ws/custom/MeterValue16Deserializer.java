package de.rwth.idsg.steve.ocpp.ws.custom;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.SampledValue;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class MeterValue16Deserializer extends JsonDeserializer<List<MeterValue>>
{
    @Override
    public List<MeterValue> deserialize(JsonParser jp, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {

        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = mapper.readTree(jp);
        if (node == null) {
            return Collections.emptyList();
        }
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
        List<SampledValue> list = meterValue.getSampledValue();

        parseValue(mapper, list, node.path("sampledValue"));
        parseDateTime(meterValue, node.path("timestamp"));

        return meterValue;
    }

    // List<MeterValue.Value>
    private void parseValue(ObjectMapper mapper, List<SampledValue> list, JsonNode listNode)
            throws JsonProcessingException {

        if (!listNode.isMissingNode()) {
            for (JsonNode node : listNode) {
                list.add(mapper.treeToValue(node, SampledValue.class));
            }
        }
    }

    private void parseDateTime(MeterValue meterValue, JsonNode node) {
        if (!node.isMissingNode()) {
            meterValue.setTimestamp(new DateTime(node.asText()));
        }
    }
}
