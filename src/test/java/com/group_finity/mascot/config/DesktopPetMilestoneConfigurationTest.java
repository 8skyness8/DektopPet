package com.group_finity.mascot.config;

import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DesktopPetMilestoneConfigurationTest {
    @Test void productionConfigurationLoadsAndValidates() throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        var actions = factory.newDocumentBuilder().parse(Path.of("conf/actions.xml").toFile());
        var behaviors = factory.newDocumentBuilder().parse(Path.of("conf/behaviors.xml").toFile());

        Configuration configuration = new Configuration();
        configuration.load(new Entry(actions.getDocumentElement()), "DevPet");
        configuration.load(new Entry(behaviors.getDocumentElement()), "DevPet");
        configuration.validate();

        assertEquals(1, named(actions, "Action", "ClimbWindowSide"));
        assertEquals(1, named(actions, "Action", "HangFromWindowBottom"));
        assertEquals(1, named(actions, "Action", "IncreaseCuriosity"));
        assertEquals(1, named(actions, "Action", "PetResponse"));
        assertEquals(1, named(actions, "Action", "SayThanks"));
        assertEquals(1, named(actions, "Action", "OfferGreeting"));
        assertEquals(1, named(actions, "Action", "AnswerGreeting"));
        assertEquals(1, named(actions, "Hotspot", null));
        assertEquals(1, named(behaviors, "Behavior", "CuriousObservation"));
        assertEquals(1, named(behaviors, "Behavior", "PetResponse"));
        assertEquals(1, named(behaviors, "Behavior", "ClimbWindowSide"));
        assertEquals(1, named(behaviors, "Behavior", "HangFromWindowBottom"));
        assertEquals(1, named(behaviors, "Behavior", "OfferGreeting"));
        assertEquals(1, named(behaviors, "Behavior", "AnswerGreeting"));
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
}
