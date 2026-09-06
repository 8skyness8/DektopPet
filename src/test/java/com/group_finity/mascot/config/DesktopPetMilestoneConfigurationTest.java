package com.group_finity.mascot;

import com.group_finity.mascot.config.Configuration;
import com.group_finity.mascot.config.Entry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DesktopPetMilestoneConfigurationTest {
    private static final Path GENERATED_DEV_PET = Path.of("target", "generated-distribution", "img", "DevPet");
    private static final Path RUNTIME_DEV_PET = Path.of("img", "DevPet");

    @BeforeAll
    static void initializeRuntimePrerequisites() throws Exception {
        Main.getInstance().loadLanguage(Locale.ENGLISH);

        if (!Files.isDirectory(GENERATED_DEV_PET)) {
            throw new IllegalStateException("Generated DevPet sprites are missing: " + GENERATED_DEV_PET);
        }

        Files.createDirectories(RUNTIME_DEV_PET);
        try (var files = Files.list(GENERATED_DEV_PET)) {
            for (Path source : files.toList()) {
                if (Files.isRegularFile(source)) {
                    Files.copy(source, RUNTIME_DEV_PET.resolve(source.getFileName()));
                }
            }
        }
    }

    @AfterAll
    static void cleanUpRuntimeAssets() throws Exception {
        if (Files.exists(RUNTIME_DEV_PET)) {
            try (var paths = Files.walk(RUNTIME_DEV_PET)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    @Test void productionConfigurationLoadsAndValidates() throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        var actions = factory.newDocumentBuilder().parse(Path.of("conf/actions.xml").toFile());
        var behaviors = factory.newDocumentBuilder().parse(Path.of("conf/behaviors.xml").toFile());

        Configuration configuration = new Configuration();
        configuration.load(new Entry(actions.getDocumentElement()), "DevPet");
        configuration.load(new Entry(behaviors.getDocumentElement()), "DevPet");
        configuration.validate();

        // Assert the production V2 metadata through the source configuration rather than
        // reaching into Configuration's package-private implementation details.
        assertEquals("300", namedAttribute(behaviors, "Behavior", "CuriousObservation", "Cooldown"));
        assertEquals("0.8", namedAttribute(behaviors, "Behavior", "CuriousObservation", "BoredomWeight"));
        assertEquals("0.25", namedAttribute(behaviors, "Behavior", "OfferGreeting", "RelationshipWeight"));
        assertEquals("0.15", namedAttribute(behaviors, "Behavior", "IdleThought", "RelationshipWeight"));

        assertEquals(1, named(actions, "Action", "ClimbWindowSide"));
        assertEquals(1, named(actions, "Action", "HangFromWindowBottom"));
        assertEquals(1, named(actions, "Action", "IncreaseCuriosity"));
        assertEquals(1, named(actions, "Action", "PetResponse"));
        assertEquals(1, named(actions, "Action", "SayPetting"));
        assertEquals(1, named(actions, "Action", "SayCurious"));
        assertEquals(1, named(actions, "Action", "SayIdle"));
        assertEquals(1, named(actions, "Action", "SayGreeting"));
        assertEquals(1, named(actions, "Action", "OfferGreeting"));
        assertEquals(1, named(actions, "Action", "AnswerGreeting"));
        assertEquals(1, named(actions, "Hotspot", null));
        assertEquals(1, named(behaviors, "Behavior", "CuriousObservation"));
        assertEquals(1, named(behaviors, "Behavior", "PetResponse"));
        assertEquals(1, named(behaviors, "Behavior", "ClimbWindowSide"));
        assertEquals(1, named(behaviors, "Behavior", "HangFromWindowBottom"));
        assertEquals(1, named(behaviors, "Behavior", "OfferGreeting"));
        assertEquals(1, named(behaviors, "Behavior", "AnswerGreeting"));
        assertEquals(1, named(behaviors, "Behavior", "IdleThought"));
    }

    private static int named(org.w3c.dom.Document document, String element, String name) {
        int matches = 0;
        var nodes = document.getElementsByTagNameNS("*", element);
        for (int i = 0; i < nodes.getLength(); i++) {
            var nameAttribute = nodes.item(i).getAttributes().getNamedItem("Name");
            if (name == null || (nameAttribute != null && name.equals(nameAttribute.getNodeValue()))) {
                matches++;
            }
        }
        return matches;
    }

    private static String namedAttribute(org.w3c.dom.Document document, String element, String name, String attribute) {
        var nodes = document.getElementsByTagNameNS("*", element);
        for (int i = 0; i < nodes.getLength(); i++) {
            var attributes = nodes.item(i).getAttributes();
            var nameAttribute = attributes.getNamedItem("Name");
            if (nameAttribute != null && name.equals(nameAttribute.getNodeValue())) {
                var value = attributes.getNamedItem(attribute);
                return value == null ? null : value.getNodeValue();
            }
        }
        return null;
    }
}
