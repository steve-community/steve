/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.08.2022
 */
public class CustomStringModuleTest {

    private final ObjectMapper mapper = JsonObjectMapper.INSTANCE.getMapper();

    @Test
    public void testNormalString() throws Exception {
        SimpleJsonModel input = new SimpleJsonModel("normal string");
        String output = mapper.writeValueAsString(input);
        Assertions.assertEquals("{\"someText\":\"normal string\"}", output);
    }

    @Test
    public void testLink() throws Exception {
        SimpleJsonModel input = new SimpleJsonModel("<a href=\"link\">Some link</a>");
        String output = mapper.writeValueAsString(input);
        Assertions.assertEquals("{\"someText\":\"&lt;a href=&#34;link&#34;&gt;Some link&lt;/a&gt;\"}", output);
    }

    @Test
    public void testScript() throws Exception {
        SimpleJsonModel input = new SimpleJsonModel("<script src=\"http://someurl.com/script.js\"/>");
        String output = mapper.writeValueAsString(input);
        Assertions.assertEquals("{\"someText\":\"&lt;script src=&#34;http://someurl.com/script.js&#34;/&gt;\"}", output);
    }

    @Data
    private static class SimpleJsonModel {
        private final String someText;
    }
}
