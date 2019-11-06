package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter12.testingconcurrentprograms.testingforcorrectness.usingcallbacks;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testing thread pool expansion
 *
 * <NOTE_callbacks>
 *
 *     Callbacks to client-provided code can be helpful in constructing test cases;
 *     callbacks are often made at known points in an object's lifecycle that are good
 *     opportunities to assert invariants. For example, {@link ThreadPoolExecutor} makes
 *     calls to the task Runnables and to the {@link ThreadFactory}
 *
 * </NOTE_callbacks>
 */
public class TestThreadPool {
    private final TestingThreadFactory threadFactory = new TestingThreadFactory();

    /**
     * If the core pool size is smaller than the maximum size, the thread pool should
     * grow as demand for execution increases.
     *
     * Submitting long-running tasks to the pool makes the number of executing tasks
     * stay constant for long enough to make a few assertions, such as testing that
     * the pool is expanded as expected.
     */
    @Test
    public void testPoolExpansion() throws InterruptedException {
        final int CORE_SIZE = 10;
        final int MAX_SIZE = CORE_SIZE + 10;
        final ExecutorService exec = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE,
                                                            0L, TimeUnit.MILLISECONDS,
                                                            new LinkedBlockingQueue<Runnable>(),
                                                            this.threadFactory);

        for (int i = 0; i < 10 * CORE_SIZE; i++) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        for (int i = 0;
             i < 20 && this.threadFactory.numCreated.get() < CORE_SIZE;
             i++) {
            Thread.sleep(100);
        }
        assertThat(this.threadFactory.numCreated.get()).isEqualTo(CORE_SIZE);
        exec.shutdownNow();
    }
}

/**
 * We can instrument thread creation by using a custom thread factory.
 *
 * TestingThreadFactory maintains a count of created threads; test cases
 * can verify the number of threads created during a test run.
 *
 * TestingThreadFactory could be extended to return a custom Thread that
 * also records when the thread terminates, so that test cases can verify
 * that threads are reaped in accordance with the execution policy.
 */
class TestingThreadFactory implements ThreadFactory {
    public final AtomicInteger numCreated = new AtomicInteger();
    private final ThreadFactory factory = Executors.defaultThreadFactory();

    @Override
    public Thread newThread(final Runnable r) {
        this.numCreated.incrementAndGet();
        return this.factory.newThread(r);
    }
}
