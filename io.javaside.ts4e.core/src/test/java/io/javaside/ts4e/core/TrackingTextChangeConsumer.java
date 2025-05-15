package io.javaside.ts4e.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackingTextChangeConsumer implements TextChangeConsumer {

    private final List<BufferChange> changes = new ArrayList<>();

    @Override
    public void accept(BufferChange change) {
        changes.add(change);
    }

    public List<BufferChange> getChanges() {
        return Collections.unmodifiableList(changes);
    }

    public BufferChange getFirstChange() {
        return changes.getFirst();
    }

    public BufferChange getLastChange() {
        return changes.getLast();
    }
}
