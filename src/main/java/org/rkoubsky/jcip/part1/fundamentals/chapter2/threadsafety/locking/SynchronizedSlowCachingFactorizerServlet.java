package org.rkoubsky.jcip.part1.fundamentals.chapter2.threadsafety.locking;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ThreadSafe
 *
 * This servlet is now thread-safe as we used the synchronized word on the whole service method,
 * unfortunately the method has now unacceptably poor performance.
 *
 * <NOTE_synchronized_block>
 *
 *  A synchronized block has two parts: a reference to an object which will serve as the lock,
 *  and a block of code to be guarded by that lock.
 *
 *  Synchronized method is a block of code of the whole method body and the lock is the object
 *  on which the method is being invoked.
 *
 * </NOTE_synchronized_block>
 *
 * <NOTE_intrinsic_locks_or_monitor_locks>
 *
 * Every Java object can act as a lock for purposes of synchronization, these built-in locks are called
 * intrinsic or monitor locks.
 *
 * </NOTE_intrinsic_locks_or_monitor_locks>
 *
 */
@ThreadSafe
public class SynchronizedSlowCachingFactorizerServlet extends GenericServlet implements Servlet {
    @GuardedBy("this") private final AtomicReference<BigInteger> lastNumber = new AtomicReference<BigInteger>();
    @GuardedBy("this") private final AtomicReference<BigInteger[]> lastFactors = new AtomicReference<BigInteger[]>();

    /**
     * Using synchronized on the whole method results into unacceptable performance as all threads
     * queue up and are processed sequentially.
     */
    @Override
    public synchronized void service(final ServletRequest req, final ServletResponse resp) throws ServletException, IOException {
        final BigInteger i = this.extractFromRequest(req);
        if (i.equals(this.lastNumber.get())) {
            this.encodeIntoResponse(resp, this.lastFactors.get());
        } else {
            final BigInteger[] factors = this.factor(i);
            this.lastNumber.set(i);
            this.lastFactors.set(factors);
            this.encodeIntoResponse(resp, factors);
        }
    }


    private void encodeIntoResponse(final ServletResponse res, final BigInteger[] factors) {
    }

    private BigInteger extractFromRequest(final ServletRequest req) {
        return new BigInteger("7");
    }

    private BigInteger[] factor(final BigInteger i) {
        // Doesn't really factor
        return new BigInteger[]{i};
    }
}
