package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.immutability;

import net.jcip.annotations.Immutable;
import org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.escape.ThisEscape;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Immutable class built out of mutable underlying objects.
 *
 * NOTE: Immutable objects are always thread safe as their invariants
 * are established by the constructor and cannot be changed, thus the
 * invariants always.
 *
 * An object is immutable if:
 *
 *  - Its state cannot be modified after construction
 *  - All its fields are final
 *  - It is properly constructed (the "this" reference does not escape during construction - see
 *  the escaping "this" from inner class instances in the "qualified this" example
 *  in {@link ThisEscape} class )
 *
 *
 */
@Immutable
public final class ThreeStooges {
    /*
     * Immutable objects can still use mutable objects internally and manage their state
     *
     * While the Set that stores the names is mutable, the design of this class
     * makes it impossible to modify:
     *
     *  - the state of the set is impossible to modify after construction
     *  - the "stooges" reference is final
     *  - proper construction is easily met since the constructor does nothing that would cause
     *  the "this" reference to escape (the "this" reference is not accessible to code other
     * than the constructor and its caller)
     */
    private final Set<String> stooges = new HashSet<String>();

    public ThreeStooges() {
        this.stooges.add("Moe");
        this.stooges.add("Larry");
        this.stooges.add("Curly");
    }

    public boolean isStooge(final String name) {
        return this.stooges.contains(name);
    }

    public String getStoogeNames() {
        final List<String> stooges = new Vector<String>();
        stooges.add("Moe");
        stooges.add("Larry");
        stooges.add("Curly");
        return stooges.toString();
    }
}