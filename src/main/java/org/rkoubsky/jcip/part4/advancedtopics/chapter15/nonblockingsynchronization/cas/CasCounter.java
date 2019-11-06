package org.rkoubsky.jcip.part4.advancedtopics.chapter15.nonblockingsynchronization.cas;

import net.jcip.annotations.ThreadSafe;

/**
 * Nonblocking counter using CAS
 *
 * CasCounter implements a thread-safe counter using CAS.
 * The increment operation follows the canonical form -
 * fetch the old value, transform, it to the new value
 * (adding one), and use CAS to set the new value. If the
 * CAS fails, the operation is immediately retried.
 *
 * Retrying repeatedly is usually a reasonable strategy,
 * although in cases of extreme contention it might be
 * desirable to wait or back off before retrying to avoid
 * livelock.
 */
@ThreadSafe
public class CasCounter {
    private SimulatedCAS value;

    public int getValue() {
        return this.value.get();
    }

    public int increment() {
        int v;
        do {
            v = this.value.get();
        } while (v != this.value.compareAndSwap(v, v + 1));
        return v + 1;
    }
}
