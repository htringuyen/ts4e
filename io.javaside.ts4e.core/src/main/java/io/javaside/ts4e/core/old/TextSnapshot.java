package io.javaside.ts4e.core.old;

import io.javaside.treesitter4j.Point;
import org.jspecify.annotations.Nullable;

public interface TextSnapshot {

    String getText(int offset, @Nullable Point point) throws StalenessSnapshotException;

    long getTimestamp();

}
