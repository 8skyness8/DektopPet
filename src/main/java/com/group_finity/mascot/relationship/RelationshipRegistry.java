package com.group_finity.mascot.relationship;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.LongSupplier;

/** Shared relationship owner: all runtime mascots with one image set receive the same state. */
public final class RelationshipRegistry {
    public static final int MAX_CHARACTERS = 128;
    private static final long EVENT_COOLDOWN_MILLIS = 30_000;
    private final Path path;
    private final RelationshipStore store;
    private final LongSupplier clock;
    private final Map<String, RelationshipState> states;

    public RelationshipRegistry(Path path) { this(path, new RelationshipStore(), System::currentTimeMillis); }

    public RelationshipRegistry(Path path, RelationshipStore store, LongSupplier clock) {
        this.path = path;
        this.store = store;
        this.clock = clock;
        states = new LinkedHashMap<>(store.load(path));
    }

    public synchronized RelationshipState relationship(String imageSet) {
        RelationshipState existing = states.get(imageSet);
        if (existing != null) return existing;
        if (states.size() >= MAX_CHARACTERS) states.remove(states.keySet().iterator().next());
        RelationshipState created = new RelationshipState();
        states.put(imageSet, created);
        return created;
    }

    public synchronized boolean record(String imageSet, InteractionEvent event) {
        int delta = switch (event) { case AFFECTION -> 2; case DRAG -> 1; case THROW -> -1; };
        boolean changed = relationship(imageSet).apply(event, delta, EVENT_COOLDOWN_MILLIS, clock.getAsLong());
        if (changed) {
            try { store.save(path, states); } catch (IOException ignored) { /* state remains valid in memory */ }
        }
        return changed;
    }

    public synchronized int size() { return states.size(); }
    public synchronized void save() throws IOException { store.save(path, states); }
}
