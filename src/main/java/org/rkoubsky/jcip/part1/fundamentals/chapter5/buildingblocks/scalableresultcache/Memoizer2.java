package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.scalableresultcache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Replacing HashMap with ConcurrentHashMap
 *
 * Memoizer2 improves on the awful concurrent behavior of
 * Memoizer1 by replacing the HashMap with a ConcurrentHashMap.
 *
 * Since ConcurrentHashMap is thread-safe, there is no need to
 * synchronize when accessing the backing Map, thus eliminating
 * the serialization induced by synchronizing the "compute" method
 * in Memoizer1.
 *
 * Memoizer2 certainly has better concurrent behavior than Memoizer1:
 * multiple threads can use it concurrently.
 *
 * <NOTE_problem_of_computing_the_same_value_twice>
 *
 * But it still has some defects as a cache - there is a window of vulnerability in
 * which two threads calling the "compute" method at the same time could
 * end up computing the same value. In the case of memoization, this is
 * merely inefficient as the purpose of a cache is to prevent the same data
 * being calculated multiple times.
 *
 * For a more general-purpose caching mechanism, it is far worse;
 * for an object cache that is supposed to provide once-and-only-once
 * initialization, this vulnerability would also pose safety risk.
 *
 * Solution: We need a way for a thread to wait for a result of a computation,
 * the FutureTask class does exactly this.
 *
 * </NOTE_problem_of_computing_the_same_value_twice>
 */
public class Memoizer2 <A, V> implements Computable<A, V> {
    private final Map<A, V> cache = new ConcurrentHashMap<A, V>();
    private final Computable<A, V> c;

    public Memoizer2(final Computable<A, V> c) {
        this.c = c;
    }

    /**
     * When two threads call the this method at the same time,
     * the computation will run two times for the same value which
     * is not what we want from cache, see the NOTE above.
     */
    @Override
    public V compute(final A arg) throws InterruptedException {
        V result = this.cache.get(arg);
        if (result == null) {
            result = this.c.compute(arg);
            this.cache.put(arg, result);
        }
        return result;
    }
}
