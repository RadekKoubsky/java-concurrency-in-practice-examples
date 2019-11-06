package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.scalableresultcache;

import net.jcip.annotations.GuardedBy;

import java.util.HashMap;
import java.util.Map;

/**
 * Memoizer1
 *
 * Initial cache attempt using HashMap and synchronization
 *
 * @author Brian Goetz and Tim Peierls
 */
public class Memoizer1 <A, V> implements Computable<A, V> {
    @GuardedBy("this") private final Map<A, V> cache = new HashMap<A, V>();
    private final Computable<A, V> c;

    public Memoizer1(final Computable<A, V> c) {
        this.c = c;
    }

    /**
     * HashMap is not thread-safe, so to ensure that two threads do not
     * access the HashMap at the same time, Memoizer1 takes the conservative
     * approach of synchronizing the entire compute method.
     *
     * Synchronizing the entire method ensures thread safety but has an obvious
     * scalability problem: only one thread at a time can execute compute at all.
     *
     * If another thread is busy computing a result, other threads calling compute
     * may be blocked for a long time.
     *
     * Synchronizing the entire method makes the check-then-act compound
     * action atomic, but for the sake of performance and scalability.
     */
    @Override
    public synchronized V compute(final A arg) throws InterruptedException {
        V result = this.cache.get(arg);
        if (result == null) {
            result = this.c.compute(arg);
            this.cache.put(arg, result);
        }
        return result;
    }
}