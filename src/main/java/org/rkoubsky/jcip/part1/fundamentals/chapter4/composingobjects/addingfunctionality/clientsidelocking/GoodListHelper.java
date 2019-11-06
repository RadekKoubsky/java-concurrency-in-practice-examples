package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.addingfunctionality.clientsidelocking;

import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * To fix the wrong synchronization from {@link BadListHelper},
 * GoodListHelper uses the same lock that the List uses by using client-side locking or
 * external locking. Client-side locking entails guarding client code that uses
 * some object X with the lock X uses to guard its own state.
 *
 * In order to use client-side locking, you must know what lock X uses.
 *
 * <NOTE_client_side_more_fragile_than_extension>
 *
 *     Client-side locking it even more fragile because it entails putting
 *     locking code for class C into classes that are totally unrelated to C.
 *
 *     Exercise care when using client-side locking on classes that do not commit
 *     to their locking strategy.
 *
 *     Just as extension violates encapsulation of implementation [EJ Item 14],
 *     client-side locking violates encapsulation of synchronization policy.
 *
 * </NOTE_client_side_more_fragile_than_extension>
 *
 */
@ThreadSafe
class GoodListHelper <E> {
    public List<E> list = Collections.synchronizedList(new ArrayList<E>());

    public boolean putIfAbsent(final E x) {
        synchronized (this.list) {
            final boolean absent = !this.list.contains(x);
            if (absent) {
                this.list.add(x);
            }
            return absent;
        }
    }
}
