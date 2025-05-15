package io.javaside.ts4e.core.old;

import io.javaside.treesitter4j.*;
import io.javaside.ts4e.core.BufferChange;
import io.javaside.ts4e.core.DocumentDescriptor;
import io.javaside.ts4e.core.TextBuffer;
import io.javaside.ts4e.core.internal.SimpleParserManager;
import io.javaside.ts4e.core.internal.CommonSerialScheduler;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class TSDocumentImpl {

    private final Parser parser;

    private final DocumentDescriptor descriptor;

    private final TextBuffer buffer;

    private final AtomicReference<Tree> syntaxTree;

    private final List<TSStrategy> strategies;

    private final List<TSListener> initializedListeners = new ArrayList<>();

    private final List<TSListener> waitingListeners = new ArrayList<>();

    private final List<BufferChange> changes = Collections.synchronizedList(new LinkedList<>());

    private final CommonSerialScheduler taskScheduler = new CommonSerialScheduler(Executors.newSingleThreadExecutor());

    TSDocumentImpl(DocumentDescriptor descriptor, TextBuffer buffer) {
        this.descriptor = descriptor;
        this.buffer = buffer;
        var pDesc = SimpleParserManager.getInstance()
                .findParserByLanguage(descriptor.language())
                .getFirst();
        this.parser = SimpleParserManager.getInstance().getParser(pDesc);
        this.syntaxTree = new AtomicReference<>(null);
        this.strategies = new ArrayList<>();
        strategies.add(new DefaultTSStrategy());
        taskScheduler.submit(new ParsingTask());
    }

    private void handleBufferChange(BufferChange change) {
        changes.add(change);
        var shouldTriggerParsing = strategies.stream()
                .map(stg -> stg.shouldTriggerParsing(changes))
                .reduce(false, (result, stg) -> result || stg);
        if (shouldTriggerParsing) {
            taskScheduler.submit(new ParsingTask());
        }
    }

    @Override
    public TextBuffer getTextBuffer() {
        return buffer;
    }

    @Override
    public void addTSListener(TSListener listener) {
        waitingListeners.add(listener);
        taskScheduler.submit(this::initializeListeners);
    }

    @Override
    public DocumentDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void addTSStrategy(TSStrategy strategy) {
        strategies.add(strategy);
    }

    private static class DefaultTSStrategy implements TSStrategy {
        @Override
        public boolean shouldTriggerParsing(List<BufferChange> changes) {
            return !changes.isEmpty();
        }
    }

    private void initializeListeners() {
        waitingListeners.forEach(l -> l.initialized(syntaxTree.get()));
        initializedListeners.addAll(waitingListeners);
        waitingListeners.clear();
    }

    private class ParsingTask implements Runnable {

        private final TextSnapshot snapshot;

        private final int appliedChangeCount;

        private boolean cancelTriggered = false;

        private ParsingTask() {
            snapshot = buffer.takeSnapshot();
            appliedChangeCount = changes.size();
        }

        @Override
        public void run() {
            applyChanges();
            var oldTree = syntaxTree.get();
            var newTree = parser.parse(this::readSnapshot, InputEncoding.UTF_8, syntaxTree.get(),
                    new Parser.Options(this::shouldCancelParsing))
                    .orElse(null);
            if (!cancelTriggered) {
                syntaxTree.set(newTree);
                initializedListeners.forEach(l -> l.syntaxTreeUpdated(oldTree, newTree));
                initializeListeners();
            }
            clearAppliedChanges();
        }

        private String readSnapshot(Integer offset, Point point) {
            if (cancelTriggered) {
                return null;
            }

            try {
                return snapshot.getText(offset, point);
            }
            catch (StalenessSnapshotException e) {
                cancelTriggered = true;
                return null;
            }
        }

        private boolean shouldCancelParsing(Parser.State s) {
            return cancelTriggered;
        }

        private void clearAppliedChanges() {
            for (int i = 0; i < appliedChangeCount; i++) {
                changes.removeFirst();
            }
        }

        private void applyChanges() {
            var changesApplied = 0;
            for (var change : changes) {
                if (changesApplied <= appliedChangeCount) {
                    var edit = new InputEdit(change.startByte(), change.oldEndByte(), change.newEndByte(),
                            change.startPoint(), change.oldEndPoint(), change.newEndPoint());
                    syntaxTree.get().edit(edit);
                    changesApplied++;
                }
                else {
                    break;
                }
            }
        }

    }

}






























