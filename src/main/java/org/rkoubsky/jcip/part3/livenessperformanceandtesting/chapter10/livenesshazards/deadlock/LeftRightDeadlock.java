package org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock;

/**
 * Simple lock-ordering deadlock
 *
 * If one thread calls "leftRight" and another calls "rightLeft",
 * and their actions are interleaved, they will deadlock.
 *
 * The deadlock came about because the two threads attempted to
 * acquire the same locks in a different order.
 *
 * <NOTE_deadly_embrace>
 *
 *     When a thread A holds lock L and tries to acquire lock M,
 *     but at the same time thread B holds M and tries to acquire
 *     L, both threads will wait forever.
 *
 * </NOTE_deadly_embrace>
 *
 * <NOTE_lock_ordering_deadlock_free>
 *
 *     A program will be free of lock-ordering deadlocks if all
 *     threads acquire the locks they need in a fixed global order.
 *
 * </NOTE_lock_ordering_deadlock_free>
 *
 *
 */
public class LeftRightDeadlock {
    private final Object left = new Object();
    private final Object right = new Object();

    public void leftRight() {
        synchronized (this.left) {
            synchronized (this.right) {
                this.doSomething();
            }
        }
    }

    public void rightLeft() {
        synchronized (this.right) {
            synchronized (this.left) {
                this.doSomethingElse();
            }
        }
    }

    void doSomething() {
    }

    void doSomethingElse() {
    }
}
