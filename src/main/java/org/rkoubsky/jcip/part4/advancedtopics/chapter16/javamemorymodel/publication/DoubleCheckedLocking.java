package org.rkoubsky.jcip.part4.advancedtopics.chapter16.javamemorymodel.publication;

import net.jcip.annotations.NotThreadSafe;

/**
 * Double-checked-locking antipattern
 *
 *
 */
@NotThreadSafe
public class DoubleCheckedLocking {
    /** Making resource "volatile" enables DCL to work correctly
     * (as it enforces happen-before for read and writes to the volatile field).
     *
     * However, this is an idiom whose utility has largely passed - the forces
     * that motivated it (slow uncontended synchronization, slow JVM startup) are
     * no longer in play, making it less effective as an optimization.
     *
     * The lazy initialization holder idiom offers the same benefits and is
     * easier to understand.
     */
    private static Resource resource;

    public static Resource getInstance() {
        /**
         * The common code path - fetching a reference to an already
         * constructed Resource - does not use synchronization. And that's
         * where the problem is: as described in {@link UnsafeLazyInitialization},
         * it is possible for a thread to see partially constructed Resource.
         *
         * Wrong Assumption: seeing stale value (null), then check again with lock held
         *
         * The worst case
         * It is possible to see a current value of the reference but stale values
         * for the object's state, meaning that the object could be seen to be in an
         * invalid or incorrect state.
         */
        if (resource == null) {
            synchronized (DoubleCheckedLocking.class) {
                if (resource == null) {
                    resource = new Resource();
                }
            }
        }
        return resource;
    }

    static class Resource {

    }
}
