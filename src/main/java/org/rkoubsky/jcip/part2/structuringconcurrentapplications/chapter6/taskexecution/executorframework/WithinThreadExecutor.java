package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter6.taskexecution.executorframework;

import java.util.concurrent.Executor;

/**
 * Executor that executes tasks synchronously in the calling thread
 *
 * We could make a single-threaded version of TaskExecutionWebServer using
 * this executor.
 */
public class WithinThreadExecutor implements Executor {
    @Override
    public void execute(final Runnable r) {
        r.run();
    };
}
