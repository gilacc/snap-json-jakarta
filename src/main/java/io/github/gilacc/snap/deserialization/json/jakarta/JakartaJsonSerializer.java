/*
 * src/main/java/io/github/gilacc/snap/serialization/json/jakarta/JakartaJsonSerializer.java - Serializer impl
 * Copyright (C) 2026 Antonio Gil
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

package io.github.gilacc.snap.deserialization.json.jakarta;

import io.github.gilacc.snap.node.Node;
import io.github.gilacc.snap.serialization.Serializer;
import jakarta.json.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>JSON serializer implementation using the Jakarta JSON API as a backend.</p>
 */
public final class JakartaJsonSerializer implements Serializer<JsonValue> {

    private final JsonBuilderFactory jsonBuilderFactory;

    public JakartaJsonSerializer(final JsonBuilderFactory jsonBuilderFactory) {
        this.jsonBuilderFactory = jsonBuilderFactory;
    }

    @Override
    public JsonValue serializeNumber(final Number number) {
        final String representation = new DecimalFormat(
            "#.##################",
            new DecimalFormatSymbols(Locale.ENGLISH)
        ).format(number);
        return Json.createValue(new BigDecimal(representation));
    }

    @Override
    public JsonValue serializeBoolean(final boolean bool) {
        return (bool) ? JsonValue.TRUE : JsonValue.FALSE;
    }

    @Override
    public JsonValue serializeString(final String string) {
        return Json.createValue(string);
    }

    @Override
    public JsonValue serializeList(final List<Node> list) {
        final JsonArrayBuilder arrayBuilder = this.jsonBuilderFactory.createArrayBuilder();
        list.stream()
            .map(node -> node.serialize(this))
            .forEach(arrayBuilder::add);
        return arrayBuilder.build();
    }

    @Override
    public JsonValue serializeMap(final Map<String, Node> map) {
        final JsonObjectBuilder objectBuilder = this.jsonBuilderFactory.createObjectBuilder();
        map.entrySet().stream()
            .filter(entry -> entry.getValue() != null)
            .forEach(entry -> objectBuilder.add(entry.getKey(), entry.getValue().serialize(this)));
        return objectBuilder.build();
    }

    @Override
    public JsonValue serializeNil() {
        return JsonValue.NULL;
    }

}
