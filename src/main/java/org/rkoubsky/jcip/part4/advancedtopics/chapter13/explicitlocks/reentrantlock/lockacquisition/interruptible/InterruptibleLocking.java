package org.rkoubsky.jcip.part4.advancedtopics.chapter13.explicitlocks.reentrantlock.lockacquisition.interruptible;

import org.rkoubsky.jcip.part4.advancedtopics.chapter13.explicitlocks.reentrantlock.lockacquisition.timed.TimedLocking;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * InterruptibleLocking
 *
 * Just as timed lock acquisition allows exclusive locking to be used
 * within timed-limited activities, interruptible lock acquisition allows
 * locking to be used within cancellable activities.
 *
 * This class uSses "lockInterruptibly" to implement sendOnSharedLine from {@link TimedLocking} so
 * that we can call it from a cancellable task. The timed tryLock is also responsive to
 * interruption when you need both timed and interruptible lock acquisition.
 */
public class InterruptibleLocking {
    private Lock lock = new ReentrantLock();

    public boolean sendOnSharedLine(final String message) throws InterruptedException {
        this.lock.lockInterruptibly();
        try {
            return this.cancellableSendOnSharedLine(message);
        } finally {
            this.lock.unlock();
        }
    }

    private boolean cancellableSendOnSharedLine(final String message) throws InterruptedException {
        /* send something */
        return true;
    }

}
