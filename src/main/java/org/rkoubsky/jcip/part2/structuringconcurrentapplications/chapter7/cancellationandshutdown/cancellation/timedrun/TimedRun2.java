package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.cancellation.timedrun;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizers.futuretask.LaunderThrowable.launderThrowable;

/**
 * Interrupting a task in a dedicated thread
 *
 * This version addresses the problems in the previous examples {@link TimedRun1}, but
 * because it relies on a timed "join", it shares a deficiency with "join": we don't
 * know if control was returned because the thread exited normally or because the
 * "join" timed out.
 */
public class TimedRun2 {
    private static final ScheduledExecutorService cancelExec = newScheduledThreadPool(1);

    public static void timedRun(final Runnable r,
            final long timeout, final TimeUnit unit)
            throws InterruptedException {
        class RethrowableTask implements Runnable {
            /**
             * The saved Throwable is shared between the two threads,
             * and so is declared "volatile" to safely publish it from
             * the taskThread to the timedRun thread.
             */
            private volatile Throwable t;

            @Override
            public void run() {
                try {
                    r.run();
                } catch (final Throwable t) {
                    this.t = t;
                }
            }

            void rethrow() {
                if (this.t != null) {
                    throw launderThrowable(this.t);
                }
            }
        }

        /**
         * The thread created to to run the task can have its
         * own execution policy, and even if the task doesn't
         * respond to the interrupt, the timed run method can still
         * return to its caller.
         *
         */
        final RethrowableTask task = new RethrowableTask();
        final Thread taskThread = new Thread(task);
        taskThread.start();
        cancelExec.schedule(new Runnable() {
            @Override
            public void run() {
                taskThread.interrupt();
            }
        }, timeout, unit);
        /**
         * After starting the task thread, timedRun executes a timed "join" with
         * the newly created thread.
         */
        taskThread.join(unit.toMillis(timeout));
        /**
         * After "join" returns, it checks if an exception was thrown from the task
         * and if so, rethrows it in the thread calling timedRun.
         */
        task.rethrow();
    }
}
