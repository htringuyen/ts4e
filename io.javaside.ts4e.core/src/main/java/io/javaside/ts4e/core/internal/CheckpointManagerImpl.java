package io.javaside.ts4e.core.internal;

import io.javaside.ts4e.core.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class CheckpointManagerImpl implements CheckpointManager {

    private final Map<DocumentDescriptor, Checkpoints> checkpointsMap;

    public void registerDocument(TSDocument document) {

    }

    public DocumentCheckpoint newCheckpointFor(TSDocument document) {

    }

    static class Checkpoints {

        private Queue<AtomicBoolean> validationFlags = new LinkedList<>();

        private TSDocument document;

        private ReentrantLock lock;

        private SerialScheduler scheduler;

        private ExecutorService asyncExecutor;

        private Checkpoints(TSDocument document) {
            document.getTextBuffer().addChangeConsumer(this::onBufferChanged);
        }

        private void onBufferChanged(BufferChange change) {
            try {
                lock.lock();
                for (var i = 0; i < validationFlags.size(); i++) {
                    validationFlags.poll().set(false);
                }
            }
            finally {
                lock.unlock();
            }
        }

        private DocumentCheckpoint createCheckpoint() {

        }

        final class ValidationFlag {

            private boolean value = true;

            private Runnable invalidatedCallback;

            void invalidate() {
                try {
                    lock.lock();
                    this.value = false;
                    if (invalidatedCallback != null) {
                        asyncExecutor.submit(invalidatedCallback);
                    }
                }
                finally {
                    lock.unlock();
                }
            }

            boolean get() {
                try {
                    lock.lock();
                    return value;
                }
                finally {
                    lock.unlock();
                }
            }

            void setInvalidatedCallback(Runnable callback) {
                asyncExecutor.submit(callback);
            }
        }
    }

    static class DocumentCheckpointImpl implements DocumentCheckpoint {

        private final Checkpoints.ValidationFlag validationFlag;

        private final long modificationStamp;

        private final SerialScheduler defaultScheduler;

        private final ExecutorService asyncExecutor;

        private final List<Runnable> invalidatedCallbacks = new ArrayList<>();

        DocumentCheckpointImpl(Checkpoints.ValidationFlag validationFlag, long modificationStamp,
                                      SerialScheduler defaultScheduler, ExecutorService asyncExecutor) {
            this.validationFlag = validationFlag;
            this.modificationStamp = modificationStamp;
            this.defaultScheduler = defaultScheduler;
            this.asyncExecutor = asyncExecutor;
            validationFlag.setInvalidatedCallback(() -> {
                invalidatedCallbacks.forEach(Runnable::run);
            });
        }

        @Override
        public boolean isValid() {
            return validationFlag.get();
        }

        @Override
        public long getModificationStamp() {
            return modificationStamp;
        }

        @Override
        public DocumentCheckpoint addInvalidationCallback(Runnable runnable) {
            invalidatedCallbacks.add(runnable);
            return this;
        }

        @Override
        public <V> CheckpointTask<V> execute(Callable<V> callable) {

        }


    }

    static class CheckpointTaskImpl<V> implements DocumentCheckpoint.CheckpointTask<V>, Runnable {

    }


}


























