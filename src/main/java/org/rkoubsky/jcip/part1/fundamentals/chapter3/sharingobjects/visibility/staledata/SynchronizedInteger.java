package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.visibility.staledata;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * This integer is thread-safe because it uses synchronization for both the getter and setter.
 *
 * NOTE: Synchronizing only the setter would not be sufficient: threads calling the "get" method would
 * still be able to see stale values.
 */
@ThreadSafe
public class SynchronizedInteger {
    @GuardedBy("this") private int value;

    public synchronized int get() {
        return this.value;
    }

    public synchronized void set(final int value) {
        this.value = value;
    }
}
