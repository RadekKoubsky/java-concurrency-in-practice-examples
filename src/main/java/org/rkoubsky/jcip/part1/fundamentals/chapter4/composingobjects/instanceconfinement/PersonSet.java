package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.instanceconfinement;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashSet;
import java.util.Set;

/**
 * Using confinement to ensure thread safety
 *
 * This class illustrates how confinement and locking can work together
 * to make a class thread-safe.
 *
 * The state of "PersonSet" is managed by a HashSet, which is not thread-safe.
 * But because "mySet" is private and not allowed to escape, the HashSet is confined
 * to the PersonSet. The only code paths that can access mySet are "addPerson" and
 * "containsPerson", and each of these acquires the lock on the PersonSet. All its
 * state is guarded by its intrinsic lock, making PersonSet thread-safe.
 *
 * <NOTE_encapsulating_data>
 *
 * Encapsulating data within an object confines access to the data to the object's
 * methods, making it easier to ensure that the data is always accessed with the appropriate
 * lock held.
 *
 * </NOTE_encapsulating_data>
 *
 * Instance confinement is one of the easies ways to build thread-safe classes.
 *
 */

@ThreadSafe
public class PersonSet {
    @GuardedBy("this") private final Set<Person> mySet = new HashSet<Person>();

    public synchronized void addPerson(final Person p) {
        this.mySet.add(p);
    }

    public synchronized boolean containsPerson(final Person p) {
        return this.mySet.contains(p);
    }

    interface Person {
    }
}
