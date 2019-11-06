package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.scalableresultcache.finalsolution;

import org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.scalableresultcache.Computable;
import org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizers.futuretask.LaunderThrowable;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Final implementation of Memoizer
 *
 * Final version, FinalMemoizer takes advantage of the atomic "putIfAbsent"
 * method of ConcurrentMap, closing the window of vulnerability in Memoizer3.
 *
 * FinalMemoizer does not address cache expiration, but this could be accomplished
 * by using a subclass of FutureTask that associates an expiration time with
 * each result and periodically scanning the cache for expired entries.
 *
 * Similarly, it does not address cache eviction, where old entries are removed to make
 * room for new ones so that the cache does not consume too much memory.
 *
 * With our cache implementation complete, we can now add real caching to the factorizing
 * servlet from Chapter 2, as promised.
 *
 */
public class FinalMemoizer <A, V> implements Computable<A, V> {
    private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public FinalMemoizer(final Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(final A arg) throws InterruptedException {
        while (true) {
            Future<V> f = this.cache.get(arg);
            if (f == null) {
                final Callable<V> eval = new Callable<V>() {
                    @Override
                    public V call() throws InterruptedException {
                        return FinalMemoizer.this.c.compute(arg);
                    }
                };
                final FutureTask<V> ft = new FutureTask<V>(eval);
                f = this.cache.putIfAbsent(arg, ft);
                if (f == null) {
                    f = ft;
                    ft.run();
                }
            }
            try {
                return f.get();
            } catch (final CancellationException e) {
                /**
                 * <NOTE_cache_pollution>
                 * Caching a Future instead of value creates a possibility of "cache pollution":
                 * if a computation is cancelled or fails, future attempts to compute the result
                 * will also indicate cancellation or failure.
                 *
                 * To avoid this, FinalMemoizer removes the Future from cache if it detects that the
                 * computation was cancelled; it might also be desirable to remove the Future upon
                 * detecting a RuntimeException if the computation might succeed on a future attempt.
                 *
                 * </NOTE_cache_pollution>
                 */
                this.cache.remove(arg, f);
            } catch (final ExecutionException e) {
                throw LaunderThrowable.launderThrowable(e.getCause());
            }
        }
    }
}
