package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizedcollections;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@ThreadSafe
public class SafeHiddenIterator {
    /**
     * Wrapping the HashSet with synchronizedSet, we encapsulate the synchronization
     * to enforce the HiddenIterator synchronization policy
     */
    @GuardedBy("this") private final Set<Integer> set = Collections.synchronizedSet(new HashSet<>());

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
         * HiddenIterator lock (this) should be acquired before using the "set"
         * field in "println" call, but debugging and logging code commonly neglect to do this.
         *
         * The real reason here is that the greater the distance between the state and the
         * synchronization that guards it, the more likely that someone will forget to use proper
         * synchronization when accessing that state.
         *
         */
        synchronized (this){
            System.out.println("DEBUG: added ten elements to " + this.set);
        }

    }
}
