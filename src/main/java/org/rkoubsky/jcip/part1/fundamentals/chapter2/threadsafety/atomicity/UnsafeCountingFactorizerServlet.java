package org.rkoubsky.jcip.part1.fundamentals.chapter2.threadsafety.atomicity;

import net.jcip.annotations.NotThreadSafe;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @NotThreadSafe
 *
 * <NOTE_number_of_servlet_instances>
 *
 * According to Servlet Specification 4.0, the servlet container must use only one instance per servlet declaration,
 * thus the servlet instance is shared across multiple threads and is expected to be thread safe.
 *
 * </NOTE_number_of_servlet_instances>
 *
 * This servlet is not threadsafe, it is susceptible to LOST UPDATES
 *
 * The increment operation ++count is not atomic, which means that it does not execute
 * as a single, indivisible operation.
 *
 * The ++count represents three operations: read the current value, add one to it, write the new value back
 *
 * The ++count operation is a READ-MODIFY-WRITE operation, in which the resulting state s derived from the
 * previous state. It must be atomic to ensure thread safety.
 *
 * <NOTE_race_condition>
 *
 * This servlet has several race conditions.
 *
 * A race condition occurs when the correctness of a computation depends on the relative timing
 * or interleaving of multiple threads by the runtime.
 *
 * Simply put: Getting the right answer relies on lucky timing.
 *
 * </NOTE_race_condition>
 */
@NotThreadSafe
public class UnsafeCountingFactorizerServlet extends GenericServlet implements Servlet {
    private long count;

    @Override
    public void service(final ServletRequest req, final ServletResponse resp) throws ServletException, IOException {
        final BigInteger i = this.extractFromRequest(req);
        final BigInteger[] factors = this.factor(i);
        /*
         * !!!!! Incrementing without required synchronization, don't do this !!!!!
         */
        ++this.count;
        this.encodeIntoResponse(resp, factors);
    }

    public long getCount() {
        return this.count;
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
