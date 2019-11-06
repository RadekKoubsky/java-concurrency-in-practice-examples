package org.rkoubsky.jcip.part4.advancedtopics.chapter13.explicitlocks.reentrantlock.lockacquisition.timed;

import org.rkoubsky.jcip.part4.advancedtopics.chapter13.explicitlocks.reentrantlock.lockacquisition.interruptible.InterruptibleLocking;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Locking with a time budget
 *
 * We saw one way to ensure serialized access to a resource in section 9.5:
 * a single-threaded executor. Another approach is to use  an exclusive lock
 * to guard access to the resource.
 *
 * TimedLocking tries to send a message on a shared communications line guarded
 * by a Lock, but fails gracefully is it cannot do so within its time budget.
 */
public class TimedLocking {
    private Lock lock = new ReentrantLock();

    public boolean trySendOnSharedLine(final String message, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        final long nanosToLock = unit.toNanos(timeout) - this.estimatedNanosToSend(message);
        /**
         * The timed "tryLock" makes it practical to incorporate exclusive locking into
         * such a time-limited activity.
         *
         * The timed tryLock is also responsive to interruption when you need both
         * timed and interruptible lock acquisition. (see {@link InterruptibleLocking})
         */
        if (!this.lock.tryLock(nanosToLock, NANOSECONDS)) {
            return false;
        }
        try {
            return this.sendOnSharedLine(message);
        } finally {
            this.lock.unlock();
        }
    }

    private boolean sendOnSharedLine(final String message) {
        /* send something */
        return true;
    }

    long estimatedNanosToSend(final String message) {
        return message.length();
    }
}
