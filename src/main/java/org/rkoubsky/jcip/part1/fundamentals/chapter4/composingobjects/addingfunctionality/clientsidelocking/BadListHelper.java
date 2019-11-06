package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.addingfunctionality.clientsidelocking;

import net.jcip.annotations.NotThreadSafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Examples of thread-safe and non-thread-safe implementations of
 * put-if-absent helper methods for List
 *
 * Why wouldn't this work? After all, putIfAbsent is synchronized, right?
 * The problem is that it synchronizes on the wrong lock.
 *
 * Whatever lock the List uses to guard its state, it sure isn't the lock
 * on the BadListHelper.
 *
 * BadListHelper provides only the illusion of synchronization; the various
 * list operations, while all synchronized, use different locks, which means
 * that putIfAbsent is not atomic relative to other operations on the List.
 * So there is no guarantee that another thread will not modify the list while
 * not-atomic check-then-act putIfAbsent is executing.
 */

@NotThreadSafe
class BadListHelper <E> {
    public List<E> list = Collections.synchronizedList(new ArrayList<E>());

    public synchronized boolean putIfAbsent(final E x) {
        final boolean absent = !this.list.contains(x);
        if (absent) {
            this.list.add(x);
        }
        return absent;
    }
}
