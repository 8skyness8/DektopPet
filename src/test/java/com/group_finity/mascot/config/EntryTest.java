package com.group_finity.mascot.config;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntryTest {
    @Test
    void exposesAttributesAndSelectsOnlyMatchingElementChildren() throws Exception {
        Entry root = entry("<Mascot Name=\"DevPet\">text<Action Name=\"Stand\"/><Ignored/></Mascot>");

        assertEquals("Mascot", root.getName());
        assertEquals("DevPet", root.getAttribute("Name"));
        assertEquals("Stand", root.selectChildren("Action").getFirst().getAttribute("Name"));
        assertEquals(2, root.getChildren().size());
    }

    @Test
    void absentAttributesAndChildrenUseEmptyDefaults() throws Exception {
        Entry root = entry("<Mascot/>");

        assertNull(root.getAttribute("Name"));
        assertFalse(root.hasAttribute("Name"));
        assertEquals(0, root.selectChildren("Action").size());
    }

    @Test
    void malformedXmlIsRejectedByTheConfiguredParser() {
        assertThrows(SAXException.class, () -> entry("<Mascot>"));
    }

    static Entry entry(String xml) throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        return new Entry(document.getDocumentElement());
    }
}
