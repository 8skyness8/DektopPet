package com.group_finity.mascot.relationship;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/** Properties persistence containing only image-set identity, bond, and gesture cooldown timestamps. */
public final class RelationshipStore {
    public Map<String, RelationshipState> load(Path path) {
        Properties properties = new Properties();
        if (!Files.isRegularFile(path)) return new LinkedHashMap<>();
        try (InputStream input = Files.newInputStream(path)) { properties.load(input); }
        catch (IOException | IllegalArgumentException ignored) { return new LinkedHashMap<>(); }

        Map<String, RelationshipState> result = new LinkedHashMap<>();
        for (String key : properties.stringPropertyNames()) {
            if (!key.endsWith(".bond")) continue;
            String encoded = key.substring(0, key.length() - 5);
            String identity = decode(encoded);
            if (identity == null || identity.isBlank()) continue;
            try {
                int value = Integer.parseInt(properties.getProperty(key));
                if (value < RelationshipState.MIN_BOND || value > RelationshipState.MAX_BOND) continue;
                RelationshipState state = new RelationshipState(value);
                for (InteractionEvent event : InteractionEvent.values()) {
                    String timestamp = properties.getProperty(encoded + ".last." + event.name());
                    if (timestamp != null) state.restoreAccepted(event, Long.parseLong(timestamp));
                }
                result.put(identity, state);
            } catch (NumberFormatException ignored) { /* malformed character entries use defaults */ }
        }
        return result;
    }

    public void save(Path path, Map<String, RelationshipState> states) throws IOException {
        Properties properties = new Properties();
        states.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            String encoded = encode(entry.getKey());
            properties.setProperty(encoded + ".bond", Integer.toString(entry.getValue().getBond()));
            entry.getValue().acceptedSnapshot().forEach((event, time) ->
                    properties.setProperty(encoded + ".last." + event.name(), Long.toString(time)));
        });
        Path target = path.toAbsolutePath();
        Files.createDirectories(target.getParent());
        Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
        try {
            try (OutputStream output = Files.newOutputStream(temporary)) {
                properties.store(output, "DesktopPet character relationships (no personal data)");
            }
            try { Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally { Files.deleteIfExists(temporary); }
    }

    private static String encode(String value) {
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        try { return new String(java.util.Base64.getUrlDecoder().decode(value), java.nio.charset.StandardCharsets.UTF_8); }
        catch (IllegalArgumentException ignored) { return null; }
    }
}
