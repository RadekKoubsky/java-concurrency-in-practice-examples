package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.delegatingthreadsafety.publishingstatevariables;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Mutable, but thread safe Point.
 */
@ThreadSafe
public class SafePoint {
    @GuardedBy("this") private int x, y;

    private SafePoint(final int[] a) {
        this(a[0], a[1]);
    }

    public SafePoint(final SafePoint p) {
        this(p.get());
    }

    public SafePoint(final int x, final int y) {
        this.set(x, y);
    }

    /**
     * Getter that retrieves both "x" and "y" values at once.
     *
     * If we provided separate getters for "x" and "y", then
     * the values could change between the time one coordinate
     * is retrieved and the other, resulting in a caller seeing
     * an inconsistent value: an (x, y) location where the vehicle
     * never was
     */
    public synchronized int[] get() {
        return new int[]{this.x, this.y};
    }

    public synchronized void set(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
}
