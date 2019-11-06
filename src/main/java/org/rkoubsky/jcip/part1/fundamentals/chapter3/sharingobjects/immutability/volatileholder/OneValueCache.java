package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.immutability.volatileholder;

import net.jcip.annotations.Immutable;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Immutable holder for caching a number and its factors
 *
 * <NOTE_immutable_holder>
 *
 * Whenever a group of related data items must be acted on atomically,
 * consider creating an immutable holder class for them.
 *
 * </NOTE_immutable_holder>
 *
 * <NOTE_updating_holder_variable>
 *
 * If the variables are to be updated, a new holder object is created, but any threads
 * working with the previous holder still see it in a consistent state.
 *
 * </NOTE_updating_holder_variable>
 *
 */
@Immutable
public class OneValueCache {
    private final BigInteger lastNumber;
    private final BigInteger[] lastFactors;

    public OneValueCache(final BigInteger i,
            final BigInteger[] factors) {
        this.lastNumber = i;
        this.lastFactors = Arrays.copyOf(factors, factors.length);
    }

    public BigInteger[] getFactors(final BigInteger i) {
        if (this.lastNumber == null || !this.lastNumber.equals(i)) {
            return null;
        } else {
            return Arrays.copyOf(this.lastFactors, this.lastFactors.length);
        }
    }
}
