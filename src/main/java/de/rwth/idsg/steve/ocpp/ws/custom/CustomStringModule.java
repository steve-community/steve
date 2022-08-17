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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import org.owasp.encoder.Encode;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.08.2022
 */
public class CustomStringModule extends SimpleModule {

    public CustomStringModule() {
        super("CustomStringModule", new Version(0, 0, 1, null, "de.rwth.idsg", "steve"));

        super.addSerializer(String.class, new CustomStringSerializer());
        super.addDeserializer(String.class, new CustomStringDeserializer());
    }

    /**
     * Since {@link com.fasterxml.jackson.databind.ser.std.StringSerializer} is marked as final, its contents are
     * copied here (and adjusted as needed).
     */
    private static class CustomStringSerializer extends StdScalarSerializer<Object> {

        private static final long serialVersionUID = 1L;

        public CustomStringSerializer() {
            super(String.class, false);
        }

        @Override
        public boolean isEmpty(SerializerProvider prov, Object value) {
            String str = (String) value;
            return str.isEmpty();
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(objectToString(value));
        }

        @Override
        public final void serializeWithType(Object value, JsonGenerator gen, SerializerProvider provider,
                                            TypeSerializer typeSer) throws IOException {
            gen.writeString(objectToString(value));
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("string", true);
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            visitStringFormat(visitor, typeHint);
        }

        private static String objectToString(Object value) {
            return Encode.forHtml((String) value);
        }
    }

    private static class CustomStringDeserializer extends StringDeserializer {

        private static final long serialVersionUID = 1L;

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String val = super.deserialize(p, ctxt);
            return Encode.forHtml(val);
        }
    }
}
