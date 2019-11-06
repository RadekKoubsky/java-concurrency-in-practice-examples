package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizedcollections;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.NotThreadSafe;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Iteration hidden within string concatenation. Don't do this.
 *
 */
@NotThreadSafe
public class HiddenIterator {
    @GuardedBy("this") private final Set<Integer> set = new HashSet<>();

    public synchronized void add(final Integer i) {
        this.set.add(i);
    }

    public synchronized void remove(final Integer i) {
        this.set.remove(i);
    }

    public void addTenThings() {
        final Random r = new Random();
        for (int i = 0; i < 10; i++) {
            this.add(r.nextInt());
        }
        /**
         * WARN Hidden iteration in concatenation
         *
         * The string concatenation gets turned by the compiler into a call to
         * StringBuilder.append(Object), which in turn invokes the collection's
         * toString method - and the implementation of toString in the standard
         * collections iterates the collection, thus may throw ConcurrentModificationException
         *
         * Of course, the real problem is that HiddenIterator is not thread-safe;
         * the HiddenIterator lock (this) should be acquired before using the "set"
         * field in "println" call, but debugging and logging code commonly neglect to do this.
         *
         * The real reason here is that the greater the distance between the state and the
         * synchronization that guards it, the more likely that someone will forget to use proper
         * synchronization when accessing that state.
         */
        System.out.println("DEBUG: added ten elements to " + this.set);
    }
}
