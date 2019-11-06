package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter8.threadpools.parallelizingrecursivealgorithms.slidingblockpuzzle.concurrent;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.CountDownLatch;

/**
 * Result-bearing latch used by ConcurrentPuzzleSolver
 *
 * ValueLatch provides a way to hold a value such that only the first
 * call actually sets the value, callers can test whether it has been
 * set, and callers can block waiting for it to be set.
 */
@ThreadSafe
public class ValueLatch <T> {
    @GuardedBy("this") private T value = null;
    private final CountDownLatch done = new CountDownLatch(1);

    public boolean isSet() {
        return (this.done.getCount() == 0);
    }
    public synchronized void setValue(final T newValue) {
        if (!this.isSet()) {
            this.value = newValue;
            this.done.countDown();
        }
    }

    public T getValue() throws InterruptedException {
        this.done.await();
        synchronized (this) {
            return this.value;
        }
    }
}
