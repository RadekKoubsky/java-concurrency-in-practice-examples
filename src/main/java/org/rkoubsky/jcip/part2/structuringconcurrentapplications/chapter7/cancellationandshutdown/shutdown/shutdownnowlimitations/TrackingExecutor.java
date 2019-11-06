package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.shutdown.shutdownnowlimitations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorService that keeps track of cancelled tasks after shutdown
 *
 * TrackingExecutor shows a technique for determining which tasks were in progress
 * at shutdown time (as there is no way of knowing the state of the tasks in progress
 * at shutdown time unless the tasks themselves perform some sort of checkpointing).
 *
 * By encapsulating an ExecutorService and instrumenting "execute" (and similarly submit)
 * to remember which tasks were cancelled after shutdown, TrackingExecutor can identify
 * which tasks started but did not complete normally.
 *
 *
 */
public class TrackingExecutor extends AbstractExecutorService {
    private final ExecutorService exec;
    private final Set<Runnable> tasksCancelledAtShutdown =
            Collections.synchronizedSet(new HashSet<>());

    public TrackingExecutor(final ExecutorService exec) {
        this.exec = exec;
    }

    @Override
    public void shutdown() {
        this.exec.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.exec.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.exec.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.exec.isTerminated();
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return this.exec.awaitTermination(timeout, unit);
    }

    /**
     * After the executor terminates, getCancelledTasks returns the list
     * of cancelled tasks. In order for this technique to work, the tasks
     * must preserve the thread's interrupted status when they return,
     * which well behaved tasks will do anyway.
     */
    public List<Runnable> getCancelledTasks() {
        if (!this.exec.isTerminated()) {
            throw new IllegalStateException(/*...*/);
        }
        return new ArrayList<>(this.tasksCancelledAtShutdown);
    }

    @Override
    public void execute(final Runnable runnable) {
        this.exec.execute(this.instrumentTask(runnable));
    }

    @Override
    public Future<?> submit(final Runnable task) {
        return this.exec.submit(this.instrumentTask(task));
    }

    private Runnable instrumentTask(final Runnable task) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } finally {
                    if (TrackingExecutor.this.isShutdown()
                            && Thread.currentThread()
                                     .isInterrupted()) {
                        TrackingExecutor.this.tasksCancelledAtShutdown.add(task);
                    }
                }
            }
        };
    }
}
