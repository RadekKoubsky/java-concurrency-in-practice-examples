package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.delegatingthreadsafety.faileddelegation;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Number range class that does not sufficiently protect its invariants
 *
 * Both "setLower" and "setUpper" are check-then-act sequences (compound actions),
 * but they do not use sufficient locking to make them atomic.
 *
 * An example of an invalid state o the range object:
 *
 * If the number range holds (0,10), and one thread calls "setLower(5)" while another
 * thread calls "setUpper(4)", with some unlucky timing both will pass the checks in
 * the setters and both modifications will be applied. The result is that the range
 * now holds (5,4) - an invalid state.
 *
 * <NOTE_making_range_thread_safe>
 *
 *     Number range could be made thread-safe using locking to maintain its
 *     invariants, such as guarding the "lower" and "upper" with common lock.
 *
 *     It must also avoid publishing "lower" and "upper" to prevent clients
 *     from subverting its invariants.
 *
 * </NOTE_making_range_thread_safe>
 *
 * <NOTE_safe_delegation_with_independent_thread_safe_variables>
 *
 *     If a class is composed of multiple independent thread-safe state variables
 *     and has no operations that have any invalid state transitions, then it can
 *     delegate thread safety to underlying state variables.
 *
 * </NOTE_safe_delegation_with_independent_thread_safe_variables>
 *
 * <NOTE_delegation_vs_volatile_variables>
 *
 *     The problem that prevented NumberRange from being thread-safe even though
 *     its state components were thread-safe is very similar to one of the rules
 *     about volatile variables:
 *
 *      - a variable is suitable for being declared volatile only if it does not
 *      participate in invariants involving other state variables.
 *
 * </NOTE_delegation_vs_volatile_variables>
 *
 */
public class NumberRange {
    // INVARIANT: lower <= upper
    private final AtomicInteger lower = new AtomicInteger(0);
    private final AtomicInteger upper = new AtomicInteger(0);

    public void setLower(final int i) {
        // Warning -- unsafe check-then-act
        if (i > this.upper.get()) {
            throw new IllegalArgumentException("can't set lower to " + i + " > upper");
        }
        this.lower.set(i);
    }

    public void setUpper(final int i) {
        // Warning -- unsafe check-then-act
        if (i < this.lower.get()) {
            throw new IllegalArgumentException("can't set upper to " + i + " < lower");
        }
        this.upper.set(i);
    }

    public boolean isInRange(final int i) {
        return (i >= this.lower.get() && i <= this.upper.get());
    }
}
