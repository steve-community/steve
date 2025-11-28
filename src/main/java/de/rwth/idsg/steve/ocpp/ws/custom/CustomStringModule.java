/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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

import org.owasp.encoder.Encode;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.deser.jdk.StringDeserializer;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.jdk.StringSerializer;
import tools.jackson.databind.ser.std.StdScalarSerializer;

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
     * Since {@link StringSerializer} is marked as final, its contents are copied here (and adjusted as needed).
     */
    private static class CustomStringSerializer extends StdScalarSerializer<Object> {

        public CustomStringSerializer() {
            super(String.class, false);
        }

        @Override
        public boolean isEmpty(SerializationContext prov, Object value) {
            String str = (String) value;
            return str.isEmpty();
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
            gen.writeString(objectToString(value));
        }

        @Override
        public final void serializeWithType(Object value, JsonGenerator gen, SerializationContext provider,
                                            TypeSerializer typeSer) throws JacksonException {
            gen.writeString(objectToString(value));
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws DatabindException {
            visitStringFormat(visitor, typeHint);
        }

        private static String objectToString(Object value) {
            return Encode.forHtml((String) value);
        }
    }

    private static class CustomStringDeserializer extends StringDeserializer {

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            String val = super.deserialize(p, ctxt);
            return Encode.forHtml(val);
        }
    }
}
