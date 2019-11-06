package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.extendingthreadpoolexecutor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread pool extended with logging and timing
 *
 * TimingThreadPool shows a custom thread pool that uses
 * beforeExecute, afterExecute, and "terminated" to add
 * logging and statistics gathering.
 */
@Slf4j
public class TimingThreadPool extends ThreadPoolExecutor {

    public TimingThreadPool() {
        super(2, 2, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    /**
     * To measure a task's runtime, beforeExecute must record
     * the start time and store it somewhere afterExecute can find
     * it. Because execution hooks are called in the the thread that
     * executes the task, a value placed in a ThreadLocal by beforeExecute
     * can be retrieved by afterExecute.
     */
    private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        super.beforeExecute(t, r);
        log.debug("Thread {}: start {}s", t, r.getClass().getSimpleName());
        // set the ThreadLocal value of currently executing pool thread
        this.startTime.set(System.nanoTime());
    }

    @Override
    protected void afterExecute(final Runnable r, final Throwable t) {
        try {
            final long endTime = System.nanoTime();
            // read the ThreadLocal value set by currently executing pool thread in "beforeExecute" above
            final long taskTime = endTime - this.startTime.get();
            this.numTasks.incrementAndGet();
            this.totalTime.addAndGet(taskTime);
            log.debug("Thread {}: end {}, time={}}", Thread.currentThread().getName(), r
                    .getClass().getSimpleName(), taskTime);
        } finally {
            super.afterExecute(r, t);
        }
    }

    @Override
    protected void terminated() {
        try {
            log.info("Terminated: avg time={}ns", this.totalTime.get() / this.numTasks.get());
        } finally {
            super.terminated();
        }
    }
}
