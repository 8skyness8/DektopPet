package com.group_finity.mascot.platform.window;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WindowSnapshotMapperTest {
    @Test
    void mapsNativeEdgesAndFlagsWithoutFiltering() {
        WindowSnapshot snapshot = WindowSnapshotMapper.map(
                1234, -500, 20, 300, 620, "Editor", false, true);

        assertEquals(1234, snapshot.nativeIdentifier());
        assertEquals(new Bounds(-500, 20, 800, 600), snapshot.bounds());
        assertEquals("Editor", snapshot.title());
        assertFalse(snapshot.visible());
        assertTrue(snapshot.minimized());
    }

    @Test
    void malformedNativeEdgesProduceZeroAreaInsteadOfInvalidBounds() {
        WindowSnapshot snapshot = WindowSnapshotMapper.map(1, 10, 20, 5, 15, "", true, false);

        assertEquals(new Bounds(10, 20, 0, 0), snapshot.bounds());
    }
}
