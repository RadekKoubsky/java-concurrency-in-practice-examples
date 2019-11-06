package org.rkoubsky.jcip.part4.advancedtopics.chapter15.nonblockingsynchronization.cas;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Simulated CAS operation
 *
 * <NOTE_compare_and_swap>
 *
 *     CAS has three operands - a memory location V on which
 *     to operate, the expected old value A, and the new value B.
 *
 *     CAS atomically updated V to the new value B, but only if
 *     the value in V matches the expected old value A; otherwise
 *     does nothing. In either case it returns the value currently
 *     in V.
 *
 *
 *     CAS means "I think V should have the value A; if it does, put
 *     B there, otherwise don't change it but tell me I was wrong."
 *
 * </NOTE_compare_and_swap>
 *
 * CAS addresses the problem of implementing atomic read-modify-write
 * sequences without locking, because it can detect interference from
 * other threads.
 *
 * SimulatedCAS illustrates the semantics of CAS (but not the implementation
 * or performance.)
 */
@ThreadSafe
public class SimulatedCAS {
    @GuardedBy("this") private int value;

    public synchronized int get() {
        return this.value;
    }

    public synchronized int compareAndSwap(final int expectedValue,
            final int newValue) {
        final int oldValue = this.value;
        if (oldValue == expectedValue) {
            this.value = newValue;
        }
        return oldValue;
    }

    public synchronized boolean compareAndSet(final int expectedValue,
            final int newValue) {
        return (expectedValue
                == this.compareAndSwap(expectedValue, newValue));
    }
}
