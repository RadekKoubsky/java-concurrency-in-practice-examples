package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter6.taskexecution.executorframework;

import java.util.concurrent.Executor;

/**
 * Executor that starts a new thread for each task
 *
 * We can substitute hardcoded task execution in ThreadPerTaskWebServer
 * by this task executor implementation that creates a new thread for each
 * request.
 */
public class ThreadPerTaskExecutor implements Executor {
    @Override
    public void execute(final Runnable r) {
        new Thread(r).start();
    };
}
