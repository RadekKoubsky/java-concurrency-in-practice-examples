package org.rkoubsky.jcip.part1.fundamentals.chapter2.threadsafety.atomicity;

import net.jcip.annotations.ThreadSafe;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ThreadSafe
 *
 * We use AtomicLong to ensure that all actions that access the counter state are atomic.
 *
 * Because the state of the servlet is the state of the counter and the counter is thread-safe,
 * this servlet is also thread-safe.
 *
 * When a single element of state is added to a stateless class, the resulting class will be
 * thread-safe if the state is entirely managed by the a thread safe object, in our case the AtomicLong.
 *
 * Where practical, use existing thread-safe objects, like AtomicLong, to manage your class's state.
 */
@ThreadSafe
public class CountingFactorizerServlet extends GenericServlet implements Servlet {
    private AtomicLong count = new AtomicLong(0);

    @Override
    public void service(final ServletRequest req, final ServletResponse resp) throws ServletException, IOException {
        final BigInteger i = this.extractFromRequest(req);
        final BigInteger[] factors = this.factor(i);
        // Atomic operation
        this.count.incrementAndGet();
        this.encodeIntoResponse(resp, factors);
    }

    public long getCount() {
        return this.count.get();
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
