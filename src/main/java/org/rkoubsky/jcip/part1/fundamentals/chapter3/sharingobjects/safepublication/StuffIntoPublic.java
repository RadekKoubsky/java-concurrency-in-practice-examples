package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.safepublication;

/**
 * Unsafe publication. Don't do this.
 *
 * <NOTE_integrity>
 *
 * You cannot rely o nan integrity of partially constructed objects.
 *
 * An observing thread could see the object in an inconsistent state, and then later see its
 * state suddenly change, even though it has not been modified since publication.
 *
 * </NOTE_integrity>
 *
 * Two bad scenarios with improperly published objects can happen:
 *
 * 1) Bad
 *
 * Other threads could see a stale value for the "holder" field, and thus see a null reference or other older
 * value even though a value has been placed in holder.
 *
 * 2) Far worse
 *
 * Other threads could see an up-to-date value for the "holder" reference,
 * but stale values for the state of the "Holder" --->
 *
 * ---> (this happens because the "Object" constructor first writes the default
 * values to all fields before subclass constructors run, it is therefore possible to see the default value
 * for a field as stale value)
 *
 * <NOTE_safe_publication>
 *
 * To publish an object safely, both reference to the object and the object's state must be made
 * visible to other threads at the same time.
 *
 * A properly constructed object can be safely published by:
 *
 *  - initializing an object reference from a static initializer (guaranteed by the JLS and JVM,
 *    public static Holder holder = new Holder(42))
 *  - storing a reference to it into a "volatile" field or AtomicReference
 *  - storing a reference to it into a final field of a properly constructed object; or
 *  - storing a reference to it into field that is properly guarded by a lock
 *
 * </NOTE_safe_publication>
 *
 * <NOTE_publication_vs_object_mutability>
 *
 * The publication requirements for an object depend on its mutability:
 *
 *  - Immutable objects can be published through any mechanism
 *  - Effectively immutable objects must be safely published
 *  - Mutable objects must be safely published, and must be either thread-safe or guarded by a lock
 *
 * </NOTE_publication_vs_object_mutability>
 *
 * <NOTE_sharing_object_policies>
 *
 * The most useful policies for using and sharing objects in a concurrent program are:
 *
 * [Thread-confined]
 * A thread-confined object is owned exclusively by and confined to one thread,
 * and can be modified by its owning thread
 *
 * [Shared read-only]
 * A shared read-only object can be accessed concurrently by multiple threads without
 * additional synchronization, but cannot be modified by any thread. Shared read-only
 * include immutable and effectively immutable objects.
 *
 * [Shared thread-safe]
 * A thread-safe object performs synchronization internally, so multiple threads can freely
 * access it through its public interface without further synchronization.
 *
 * [Guarded]
 * A guarded object can be accessed only with a specific lock held. Guarded objects include
 * those that are encapsulated within other thread-safe objects and published objects that are
 * known to be guarded by a specific lock.
 *
 * </NOTE_sharing_object_policies>
 *
 */
public class StuffIntoPublic {
    public Holder holder;

    public void initialize() {
        this.holder = new Holder(42);
    }
}
