package org.rkoubsky.jcip.part4.advancedtopics.chapter15.nonblockingsynchronization.nonblockingalgorithms;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Insertion in the Michael-Scott nonblocking queue algorithm
 *
 * A linked queue is more complicated than a stack because it must
 * support fast access to both the head and the tail. To do this, it
 * maintains separate head and tail pointers. Two pointers refer to the
 * node at the tail: the "next" pointer of the current last element, and
 * the tail pointer.
 */
@ThreadSafe
public class LinkedQueue <E> {

    private static class Node <E> {
        final E item;
        final AtomicReference<Node<E>> next;

        public Node(final E item, final LinkedQueue.Node<E> next) {
            this.item = item;
            this.next = new AtomicReference<>(next);
        }
    }

    private final LinkedQueue.Node<E> dummy = new LinkedQueue.Node<>(null, null);
    private final AtomicReference<LinkedQueue.Node<E>> head = new AtomicReference<>(this.dummy);
    private final AtomicReference<LinkedQueue.Node<E>> tail = new AtomicReference<>(this.dummy);

    /**
     * Inserting a new element involves updating two pointers. The first links the
     * new node to the end of the list by updating the "next" pointer of the current
     * last element; the second swings the tail pointer around to point to the new
     * last element. Between these two operations, the queue is in the intermediate
     * state.
     *
     * The key observation that enables both of the required tricks is that
     * if the queue is in the quiescent (normal) state, the "next" field
     * of the link node pointed to by tail is null, and if it is in the
     * intermediate state, tail.next is non-null.
     *
     * The put method first checks to see if the queue is in the intermediate
     * state before attempting to insert a new element (step A). If it is, then
     * some other thread is already in the process of inserting an element (between
     * its steps C and D). Rather than wait for that thread to finish, the current
     * thread helps it by finishing the operation for it, advancing the tail pointer (step B).
     * It then repeats this check in case another thread has started inserting a new
     * element, advancing the tail pointer until it finds the queue in quiescent state
     * so it can begin its own insertion.
     *
     * The CAS at step C, which links the new node at the tail of the queue, could
     * fail if two threads try to insert an element at the same time. In that case
     * no harm is done: no changes have been made, and the current thread can just
     * reload the tail pointer and try again.
     *
     * If D fails, the inserting thread returns anyway rather than retrying the CAS,
     * because no retry is needed - another thread has already finished the job
     * in its step B!
     */
    public boolean put(final E item) {
        final LinkedQueue.Node<E> newNode = new LinkedQueue.Node<E>(item, null);
        while (true) {
            final LinkedQueue.Node<E> curTail = this.tail.get();
            final LinkedQueue.Node<E> tailNext = curTail.next.get();
            if (curTail == this.tail.get()) {
                if (tailNext != null) { // STEP A
                    // Queue in intermediate state, advance tail
                    this.tail.compareAndSet(curTail, tailNext); // STEP B
                } else {
                    // In quiescent state, try inserting new node
                    if (curTail.next.compareAndSet(null, newNode)) { // STEP C
                        // Insertion succeeded, try advancing tail
                        this.tail.compareAndSet(curTail, newNode); // STEP D
                        return true;
                    }
                }
            }
        }
    }
}
