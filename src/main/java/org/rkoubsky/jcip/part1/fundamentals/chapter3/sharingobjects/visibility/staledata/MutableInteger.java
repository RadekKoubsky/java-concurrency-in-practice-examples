package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.visibility.staledata;

import net.jcip.annotations.NotThreadSafe;

/**
 * This mutable integer is not thread-safe.
 *
 * If one thread calls the "set" method, the other threads calling "get" method
 * may or may not see that update.
 */
@NotThreadSafe
public class MutableInteger {
    private int value;

    public int get() {
        return this.value;
    }

    public void set(final int value) {
        this.value = value;
    }
}
