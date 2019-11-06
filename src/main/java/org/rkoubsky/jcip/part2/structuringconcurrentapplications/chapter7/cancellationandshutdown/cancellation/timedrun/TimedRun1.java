package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.cancellation.timedrun;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Scheduling an interrupt on a borrowed thread. Don't do this.
 *
 * TimedRun1 shows an attempt at running an arbitrary Runnable for a
 * given amount of time.
 *
 * It runs the task in the calling thread and schedules a cancellation
 * task to interrupt it after a given time interval. This addresses the
 * problem of unchecked exceptions thrown from the task, since they can
 * then be caught by the caller of "timedRun"
 */
public class TimedRun1 {
    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool(1);

    /**
     * A caller of timedRun can catch unchecked exception thrown by
     * arbitrary Runnable.
     *
     * This is appealingly simple approach but it violates the rules:
     * you should know a thread's interruption policy before interrupting it.
     *
     */
    public static void timedRun(final Runnable r,
            final long timeout, final TimeUnit unit) {
        final Thread taskThread = Thread.currentThread();
        cancelExec.schedule(new Runnable() {
            @Override
            public void run() {
                taskThread.interrupt();
            }
        }, timeout, unit);
        r.run();
        /**
         * WARN: Leaked interruption call to a caller of timedRun.
         *
         * If the r.run() completes before the timeout, the cancellation task
         * that interrupts the thread in which timedRun was called could go off
         * after timedRun has returned to its caller. We con't know what code
         * will be running when that happens, but the result won't be good.
         *
         * The {@link TimedRun2} addresses the exception-handling of aSecondOfPrimes
         * and the problems with this broken attempt.
         */
    }
}