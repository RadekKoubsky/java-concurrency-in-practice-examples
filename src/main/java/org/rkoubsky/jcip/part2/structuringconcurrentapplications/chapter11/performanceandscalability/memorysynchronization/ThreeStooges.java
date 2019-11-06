package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter11.performanceandscalability.memorysynchronization;

import net.jcip.annotations.Immutable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Immutable class built out of mutable underlying objects,
 * demonstration of candidate for lock elision
 */
@Immutable
public final class ThreeStooges {
    private final Set<String> stooges = new HashSet<String>();

    public ThreeStooges() {
        this.stooges.add("Moe");
        this.stooges.add("Larry");
        this.stooges.add("Curly");
    }

    public boolean isStooge(final String name) {
        return this.stooges.contains(name);
    }

    /**
     * The only reference to the List is the local variable "stooges",
     * and stack-confined variables are automatically thread-local.
     *
     * A naive execution of "getStoogeNames" would acquire and release
     * lock on the Vector four times, once for each call to add or toString.
     * However, a smart runtime compiler can inline these calls and then see
     * that "stooges" and its internal state never escape, and therefore
     * all lock acquisitions can be eliminated - this is called lock elision.
     */
    public String getStoogeNames() {
        final List<String> stooges = new Vector<String>();
        stooges.add("Moe");
        stooges.add("Larry");
        stooges.add("Curly");
        return stooges.toString();
    }
}
