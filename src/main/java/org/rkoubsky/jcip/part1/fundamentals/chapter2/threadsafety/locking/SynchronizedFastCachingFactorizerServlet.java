package org.rkoubsky.jcip.part1.fundamentals.chapter2.threadsafety.locking;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.math.BigInteger;

/**
 * This caching servlet is thread-safe and has good performance as we use synchronized blocks only
 * for accessing/updating shared state
 *
 *
 * <NOTE_atomic_variables>
 *
 *  We do not use AtomicLong as we already use synchronization blocks for long variables. Mixing
 *  two different synchronization mechanisms would be confusing and would offer no performance or
 *  safety benefits.
 *
 * </NOTE_atomic_variables>
 *
 * <NOTE_simplicity_vs_performance>
 *
 *  When implementing a synchronization policy, resist the temptation to prematurely sacrifice
 *  simplicity (potentially compromising safety) for the sake of performance.
 *
 * </NOTE_simplicity_vs_performance>
 *
 * <NOTE_locks_vs_long_operations>
 *
 * Avoid holding locks during lengthy computations or operations.
 *
 * </NOTE_locks_vs_long_operations>
 */
@ThreadSafe
public class SynchronizedFastCachingFactorizerServlet extends GenericServlet implements Servlet {
    // The lastNumber and lastFactor variables are the invariant of this servlet.
    @GuardedBy("this") private BigInteger lastNumber;
    @GuardedBy("this") private BigInteger[] lastFactors;

    @GuardedBy("this") private long hits;
    @GuardedBy("this") private long cacheHits;

    /**
     * Because both the hits and cacheHits constitute shared mutable state,
     * we must use synchronization everywhere they are accessed
     */
    public synchronized long getHits() {
        return this.hits;
    }

    /**
     * Because both the hits and cacheHits constitute shared mutable state,
     * we must use synchronization everywhere they are accessed
     */
    public synchronized double getCacheHitRatio() {
        return (double) this.cacheHits / (double) this.hits;
    }

    @Override
    public void service(final ServletRequest req, final ServletResponse resp) {
        final BigInteger i = this.extractFromRequest(req);
        BigInteger[] factors = null;
        /*
        * This synchronized block guards the check-then-act sequence which tests
        * whether we can return cached result.
        * */
        synchronized (this) {
            ++this.hits;
            if (i.equals(this.lastNumber)) {
                ++this.cacheHits;
                factors = this.lastFactors.clone();
            }
        }
        if (factors == null) {
            /*
            * As the long-running factor operation does not affect shared state,
            * we can safely exclude it from synchronized bock
            * */
            factors = this.factor(i);

            /*
             * This synchronized block guards updating both the lastNumber and lastFactors
             * */
            synchronized (this) {
                this.lastNumber = i;
                this.lastFactors = factors.clone();
            }
        }
        this.encodeIntoResponse(resp, factors);
    }

    void encodeIntoResponse(final ServletResponse resp, final BigInteger[] factors) {
    }

    BigInteger extractFromRequest(final ServletRequest req) {
        return new BigInteger("7");
    }

    BigInteger[] factor(final BigInteger i) {
        // Doesn't really factor
        return new BigInteger[]{i};
    }
}
