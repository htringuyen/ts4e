package io.javaside.ts4e.core;

import io.javaside.treesitter4j.ParseCallback;
import io.javaside.treesitter4j.Point;
import io.javaside.ts4e.core.internal.InMemoryTextBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HitCountParserCallback implements ParseCallback {

    private static final Logger logger = LoggerFactory.getLogger(HitCountParserCallback.class);

    private int hitCount = 0;

    private final TextBuffer buffer;

    public HitCountParserCallback(String text) {
        buffer = new InMemoryTextBuffer(text);
    }

    @Override
    public String apply(Integer offset, Point point) {
        hitCount++;
        var result = buffer.getText(offset, point);
        logger.info("offset={}, point={}, text={}", offset, point, result);
        return result;
    }

    public int getHitCount() {
        return hitCount;
    }
}
