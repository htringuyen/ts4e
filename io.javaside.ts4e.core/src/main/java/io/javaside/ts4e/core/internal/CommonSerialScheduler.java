package io.javaside.ts4e.core.internal;

import io.javaside.ts4e.core.SerialScheduler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

public final class CommonSerialScheduler implements SerialScheduler {

    private final ReentrantLock lock = new ReentrantLock();

    private final ExecutorService executor;

    private boolean isRunning = false;

    private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

    public CommonSerialScheduler(ExecutorService executor) {
        this.executor = executor;
    }

    private Runnable wrapTask(Runnable task) {
        return () -> {
            try {
                task.run();
            }
            finally {
                try {
                    lock.lock();
                    if (!taskQueue.isEmpty()) {
                        submit(taskQueue.poll());
                    }
                }
                finally {
                    lock.unlock();
                }
            }
        };
    }

    @Override
    public void submit(Runnable task) {
        taskQueue.add(task);
        try {
            lock.lock();
            if (!isRunning) {
                isRunning = true;
                executor.submit(wrapTask(task));
            }
        }
        finally {
            lock.unlock();
        }
    }
}
