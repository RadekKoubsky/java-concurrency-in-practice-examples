package org.rkoubsky.jcip.part4.advancedtopics.chapter16.javamemorymodel.publication;

import net.jcip.annotations.NotThreadSafe;

/**
 * Unsafe lazy initialization
 *
 * <NOTE_partially_constructed_object>
 *
 *     Initializing a new object involves writing to variables - the new object's fields.
 *     Similarly, publishing a reference involves writing to another variable - the reference
 *     to the new object.
 *     If you do not ensure that publishing the shared reference happens-before another
 *     thread loads that shared reference, then the write of the reference to the new object
 *     can be reordered (from the perspective of the thread consuming the object) with the
 *     writes to its fields. In that case, another thread could see an up-to-date value
 *     for the object reference but out-of-date values for some or all of that object's
 *     state - a partially constructed object.
 *
 * </NOTE_partially_constructed_object>
 *
 * <NOTE_happens_before_publication>
 *
 *     With the exception of immutable objects, it is not safe to use an object that has
 *     been initialized by another thread unless the publication happens-before the
 *     consuming thread uses it.
 *
 * </NOTE_happens_before_publication>
 */
@NotThreadSafe
public class UnsafeLazyInitialization {
    private static Resource resource;

    /**
     * Suppose thread A is the first to invoke getInstance. It sees that "resource"
     * is null, instantiates a new Resource, sets resource to reference it. When thread
     * B later calls getInstance, it might see that "resource" already has non-null
     * value and just us the already constructed Resource.
     *
     * This might look harmless at first, but there is no happens-before ordering between
     * the writing of resource in A and the reading of resource in B. A data race has been
     * used to publish the object, and therefore B is not guaranteed to see the correct state
     * of the Resource.
     *
     * The Resource constructor changes the fields of the freshly allocated Resource from
     * their default values (written by the Object constructor) to their initial values.
     * Since neither thread used synchronization, B could possible see A's actions in a
     * different order than A performed them. So even though A initialized the Resource
     * before setting resource field to reference it, B could see the write to resource
     * field as occurring before the the writes to the fields of the Resource. B could thus
     * see a partially constructed Resource that may well be in an invalid state - and
     * whose state may unexpectedly change later.
     */
    public static Resource getInstance() {
        if (resource == null) {
            resource = new Resource(); // unsafe publication
        }
        return resource;
    }

    static class Resource {
    }
}
