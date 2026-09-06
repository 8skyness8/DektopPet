package com.group_finity.mascot.relationship;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class RelationshipStateTest {
    @Test void interactionEffectsAreBoundedAndRateLimited() {
        RelationshipState state = new RelationshipState(99);
        assertTrue(state.apply(InteractionEvent.AFFECTION, 20, 100, 1_000));
        assertEquals(100, state.getBond());
        assertFalse(state.apply(InteractionEvent.AFFECTION, 20, 100, 1_001));
        assertTrue(state.apply(InteractionEvent.THROW, -200, 100, 1_000));
        assertEquals(0, state.getBond());
    }

    @Test void persistenceRoundTripAndMalformedFallback() throws Exception {
        var directory = Files.createTempDirectory("relationship-test");
        var file = directory.resolve("relationships.properties");
        RelationshipStore store = new RelationshipStore();
        RelationshipState state = new RelationshipState(73);
        assertTrue(state.apply(InteractionEvent.DRAG, 1, 0, 45));
        assertEquals(74, state.getBond());
        store.save(file, Map.of("Dev Pet/日本", state));
        RelationshipState loaded = store.load(file).get("Dev Pet/日本");
        assertEquals(74, loaded.getBond());
        assertFalse(loaded.apply(InteractionEvent.DRAG, 1, 100, 46));

        Files.writeString(file, "not valid unicode=\\uXXZZ\n");
        assertTrue(store.load(file).isEmpty());
        assertTrue(store.load(directory.resolve("missing")).isEmpty());
    }

    @Test void runtimeInstancesShareCharacterStateAndRegistryIsBounded() throws Exception {
        AtomicLong clock = new AtomicLong(1_000);
        var path = Files.createTempDirectory("relationship-registry-test").resolve("relationships.properties");
        RelationshipRegistry registry = new RelationshipRegistry(
                path, new RelationshipStore(), clock::get);
        assertSame(registry.relationship("DevPet"), registry.relationship("DevPet"));
        assertTrue(registry.record("DevPet", InteractionEvent.AFFECTION));
        assertEquals(52, registry.relationship("DevPet").getBond());
        assertFalse(registry.record("DevPet", InteractionEvent.AFFECTION));
        for (int i = 0; i < 200; i++) registry.relationship("character-" + i);
        assertEquals(RelationshipRegistry.MAX_CHARACTERS, registry.size());
    }
}
