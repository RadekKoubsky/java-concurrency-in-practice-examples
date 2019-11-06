package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.saturationpolicies;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * Using a Semaphore to throttle task submission
 *
 * There is no predefined saturation policy in default implementations of
 * link {@link java.util.concurrent.RejectedExecutionHandler} to make
 * the "execute" method block when the work queue is full.
 *
 * However, the same effect can be accomplished by using a Semaphore to bound
 * the task injection rate.
 *
 * In this approach, use an unbounded queue (there's no reason to bound both
 * the queue size and injection rate) and set the bound on the semaphore to
 * be equal to the pool size plus the number of queued tasks you want to allow,
 * since the semaphore is bounding the number of tasks both currently executing
 * and awaiting execution.
 */
@ThreadSafe
public class BoundedExecutor {
    private final Executor exec;
    private final Semaphore semaphore;

    public BoundedExecutor(final Executor exec, final int bound) {
        this.exec = exec;
        /**
         * set the bound on the semaphore to be equal to
         * the pool size plus the number of queued tasks you want to allow
         */
        this.semaphore = new Semaphore(bound);
    }

    public void submitTask(final Runnable command)
            throws InterruptedException {
        this.semaphore.acquire();
        try {
            this.exec.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        command.run();
                    } finally {
                        BoundedExecutor.this.semaphore.release();
                    }
                }
            });
        } catch (final RejectedExecutionException e) {
            this.semaphore.release();
        }
    }
}
