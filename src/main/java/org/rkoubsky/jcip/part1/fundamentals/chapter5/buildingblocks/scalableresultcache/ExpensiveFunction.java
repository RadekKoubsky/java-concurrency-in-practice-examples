package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.scalableresultcache;

import java.math.BigInteger;

/**
 * <NOTE_memoization>
 *
 *     ExpensiveFunction takes a long time to compute its result; we'd like to
 *     create a Computable wrapper that remembers the result of previous computations
 *     and encapsulates the caching process. This technique is known as memoization.
 *
 * </NOTE_memoization>
 */
public class ExpensiveFunction implements Computable<String, BigInteger> {
    @Override
    public BigInteger compute(final String arg) {
        // after deep thought...
        return new BigInteger(arg);
    }
}
