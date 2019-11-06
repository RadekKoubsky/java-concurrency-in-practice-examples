package org.rkoubsky.jcip.part4.advancedtopics.chapter15.nonblockingsynchronization.nonblockingalgorithms;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Nonblocking stack using Treiber's algorithm
 *
 * ConcurrentStack shows how to construct a stack using atomic
 * references.
 *
 * The stack is a linked list of Node elements, rooted at the top,
 * each of which contains a value and a link to the next element.
 *
 * Nonblocking algorithms like ConcurrentStack derive their thread
 * safety from the fact that, like locking, "compareAndSet" provides
 * both atomicity and visibility guarantees.
 *
 * When a thread changes the state of the stack, it does so with a "compareAndSet",
 * which has the memory effects of a volatile write. When a thread examines
 * the stack, it does so bh calling "get" on the same AtomicReference,
 * which has the same effect as volatile reads.
 */
@ThreadSafe
public class ConcurrentStack <E> {
    AtomicReference<Node<E>> top = new AtomicReference<Node<E>>();

    /**
     * The "push" method prepares a new link node whose next field
     * refers to the current top of the stack, and then uses CAS to
     * try to install it on the top of the stack.
     *
     * If the same node is still on the top of the stack as when we
     * started, the CAS succeeds; if the top node has changed (because
     * other thread has added or removed elements since we started), the
     * CAS fails and "push" updates the new node based on the current state
     * and tries again. In either case, the stack is still in a consistent
     * state after the CAS.
     */
    public void push(final E item) {
        final Node<E> newHead = new Node<E>(item);
        Node<E> oldHead;
        do {
            oldHead = this.top.get();
            newHead.next = oldHead;
        } while (!this.top.compareAndSet(oldHead, newHead));
    }

    public E pop() {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            oldHead = this.top.get();
            if (oldHead == null) {
                return null;
            }
            newHead = oldHead.next;
        } while (!this.top.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }

    private static class Node <E> {
        public final E item;
        public Node<E> next;

        public Node(final E item) {
            this.item = item;
        }
    }
}
