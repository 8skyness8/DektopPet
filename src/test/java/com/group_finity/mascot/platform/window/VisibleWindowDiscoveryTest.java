package com.group_finity.mascot.platform.window;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisibleWindowDiscoveryTest {
    private static final Bounds AREA = new Bounds(-100, 20, 800, 600);

    @Test
    void returnsUsableWindowsInPlatformEnumerationOrder() {
        WindowSnapshot first = snapshot(1);
        WindowSnapshot hidden = withFlags(2, false, false, false, false, false, false, false, true, AREA);
        WindowSnapshot second = snapshot(3);
        DesktopWindowInfo source = source(List.of(first, hidden, second));

        assertEquals(List.of(first, second), new VisibleWindowDiscovery(source).discover());
    }

    @Test
    void acceptsVisibleRestoredInteractiveTopLevelApplicationWindow() {
        assertTrue(VisibleWindowDiscovery.isUsable(snapshot(1)));
    }

    @Test
    void excludesEveryDocumentedIneligibleWindowKind() {
        assertFalse(VisibleWindowDiscovery.isUsable(withFlags(1, false, false, false, false, false, false, false, true, AREA)), "hidden");
        assertFalse(VisibleWindowDiscovery.isUsable(withFlags(2, true, true, false, false, false, false, false, true, AREA)), "minimized");
        assertFalse(VisibleWindowDiscovery.isUsable(withFlags(3, true, false, true, false, false, false, false, true, AREA)), "cloaked");
        assertFalse(VisibleWindowDiscovery.isUsable(withFlags(4, true, false, false, true, false, false, false, true, AREA)), "tool window");
        assertFalse(VisibleWindowDiscovery.isUsable(withFlags(5, true, false, false, false, true, false, false, true, AREA)), "owned window");
        assertFalse(VisibleWindowDiscovery.isUsable(withFlags(6, true, false, false, false, false, true, false, true, AREA)), "DesktopPet-owned");
        assertFalse(VisibleWindowDiscovery.isUsable(withFlags(7, true, false, false, false, false, false, true, true, AREA)), "shell window");
        assertFalse(VisibleWindowDiscovery.isUsable(withFlags(8, true, false, false, false, false, false, false, false, AREA)), "disabled/non-interactive");
        assertFalse(VisibleWindowDiscovery.isUsable(withFlags(9, true, false, false, false, false, false, false, true, Bounds.EMPTY)), "zero-area");
    }

    private static WindowSnapshot snapshot(long identifier) {
        return withFlags(identifier, true, false, false, false, false, false, false, true, AREA);
    }

    private static WindowSnapshot withFlags(long identifier, boolean visible, boolean minimized,
                                            boolean cloaked, boolean toolWindow, boolean owned,
                                            boolean desktopPetOwned, boolean shellWindow,
                                            boolean interactive, Bounds bounds) {
        return new WindowSnapshot(identifier, bounds, "Application", visible, minimized,
                cloaked, toolWindow, owned, desktopPetOwned, shellWindow, interactive);
    }

    private static DesktopWindowInfo source(List<WindowSnapshot> windows) {
        return new DesktopWindowInfo() {
            public Bounds getDesktopBounds() { return Bounds.EMPTY; }
            public Bounds getWorkAreaBounds() { return Bounds.EMPTY; }
            public List<WindowSnapshot> getTopLevelWindows() { return windows; }
        };
    }
}
