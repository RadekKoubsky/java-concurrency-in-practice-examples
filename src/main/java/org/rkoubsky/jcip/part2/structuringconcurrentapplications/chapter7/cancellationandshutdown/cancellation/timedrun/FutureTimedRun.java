package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.cancellation.timedrun;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizers.futuretask.LaunderThrowable.launderThrowable;

/**
 * Cancelling a task using Future
 *
 * We have already used an abstraction for managing the lifecycle of a task,
 * dealing with exceptions, and facilitating cancellation - Future.
 *
 * Following the general principle that it is better to use existing library classes
 * than build your own, let's build timedRun using Future and the task executor framework.
 *
 * <NOTE_future_cancel>
 *
 *     When Future.get throws InterruptedException or TimeoutException and
 *     you know that the result is no longer needed by the program, cancel
 *     the task with Future.cancel.
 *
 * </NOTE_future_cancel>
 */
public class FutureTimedRun {
    private static final ExecutorService taskExec = Executors.newCachedThreadPool();

    public static void timedRun(final Runnable r,
            final long timeout, final TimeUnit unit)
            throws InterruptedException {
        final Future<?> task = taskExec.submit(r);
        try {
            task.get(timeout, unit);
        } catch (final TimeoutException e) {
            // task will be cancelled below
        } catch (final ExecutionException e) {
            // exception thrown in task; rethrow
            throw launderThrowable(e.getCause());
        } finally {
            // Harmless if task already completed
            /**
             * The cancel method returns a value indicating whether the cancellation
             * attempt was successful (This tells you only whether it was able to deliver
             * the interruption, no whether the task detected and acted on it)
             *
             * Setting the argument to "false" means "don't run this task if it hasn't started
             * yet".
             *
             * <NOTE_when_to_call_cancel_with_true>
             *
             *     Since you shouldn't interrupt a thread unless you know its
             *     interruption policy, when is it OK to call cancel with "mayInterruptIfRunning"
             *     argument equal to true?
             *
             *     The task execution threads created by the standard Executor implementations
             *     implement an interruption policy that lets task be cancelled using iterruption,
             *     si it is safe to set "mayInterruptIfRunning" to true when cancelling tasks through
             *     Futures when they are running in a standard Executor.
             *
             *     You should not interrupt a pool thread directly when attempting to cancel
             *     a task, because you won't know what task is running when the interrupt request
             *     is delivered - do this only through the task's Future. This is yet another reason
             *     to code tasks to treat interruption as a cancellation request: then they can
             *     be cancelled through their Futures.
             * </NOTE_when_to_call_cancel_with_true>
             */
            task.cancel(true); // interrupt if running
        }
    }
}
