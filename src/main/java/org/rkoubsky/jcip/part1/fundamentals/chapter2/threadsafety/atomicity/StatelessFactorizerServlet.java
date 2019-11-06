package org.rkoubsky.jcip.part1.fundamentals.chapter2.threadsafety.atomicity;

import net.jcip.annotations.ThreadSafe;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @ThreadSafe
 *
 * This servlet is stateless: it has no fields and it references no fields from other classes
 *
 * The state for the factorization exists solely in local variables that are stored on the thread's stack
 * and are accessible only to the executing thread.
 *
 * NOTE: Stateless objects are always thread safe
*/
@ThreadSafe
public class StatelessFactorizerServlet extends GenericServlet implements Servlet {

    @Override
    public void service(final ServletRequest req, final ServletResponse resp) throws ServletException, IOException {
        final BigInteger i = this.extractFromRequest(req);
        final BigInteger[] factors = this.factor(i);
        this.encodeIntoResponse(resp, factors);
    }

    private void encodeIntoResponse(final ServletResponse res, final BigInteger[] factors) {
    }

    private BigInteger extractFromRequest(final ServletRequest req) {
        return new BigInteger("7");
    }

    private BigInteger[] factor(final BigInteger i) {
        // Doesn't really factor
        return new BigInteger[] { i };
    }
}
