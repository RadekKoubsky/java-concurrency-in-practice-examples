package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.instanceconfinement.javamonitorpattern;

import net.jcip.annotations.GuardedBy;
import org.rkoubsky.jcip.part1.fundamentals.chapter2.threadsafety.locking.reentrancy.Widget;
import org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.classdesign.Counter;

/**
 *
 * NOTE: See @link {@link Counter} for
 * a typical example of the Java monitor pattern.
 *
 * <NOTE_java_monitor_pattern>
 *
 *     An object that follows the Java monitor pattern encapsulates
 *     all its mutable state ant guards it with the object's  own
 *     intrinsic lock.
 *
 * </NOTE_java_monitor_pattern>
 *
 * Guarding state with a private lock:
 *
 * Any lock object can be used to guard an object's state as long as it is used
 * consistently. The PrivateLock illustrates a class that uses a private lock
 * to guard its state.
 *
 * <NOTE_advantages_of_private locks>
 *     The private lock is encapsulated, thus the client code cannot acquire it, whereas
 *     a publicly accessible lock allows the client code to participate in its synchronization
 *     policy - correctly or incorrectly.
 *
 *     !!!!
 *     Improper acquiring another object's lock by clients can cause liveness problems,
 *     and verifying that publicly accessible lock is properly used requires examining
 *     the entire program rather than a single class.
 *     !!!!
 * </NOTE_advantages_of_private>
 *
 *
 */
public class PrivateLock {
    private final Object myLock = new Object();
    @GuardedBy("myLock") Widget widget;

    void someMethod() {
        synchronized (this.myLock) {
            // Access or modify the state of widget
        }
    }
}
