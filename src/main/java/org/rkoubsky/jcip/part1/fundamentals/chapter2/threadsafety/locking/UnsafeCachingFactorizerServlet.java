package org.rkoubsky.jcip.part1.fundamentals.chapter2.threadsafety.locking;

import net.jcip.annotations.NotThreadSafe;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @NotThreadSafe
 *
 * The definition of thread-safety requires that invariants be preserved regardless
 * of timing and interleaving of operations in multiple threads.
 *
 * The invariant of this servlet is that product of the factors cached in the
 * lastFactors variable equals the value cached in the lastNumber variable.
 *
 * Example:
 * lastNumber=6 (2 * 3)
 * lastFactors=[2,3]
 *
 * When multiple variables participate in an invariant, they are not independent. Thus when updating one,
 * you must update the others in THE SAME ATOMIC OPERATION.
 *
 * Using atomic references, we cannot update both lastNumber and lastFactors simultaneously, even though
 * each call to set is atomic. There is still a window of vulnerability when one has been modified and the other
 * has not, and during that time other threads could see that the invariant does not hold.
 *
 * NOTE: To preserve state consistency, update related state variables in a single atomic operation.
 *
 */
@NotThreadSafe
public class UnsafeCachingFactorizerServlet extends GenericServlet implements Servlet {
    private final AtomicReference<BigInteger> lastNumber = new AtomicReference<BigInteger>();
    private final AtomicReference<BigInteger[]> lastFactors = new AtomicReference<BigInteger[]>();

    @Override
    public void service(final ServletRequest req, final ServletResponse resp) throws ServletException, IOException {
        final BigInteger i = this.extractFromRequest(req);
        //reading the lastNumber and lastFactors is not done atomically
        if (i.equals(this.lastNumber.get())) {
            this.encodeIntoResponse(resp, this.lastFactors.get());
        } else {
            final BigInteger[] factors = this.factor(i);
            // updating the lastNumber and lastFactors is not atomic operation
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
