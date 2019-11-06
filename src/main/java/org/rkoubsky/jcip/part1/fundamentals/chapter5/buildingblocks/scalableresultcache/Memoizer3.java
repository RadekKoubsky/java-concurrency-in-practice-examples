package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.scalableresultcache;

import org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizers.futuretask.LaunderThrowable;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Memoizer3 redefines the backing map for the value cache as a
 * ConcurrentHashMap<A, FutureTask<V>> instead of ConcurrentHashMap<A, V>.
 *
 * Memoizer3 first check to see if the appropriate calculation has been started
 * (as opposed to finish in Memoizer2). If not it creates a FutureTask, registers it
 * in the Map, an starts the computation; otherwise it waits for the result of the
 * existing computation. The result might be available immediately or might be
 * in the process of being computed - but this is transparent to the caller of
 * Future.get*
 */
public class Memoizer3<A, V> implements Computable<A, V> {
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer3(final Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(final A arg) throws InterruptedException {
        Future<V> f = this.cache.get(arg);

        /**
         * A small window of vulnerability:
         *
         * Because the "if" block statement is still a non-atomic
         * check-then-act sequence, it is possible for two threads
         * to call compute with the same value at roughly the same time
         * , both see that cache does not contain the desired value, and
         * both start the computation.
         *
         * Memoizer3 is vulnerable to this problem because a compound action
         * (put-if-absent) is performed on the backing map that cannot be
         * made atomic using locking.
         *
         * Solution: Final version, FinalMemoizer takes advantage of the atomic "putIfAbsent"
         * method of ConcurrentMap, closing the window of vulnerability in Memoizer3.
         *
         */
        if (f == null) {
            final Callable<V> eval = new Callable<V>() {
                @Override
                public V call() throws Exception {
                    return Memoizer3.this.c.compute(arg);
                }
            };
            final FutureTask<V> ft = new FutureTask<>(eval);
            f = ft;
            this.cache.put(arg, ft);
            ft.run();
        }
        try {
            /**
             * Caching a Future instead of value creates a possibility of "cache pollution":
             * if a computation is cancelled or fails, future attempts to compute the result
             * will also indicate cancellation or failure.
             *
             * Future attempts to call computation Future.get always throws the same exception
             * over and over again if task failed.
             *
             * This problem is solved in FinalMemoizer by removing the Future from the cache if
             * computation failed.
             */
            return f.get();
        } catch (final ExecutionException e) {
            throw LaunderThrowable.launderThrowable(e.getCause());
        }
    }
}
