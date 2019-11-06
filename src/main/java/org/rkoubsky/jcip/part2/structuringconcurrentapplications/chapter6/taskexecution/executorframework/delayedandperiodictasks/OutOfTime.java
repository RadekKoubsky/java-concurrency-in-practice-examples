package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter6.taskexecution.executorframework.delayedandperiodictasks;

import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Class illustrating confusing Timer behavior
 *
 * A Timer create only a single thread for executing timer tasks.
 *
 * <NOTE_thread_leakage>
 *
 *     Another problem with Timer is that it behaves poorly if
 *     a TimerTask throws an unchecked exception. The Timer thread
 *     does not catch the exception, so an unchecked exception thrown
 *     from a TimerTask terminates the timer thread. Timer also doesn't
 *     resurrect the thread  in this situation; instead, it errorneously
 *     assumes the entire Timer was cancelled. In this case, TimerTasks
 *     that are already scheduled but not yet executed are never run.
 *     And new tasks cannot be scheduled.
 *
 * </NOTE_thread_leakage>
 *
 * OutOfTime illustrates the "thread leakage". You might expect the program
 * to run for 6 seconds and exit, but what actually happens is that it
 * terminates after one second with IllegalStateException whose message text
 * is "Timer already cancelled".
 *
 * ScheduleThreadExecutor deals properly with ill behaved tasks; there is little
 * reason to use Timer in Java 5 or later.
 *
 * If you need to build your own scheduling service, you may still be able to
 * take advantage of the library by using DelayQueue, a BlockingQueue implementation
 * that provides the scheduling functionality of ScheduleThreadExecutor.
 */

public class OutOfTime {
    public static void main(final String[] args) throws Exception {
        final Timer timer = new Timer();
        timer.schedule(new ThrowTask(), 1);
        SECONDS.sleep(1);
        timer.schedule(new ThrowTask(), 1);
        SECONDS.sleep(5);
    }

    static class ThrowTask extends TimerTask {
        @Override
        public void run() {
            throw new RuntimeException();
        }
    }
}
