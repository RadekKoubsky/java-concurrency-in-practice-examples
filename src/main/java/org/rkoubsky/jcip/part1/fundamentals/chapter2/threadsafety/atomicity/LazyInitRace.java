package org.rkoubsky.jcip.part1.fundamentals.chapter2.threadsafety.atomicity;

import net.jcip.annotations.NotThreadSafe;

/**
 * @NotThreadSafe
 *
 * This is an example of CHECK-THEN-ACT operation.
 *
 * To ensure thread safety, the check-then-act operations and read-modify-write operations must be atomic.
 * We call them COMPOUND ACTIONS: sequences of operations that must be executed atomically in order to remain
 * thread-safe.
 */
@NotThreadSafe
public class LazyInitRace {
    ExpensiveObject instance = null;

    /**
     * This compound action is not executed atomically
     * */
    public ExpensiveObject getInstance(){
        if (this.instance == null) {
            this.instance = new ExpensiveObject();
        }
        return this.instance;
    }
}
