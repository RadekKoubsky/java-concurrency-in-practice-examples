package org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizers.semaphores;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Using Semaphore to bound a collection
 *
 * Counting semaphores are used to control the number of activities
 * that can access a certain resource or perform a given action at
 * the same time.
 *
 * Counting semaphores can be used to implement resource pools (database connection pool)
 * or to impose a bound on a collection.
 *
 * <NOTE_permit_objects>
 *
 *     The implementation has no actual permit objects, and Semaphore
 *     does not associate dispensed permits with treads, so a permit
 *     acquired in one thread can be released from another thread.
 *
 *     You can think of "acquire" as consuming a permit and "release" as
 *     creating one; a Semaphore is not limited to the number of permits
 *     it was created with.
 *
 * </NOTE_permit_objects>
 *
 * <NOTE_binary_semaphore>
 *
 *     A degenerate case of a counting semaphore is a binary Semaphore;
 *     a Semaphore with an initial count to one. A binary semaphore can
 *     be used as mutex with nonreentrant locking semantics; whoever holds
 *     the sole permit holds the mutex.
 *
 * </NOTE_binary_semaphore>
 */
public class BoundedHashSet <T> {
    private final Set<T> set;
    private final Semaphore sem;

    public BoundedHashSet(final int bound) {
        this.set = Collections.synchronizedSet(new HashSet<T>());
        // The semaphore is initialized to the desired maximum size of the collection
        this.sem = new Semaphore(bound);
    }

    /**
     * The add operation acquires a permit before adding the item to the underlying
     * collection.
     */
    public boolean add(final T o) throws InterruptedException {
        this.sem.acquire();
        boolean wasAdded = false;
        try {
            wasAdded = this.set.add(o);
            return wasAdded;
        } finally {
            /**
             * If the underlying add operation does not actually add anything, it releases
             * the permit immediately.
             */
            if (!wasAdded) {
                this.sem.release();
            }
        }
    }

    public boolean remove(final Object o) {
        final boolean wasRemoved = this.set.remove(o);
        if (wasRemoved) {
            // A successful remove operation releases a permit, enabling more elements to be added
            this.sem.release();
        }
        return wasRemoved;
    }
}
