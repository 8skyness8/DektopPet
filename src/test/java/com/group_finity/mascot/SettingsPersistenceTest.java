package com.group_finity.mascot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SettingsPersistenceTest {
    @TempDir Path temporaryDirectory;

    @Test void newPresentationSettingIsOptInAndRoundTripsAtomically() throws Exception {
        Path settingsFile = temporaryDirectory.resolve("nested/settings.properties");
        Settings defaults = new Settings();
        defaults.load(settingsFile);
        assertFalse(defaults.presentationBubbles);

        defaults.presentationBubbles = true;
        defaults.sounds = false;
        defaults.save(settingsFile);
        Settings reloaded = new Settings();
        reloaded.load(settingsFile);
        assertTrue(reloaded.presentationBubbles);
        assertFalse(reloaded.sounds);
        assertFalse(Files.exists(settingsFile.resolveSibling("settings.properties.tmp")));
    }

    @Test void loadingAgainDoesNotRetainPropertiesRemovedFromDisk() throws Exception {
        Path file = temporaryDirectory.resolve("settings.properties");
        Files.writeString(file, "PresentationBubbles=true\n");
        Settings settings = new Settings();
        settings.load(file);
        assertTrue(settings.presentationBubbles);
        Files.writeString(file, "Sounds=true\n");
        settings.load(file);
        assertFalse(settings.presentationBubbles);
    }
}
