package io.javaside.ts4e.core;

import java.nio.Buffer;
import java.util.List;
import java.util.Queue;

public interface TreeUpdateStrategy {

    boolean shouldUpdate(Queue<BufferChange> changes);
}
