package org.rkoubsky.jcip.part4.advancedtopics.chapter16.javamemorymodel.initializationsafety;

import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Map;

/**
 * Initialization safety for immutable objects
 *
 * <NOTE_initialization_safety>
 *
 *     Initialization safety guarantees that for properly constructed
 *     objects, all threads will see the correct values of final fields
 *     that were set by the constructor, regardless of how the object
 *     is published. Further, any variables that can be reached through
 *     a final field of a properly constructed object (such as the elements
 *     of a final array of the contents of a HashMap referenced by a final
 *     field) are also guaranteed to be visible to other threads.
 *
 * </NOTE_initialization_safety>
 *
 * Initialization safety means that SafeStates could be safely published even
 * through unsafe lazy initialization or stashing a reference to a SafeStates
 * in a public static field with no synchronization, even though it uses
 * no synchronization and relies on the non-thread-safe HashSet.
 *
 * If "states" field was not final, or if any method other than the constructor
 * modified its contents, initialization safety would not be strong enough to
 * safely access SafeStates without synchronization. If SafeStates had other
 * nonfinal fields, other threads might still see incorrect values of those
 * fields. And allowing the object to escape during construction invalidates
 * the initialization-safety guarantee.
 *
 * <NOTE_initialization_safety_visibility_gurantees>
 *
 *     Initialization safety makes visibility guarantees only for the values
 *     that are reachable through final fields as of the time the constructor
 *     finished.
 *     For values reachable through nonfinal fields, or values that may change
 *     after construction, you must use synchronization to ensure visibility.
 *
 * </NOTE_initialization_safety_visibility_gurantees>
 */
@ThreadSafe
public class SafeStates {
    private final Map<String, String> states;

    /**
     * For objects with final fields, initialization safety
     * prohibits reordering any part of construction with the
     * initial load of a reference to that object.
     */
    public SafeStates() {
        this.states = new HashMap<String, String>();
        this.states.put("alaska", "AK");
        this.states.put("alabama", "AL");
        /*...*/
        this.states.put("wyoming", "WY");
    }

    public String getAbbreviation(final String s) {
        return this.states.get(s);
    }
}
