package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.scalableresultcache.finalsolution;

import net.jcip.annotations.ThreadSafe;
import org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.scalableresultcache.Computable;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.math.BigInteger;

/**
 * Factorizing servlet that caches results using Memoizer
 *
 * FactorizerServlet uses FinalMemoizer to cache previously computed values
 * efficiently and scalably.
 */
@ThreadSafe
public class FactorizerServlet extends GenericServlet implements Servlet {
    private final Computable<BigInteger, BigInteger[]> c = new Computable<BigInteger, BigInteger[]>() {
                @Override
                public BigInteger[] compute(final BigInteger arg) {
                    return FactorizerServlet.this.factor(arg);
                }
            };

    private final Computable<BigInteger, BigInteger[]> cache = new FinalMemoizer<BigInteger, BigInteger[]>(this.c);

    @Override
    public void service(final ServletRequest req, final ServletResponse resp) {
        try {
            final BigInteger i = this.extractFromRequest(req);
            this.encodeIntoResponse(resp, this.cache.compute(i));
        } catch (final InterruptedException e) {
            this.encodeError(resp, "factorization interrupted");
        }
    }

    void encodeIntoResponse(final ServletResponse resp, final BigInteger[] factors) {
    }

    void encodeError(final ServletResponse resp, final String errorString) {
    }

    BigInteger extractFromRequest(final ServletRequest req) {
        return new BigInteger("7");
    }

    BigInteger[] factor(final BigInteger i) {
        // Doesn't really factor
        return new BigInteger[]{i};
    }
}
