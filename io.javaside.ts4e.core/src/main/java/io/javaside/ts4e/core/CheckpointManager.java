package io.javaside.ts4e.core;

public interface CheckpointManager {

    void registerDocument(TSDocument document);

    DocumentCheckpoint newCheckpointFor(TSDocument document);
}
