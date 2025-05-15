package io.javaside.ts4e.core;

import org.jspecify.annotations.NonNull;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public interface DocumentCheckpoint {

    ScopedValue<DocumentCheckpoint> SCOPED_CHECKPOINT = ScopedValue.newInstance();

    static boolean isBound() {
        return SCOPED_CHECKPOINT.isBound();
    }

    static @NonNull DocumentCheckpoint boundCheckpoint() {
        if (!SCOPED_CHECKPOINT.isBound()) {
            throw new NoSuchElementException();
        }
        return SCOPED_CHECKPOINT.get();
    }

    boolean isValid();

    DocumentCheckpoint addInvalidationCallback(@NonNull Runnable runnable);

    @NonNull CheckpointTask<Void> execute(Runnable runnable);

    @NonNull CheckpointTask<Void> executeAsync(Runnable runnable);

    <V> @NonNull CheckpointTask<V> execute(@NonNull Callable<V> consumer);

    <V> @NonNull CheckpointTask<V> executeAsync(@NonNull Callable<V> task);

    interface CheckpointTask<V> {

        CheckpointTask<V> onFailed(Runnable runnable);

        CheckpointTask<V> thenConsume(Consumer<V> consumer);

        CheckpointTask<V> thenConsumeSerially(Consumer<V> consumer);

        CheckpointTask<V> thenConsumeSerially(Consumer<V> consumer, SerialScheduler scheduler);
    }
}
