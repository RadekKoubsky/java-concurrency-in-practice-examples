package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.immutability.volatileholder;

import net.jcip.annotations.ThreadSafe;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.math.BigInteger;

/**
 * Caching the last result using a volatile reference to an immutable holder object
 *
 * <NOTE_rules_required_for_using_volatile_variables>
 *
 *     You can use volatile variables only when all the following criteria are met:
 *
 *      - writes to the variable do not depend on its current value, or you can ensure
 *      that only a single thread ever updates the value;
 *
 *      - it does not participate in invariants with other state variables; and
 *
 *      - locking is not required for any other reason while the variable is being accessed
 *
 * </NOTE_rules_required_for_using_volatile_variables>
 *
 */
@ThreadSafe
public class VolatileCachedFactorizerServlet extends GenericServlet implements Servlet {
    private volatile OneValueCache cache = new OneValueCache(null, null);

    @Override
    public void service(final ServletRequest req, final ServletResponse resp) {
        final BigInteger i = this.extractFromRequest(req);
        BigInteger[] factors = this.cache.getFactors(i);
        if (factors == null) {
            factors = this.factor(i);
            /**
             * When a thread sets the volatile "cache" field to reference
             * a new "OneValueCache", the new cached data becomes immediately visible to other
             * threads.
             *
             * Remember the part about volatile variables from a memory visibility perspective:
             *
             *  - writing a volatile variable is like exiting a synchronized block
             *  - reading a volatile is like entering a synchronized block
             *
             *
             *  When thread A writes to "cache", and thread B subsequently reads it,
             *  it will see the updated value of "cache" from thread A
             *
             */
            this.cache = new OneValueCache(i, factors);
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
