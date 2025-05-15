package io.javaside.ts4e.core;

import io.javaside.ts4e.core.internal.InMemoryTextBuffer;
import io.javaside.ts4e.core.old.TSDocumentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TSDocumentImplTest {

    private static final Logger logger = LoggerFactory.getLogger(TSDocumentImplTest.class);

    private TextBuffer buffer;

    private TSDocument document;

    private TrackingTSListener listener;

    @BeforeEach
    void setup() {
        buffer = new InMemoryTextBuffer(TextSamples.dtsPath);
        var descriptor = new DocumentDescriptor("nonamed", "sample.dts", "device-tree");
        document = new TSDocumentImpl(descriptor, buffer);
        listener = new TrackingTSListener();
        document.addTSListener(listener);
    }

    @Test
    void testInitializedTree() {
        var tree = listener.awaitGetInitTree();
        assertNotNull(tree);
        logger.info(tree.toString());
    }
}
