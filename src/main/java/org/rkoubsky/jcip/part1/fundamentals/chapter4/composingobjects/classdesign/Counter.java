package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.classdesign;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Simple thread-safe counter using the Java monitor pattern
 *
 * <NOTE_java_monitor_pattern>
 *
 *     An object that follows the Java monitor pattern encapsulates
 *     all its mutable state ant guards it with the object's  own
 *     intrinsic lock.
 *
 * </NOTE_java_monitor_pattern>
 *
 * <NOTE_thread_safe_class_design>
 *
 * The design process of a thread-safe class should include these three basic elements:
 *
 * 1. Identify the variables that form the object's state.
 * 2. Identify the invariants that constrain the state variables.
 * 3. Establish a policy for managing concurrent access to the object's state.
 *
 * </NOTE_thread_safe_class_design>
 *
 *
 * <NOTE_object_state>
 *
 * An object's state starts with fields:
 *
 * If fields are all of primitive type, the fields comprise the entire state.
 *
 * If the object has fields that are references to other objects, its state will
 * encompass fields from the referenced objects as well.
 *
 * This Counter class has only one field, so the "value" field comprises its entire state.
 *
 * </NOTE_object_state>
 *
 * <NOTE_thread_safety_vs_invariants>
 *
 * You cannot ensure thread-safety without understanding an object's invariants and
 * postconditions. Constraints on the valid values or state transitions for state variables
 * can create atomicity and encapsulation requirements.
 *
 * When multiple variables participate in an invariant, the lock that guards them must be held
 * for the duration of any operation that accesses related variables.
 *
 * </NOTE_thread_safety_vs_invariants>
 *
 */
@ThreadSafe
public final class Counter {
    /**
     * The "value" field is a long. The state space of a long ranges from
     * Long.MIN_VALUE to Long.MAX_VALUE, but the Counter class places
     * constraints on "value", negative values are not allowed.
     */
    @GuardedBy("this") private long value = 0;

    public synchronized long getValue() {
        return this.value;
    }

    /**
     * Operations may have postconditions that identify certain state state transitions
     * as invalid. If the current state of the counter is 17, the only valid next state
     * is 18.
     *
     */
    public synchronized long increment() {
        if (this.value == Long.MAX_VALUE) {
            throw new IllegalStateException("counter overflow");
        }
        return ++this.value;
    }
}
