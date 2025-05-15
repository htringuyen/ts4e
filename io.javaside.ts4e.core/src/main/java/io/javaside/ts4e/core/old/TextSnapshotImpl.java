package io.javaside.ts4e.core.old;

import io.javaside.treesitter4j.Point;
import io.javaside.ts4e.core.TextBuffer;

class TextSnapshotImpl implements TextSnapshot {

    private final long snapshotStamp;

    private final TextBuffer buffer;

    public TextSnapshotImpl(long snapshotStamp, TextBuffer buffer) {
        this.snapshotStamp = snapshotStamp;
        this.buffer = buffer;
    }

    @Override
    public String getText(int offset, Point point) throws StalenessSnapshotException {
        if (snapshotStamp != buffer.getModificationStamp()) {
            throw new StalenessSnapshotException("Snapshot was invalid to read");
        }
        return buffer.getText(offset, point);
    }

    @Override
    public long getTimestamp() {
        return snapshotStamp;
    }
}
