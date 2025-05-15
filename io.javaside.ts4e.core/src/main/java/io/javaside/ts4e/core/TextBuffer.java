package io.javaside.ts4e.core;

import io.javaside.treesitter4j.Point;
import org.jspecify.annotations.Nullable;

public interface TextBuffer {

    long getModificationStamp();

    @Nullable String getText(int offset, Point point);

    @Nullable String getText();

    @Nullable SerialScheduler getChangeScheduler();

    void addChangeConsumer(BufferChangeConsumer consumer);
}
