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
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 01.09.2015
 */
@Slf4j
public class MeterValue15Deserializer extends JsonDeserializer<List<MeterValue>> {

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
        List<MeterValue.Value> list = meterValue.getValue();

        parseValue(mapper, list, node.path("value"));
        parseValue(mapper, list, node.path("values"));
        parseDateTime(meterValue, node.path("timestamp"));

        return meterValue;
    }

    // List<MeterValue.Value>
    private void parseValue(ObjectMapper mapper, List<MeterValue.Value> list, JsonNode listNode)
            throws JsonProcessingException {

        if (!listNode.isMissingNode()) {
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
}
