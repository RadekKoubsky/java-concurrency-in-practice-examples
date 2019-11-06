package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.addingfunctionality.extension;

import net.jcip.annotations.ThreadSafe;

import java.util.Vector;

/**
 * Extending Vector to have a put-if-absent method
 *
 * The concept of put-if-absent is straightforward enough - check to see
 * if an element is in the collection before adding it, and do not add it
 * if it is already there.
 *
 * (Your "check-then-act" warning bells should be going off now.)
 * The requirement that class to be thread-safe implicitly adds another
 * requirement - that operations like put-if-absent be ATOMIC.
 *
 * <NOTE_modifying_original_class>
 *
 *  The safest way to add a new atomic operation is to modify the original class.
 *  If you can modify the original class, you need to understand the implementation's
 *  synchronization policy so that you can enhance it in a manner consistent with its
 *  original design.
 *
 * </NOTE_modifying_original_class>
 *
 * <NOTE_extending_orignal_class>
 *
 *     Another approach is to extend the class, assuming it was designed
 *     for extension.
 *
 *     Extending vector is straightforward enough, but not all classes expose
 *     enough their state to subclasses to admit this approach.
 *
 *     Extension is more fragile than adding code directly to a class.
 *     If the underlying class were to change its synchronization policy by
 *     choosing a different lock to guard its state variables, the subclass
 *     would subtly and silently break, because it no longer used the right
 *     lock to control concurrent access to the base class's state.
 *
 *     The synchronization policy of Vector is fixed by its specification,
 *     so BetterVector would not suffer from this problem.
 *
 * </NOTE_extending_orignal_class>
 */
@ThreadSafe
public class BetterVector <E> extends Vector<E> {
    // When extending a serializable class, you should redefine serialVersionUID
    static final long serialVersionUID = -3963416950630760754L;

    public synchronized boolean putIfAbsent(final E x) {
        final boolean absent = !this.contains(x);
        if (absent) {
            this.add(x);
        }
        return absent;
    }
}
