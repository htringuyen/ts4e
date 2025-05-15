package io.javaside.ts4e.core;

import io.javaside.treesitter4j.BuiltinLanguageLibrary;
import io.javaside.ts4e.core.internal.SimpleParserManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SimpleParserManagerTest {
    @Test
    void testGetParser() {
        var pDesc = new ParserDescriptor("device-tree", BuiltinLanguageLibrary.TS_DEVICETREE);
        var parser = SimpleParserManager.getInstance().getParser(pDesc);
        assertNotNull(parser);
    }
}
