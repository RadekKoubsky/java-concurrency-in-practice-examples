package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.cancellation.interruption;

import java.util.concurrent.BlockingQueue;

/**
 * Noncancelable task that restores interruption before exit
 *
 * Activities that do not support cancellation but still call interruptible
 * blocking methods will have to call them in a loop, retrying when interruption
 * is detected. In this case they should save the interruption status locally
 * and restore it just before returning rather than immediately upon catching
 * InterruptedException.
 *
 * WARN: Setting
 */
public class NoncancelableTask {
    public Task getNextTask(final BlockingQueue<Task> queue) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return queue.take();
                } catch (final InterruptedException e) {
                    /**
                     * WARN:
                     * Setting the interrupted status too early could result in
                     * an infinite loop, because most interruptible blocking methods check the interrupted
                     * status on entry and throw InterruptedException immediately  if it is set.
                     *
                     * Calling Thread.currentThread().interrupt() here would set the status=true which
                     * would cause calls to queue.take to throw InterruptedException in the next
                     * rounds in the loop forever.
                     */
                    interrupted = true;
                    // fall through and retry
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    interface Task {
    }
}
