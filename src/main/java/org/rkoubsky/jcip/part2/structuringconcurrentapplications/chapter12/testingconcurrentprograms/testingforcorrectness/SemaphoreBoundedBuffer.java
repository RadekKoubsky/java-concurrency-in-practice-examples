package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter12.testingconcurrentprograms.testingforcorrectness;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

/**
 * Bounded buffer using \Semaphore
 *
 * SemaphoreBoundedBuffer implements a fixed-length array-based
 * queue with blocking "put" and "take" methods controlled by a pair
 * of counting semaphores.
 *
 * (In practice, if you need bounded buffer you should use {@link ArrayBlockingQueue}
 * or {@link LinkedBlockingDeque} rather than rolling your own, but the technique
 * used here illustrates how insertions and removals can be controlled in other
 * data structures as well)
 */
@ThreadSafe
public class SemaphoreBoundedBuffer <E> {
    private final Semaphore availableItems, availableSpaces;
    @GuardedBy("this") private final E[] items;
    @GuardedBy("this") private int putPosition = 0, takePosition = 0;

    public SemaphoreBoundedBuffer(final int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.availableItems = new Semaphore(0);
        this.availableSpaces = new Semaphore(capacity);
        this.items = (E[]) new Object[capacity];
    }

    public boolean isEmpty() {
        return this.availableItems.availablePermits() == 0;
    }

    public boolean isFull() {
        return this.availableSpaces.availablePermits() == 0;
    }

    public void put(final E x) throws InterruptedException {
        this.availableSpaces.acquire();
        this.doInsert(x);
        this.availableItems.release();
    }

    /**
     * Blocks until the buffer becomes nonempty (this.availableItems semaphore
     * released its only permit by calling "put" method)
     */
    public E take() throws InterruptedException {
        this.availableItems.acquire();
        final E item = this.doExtract();
        this.availableSpaces.release();
        return item;
    }

    private synchronized void doInsert(final E x) {
        int i = this.putPosition;
        this.items[i] = x;
        this.putPosition = (++i == this.items.length) ? 0 : i;
    }

    private synchronized E doExtract() {
        int i = this.takePosition;
        final E x = this.items[i];
        this.items[i] = null;
        this.takePosition = (++i == this.items.length) ? 0 : i;
        return x;
    }
}
