/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.ocpp.ws;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

/**
 * Verifies that the Jackson ObjectMapper does not HTML-encode string values.
 * Strings must be serialized using standard JSON escaping (RFC 8259) so that
 * OCPP wire payloads are not corrupted.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.08.2022
 */
public class JsonObjectMapperStringTest {

    private final ObjectMapper mapper = JsonObjectMapper.INSTANCE.getMapper();

    @Test
    public void testNormalString() {
        SimpleJsonModel input = new SimpleJsonModel("normal string");
        String output = mapper.writeValueAsString(input);

        Assertions.assertEquals("""
            {"someText":"normal string"}""",
            output
        );
    }

    /**
     * Standard JSON escaping: quotes inside strings are escaped with backslash,
     * angle brackets are passed through unmodified (no HTML encoding).
     */
    @Test
    public void testLink() {
        SimpleJsonModel input = new SimpleJsonModel("""
            <a href="link">Some link</a>""");
        String output = mapper.writeValueAsString(input);

        Assertions.assertEquals("""
            {"someText":"<a href=\\"link\\">Some link</a>"}""",
            output
        );
    }

    /**
     * Standard JSON escaping: no HTML encoding of angle brackets or quotes.
     */
    @Test
    public void testScript() {
        SimpleJsonModel input = new SimpleJsonModel("""
            <script src="http://someurl.com/script.js"/>""");
        String output = mapper.writeValueAsString(input);

        Assertions.assertEquals("""
            {"someText":"<script src=\\"http://someurl.com/script.js\\"/>"}""",
            output
        );
    }

    /**
     * Reproduces the bug from issue #938: DataTransfer payloads with embedded quotes must not be HTML-encoded.
     */
    @Test
    public void testDataTransferWithQuotes() {
        String data = """
            {"txId":"123456","description": "Charging:$2.81"}""";
        SimpleJsonModel input = new SimpleJsonModel(data);
        String output = mapper.writeValueAsString(input);

        Assertions.assertEquals("""
            {"someText":"{\\"txId\\":\\"123456\\",\\"description\\": \\"Charging:$2.81\\"}"}""",
            output
        );
    }

    /**
     * Verify that serialization followed by deserialization preserves the original string.
     */
    @Test
    public void testRoundTrip() {
        String original = """
            <script>alert("xss")</script>""";
        SimpleJsonModel input = new SimpleJsonModel(original);
        String json = mapper.writeValueAsString(input);
        SimpleJsonModel deserialized = mapper.readValue(json, SimpleJsonModel.class);
        Assertions.assertEquals(original, deserialized.someText());
    }

    private record SimpleJsonModel(
        String someText
    ) {
    }
}
