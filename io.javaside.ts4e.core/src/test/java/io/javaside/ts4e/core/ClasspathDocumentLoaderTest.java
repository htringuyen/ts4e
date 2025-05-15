package io.javaside.ts4e.core;

import io.javaside.ts4e.core.internal.DocumentManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClasspathDocumentLoaderTest {

    private DocumentManager manager;

    @BeforeEach
    void setup() {
        manager = new DocumentManagerImpl();
    }

    @Test
    void testRegisterDocument() {
        assertNotNull(registerDocument());
    }

    @Test
    void testGetDocument() {
        var document = registerDocument();
        assertEquals(document, manager.getDocument(document.getDescriptor()));
    }

    TSDocument registerDocument() {
        return manager.registerDocumentFromClassPath(
                "/sample.dts", "nonamed", "sample", "device-tree");
    }
}
