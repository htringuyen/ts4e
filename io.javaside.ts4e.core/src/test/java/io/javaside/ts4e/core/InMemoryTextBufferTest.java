package io.javaside.ts4e.core;

import io.javaside.treesitter4j.Point;
import io.javaside.ts4e.core.internal.InMemoryTextBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryTextBufferTest {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryTextBufferTest.class);

    private String source;

    private Path path;

    private InMemoryTextBuffer buffer;

    private TrackingTextChangeConsumer consumer;

    @BeforeEach
    void setup() {
        try {
            var uri = TextSamples.class.getResource("/nsequence.txt").toURI();
            path = Paths.get(uri);
            source = Files.readString(path);
        }
        catch (IOException | URISyntaxException e) {
            throw new RuntimeException();
        }

        buffer = new InMemoryTextBuffer(path);
        consumer = new TrackingTextChangeConsumer();
        buffer.addChangeConsumer(consumer);
        assertNotNull(buffer);
    }


    @Test
    void testGetText() {
        assertEquals(source, buffer.getText());
        logger.info("\n" + source);
    }

    @Test
    void testGetTextUsingPositions() {
        var offsets = new int[]
                {22};
        var points = new Point[]
                {new Point(4, 8)};
        var expectedTexts = new String[] {
                "3abcdefgh\r\n" + "4abcdefgh\r\n" + "5abcdefgh"
        };

        for (var i = 0; i < offsets.length; i++) {
            var offset = offsets[i];
            var point = points[i];
            var expectedText = expectedTexts[i];
            var readText = buffer.getText(offset, point);
            logger.info("\n[" + readText + "]");
            assertEquals(expectedText, readText);
        }
    }

    @Test
    void testChangeConsuming() {
        var eolSep = System.lineSeparator();
        var eolSize = System.lineSeparator().length();
        var newText = source.replace("abcdefgh" + eolSep +
                "4abcdefgh" + eolSep +
                "5abcdefg", "");

        var lineSize = 9 + eolSize;

        var expectedChange =
                new BufferChange(-1,
                        2 * lineSize + 1,
                        4 * lineSize + 7,
                        2 * lineSize + 1,
                        new Point(2, 1),
                        new Point(4, 7),
                        new Point(2, 1)
                        );

        buffer.setText(newText);

        var change = consumer.getLastChange();
        assertNotNull(change);
        logger.info(change.toString());
    }
}







