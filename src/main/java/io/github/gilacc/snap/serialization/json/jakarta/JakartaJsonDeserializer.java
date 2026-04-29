/*
 * src/main/java/io/github/gilacc/snap/deserialization/json/jakarta/JakartaJsonDeserializer.java - Deserializer impl
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


package io.github.gilacc.snap.serialization.json.jakarta;

import io.github.gilacc.snap.SimpleSnapshot;
import io.github.gilacc.snap.Snapshot;
import io.github.gilacc.snap.deserialization.Deserializer;
import io.github.gilacc.snap.node.*;
import jakarta.json.*;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>JSON deserializer implementation using the Jakarta JSON API as a backend.</p>
 */
public final class JakartaJsonDeserializer implements Deserializer<JsonObject> {

    @Override
    public Snapshot restore(final JsonObject source) {
        return new SimpleSnapshot(this.restoreNodeMap(source)
        );
    }

    private Map<String, Node> restoreNodeMap(final JsonObject object) {
        return object.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> this.restoreNode(entry.getValue())));
    }

    private Node restoreNode(final JsonValue value) {
        return switch (value.getValueType()) {
            case ARRAY -> this.restoreArrayNode(value.asJsonArray());
            case OBJECT -> this.restoreMapNode(value.asJsonObject());
            case STRING -> new StringNode(((JsonString) value).getString());
            case NUMBER -> new NumberNode(((JsonNumber) value).bigDecimalValue());
            case TRUE -> new BooleanNode(true);
            case FALSE -> new BooleanNode(false);
            default -> new NilNode();
        };
    }

    private Node restoreArrayNode(final JsonArray array) {
        return new ListNode(
            array.stream()
                .map(this::restoreNode)
                .toList()
        );
    }

    private Node restoreMapNode(final JsonObject object) {
        return new MapNode(this.restoreNodeMap(object));
    }

}
