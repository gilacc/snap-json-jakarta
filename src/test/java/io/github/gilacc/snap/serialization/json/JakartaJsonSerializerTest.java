package io.github.gilacc.snap.serialization.json;

import io.github.gilacc.snap.SimpleSnapshot;
import io.github.gilacc.snap.Snapshot;
import io.github.gilacc.snap.serialization.json.jakarta.JakartaJsonSerializer;
import io.github.gilacc.snap.node.*;
import io.github.gilacc.snap.serialization.Serializer;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

class JakartaJsonSerializerTest {

    // {
    //     "name": "Antonio",
    //     "age": 25,
    //     "isContributor": false,
    //     "projects": ["snap", "finitegenerator"],
    //     "languages": {
    //         "Java": "Intermediate",
    //         "Rust": "Basic"
    //     }
    // }
    final Snapshot snapshot = new SimpleSnapshot(
        Map.ofEntries(
            Map.entry("name", new StringNode("Antonio")),
            Map.entry("age", new NumberNode(25)),
            Map.entry("isContributor", new BooleanNode(false)),
            Map.entry("projects", new ListNode(
                new StringNode("snap"),
                new StringNode("finitegenerator")
            )),
            Map.entry("languages", new MapNode(Map.ofEntries(
                Map.entry("Java", new StringNode("Intermediate")),
                Map.entry("Rust", new StringNode("Basic"))
            )))
        )
    );

    @Test
    void itCorrectlySerializesSnapshots() {
        final JsonObject expected = Json.createObjectBuilder()
            .add("name", "Antonio")
            .add("age", 25)
            .add("isContributor", false)
            .add("projects", Json.createArrayBuilder().add("snap").add("finitegenerator").build())
            .add("languages", Json.createObjectBuilder()
                .add("Java", "Intermediate")
                .add("Rust", "Basic")
                .build()
            ).build();
        final Serializer<JsonValue> serializer = new JakartaJsonSerializer(
            Json.createBuilderFactory(Collections.emptyMap())
        );
        assertThat(
            serializer.serializeMap(snapshot.nodes()).asJsonObject().entrySet(),
            everyItem(is(in(expected.asJsonObject().entrySet())))
        );
    }

}