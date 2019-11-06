package org.rkoubsky.jcip.part4.advancedtopics.chapter15.nonblockingsynchronization.atomicvariableclasses;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;
import org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.immutability.volatileholder.VolatileCachedFactorizerServlet;
import org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.delegatingthreadsafety.faileddelegation.NumberRange;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Preserving multivariable invariants using CAS
 *
 * We could use "volatile" reference to an immutable object in
 * {@link VolatileCachedFactorizerServlet} as the race was harmless
 * because we did not care if we occasionally lost an update.
 *
 * In {@link NumberRange}, the race condition compromises the data
 * integrity. It could not be implemented safely with a "volatile"
 * reference to an immutable holder object for the upper and lower
 * bounds. Because an invariant constrains the two numbers and they
 * cannot be updated simultaneously while preserving the invariant,
 * a number range class using "volatile" references or multiple
 * atomic integers will have unsafe check-then-act sequences.
 *
 * CasNumberRange uses an AtomicReference to an IntPair to hold the state;
 * by using compareAndSet it can update the upper or lower bound without
 * the race conditions of {@link NumberRange}.
 */
@ThreadSafe
public class CasNumberRange {
    @Immutable
    private static class IntPair {
        // INVARIANT: lower <= upper
        final int lower;
        final int upper;

        public IntPair(final int lower, final int upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }

    private final AtomicReference<IntPair> values =
            new AtomicReference<IntPair>(new IntPair(0, 0));

    public int getLower() {
        return this.values.get().lower;
    }

    public int getUpper() {
        return this.values.get().upper;
    }

    public void setLower(final int i) {
        while (true) {
            final IntPair oldv = this.values.get();
            if (i > oldv.upper) {
                throw new IllegalArgumentException("Can't set lower to " + i + " > upper");
            }
            final IntPair newv = new IntPair(i, oldv.upper);
            if (this.values.compareAndSet(oldv, newv)) {
                return;
            }
        }
    }

    public void setUpper(final int i) {
        while (true) {
            final IntPair oldv = this.values.get();
            if (i < oldv.lower) {
                throw new IllegalArgumentException("Can't set upper to " + i + " < lower");
            }
            final IntPair newv = new IntPair(oldv.lower, i);
            if (this.values.compareAndSet(oldv, newv)) {
                return;
            }
        }
    }
}
