package org.rkoubsky.jcip.part1.fundamentals.chapter2.threadsafety.locking.reentrancy;

/**
 * <NOTE_reentrancy>
 *
 * Reentrancy means that locks are acquired on a per-thread rather than per-invocation basis.
 *
 * Intrinsic locks are reentrant, if a thread tries to acquire a lock that it already holds,
 * the requests succeed.
 *
 * </NOTE_reentrancy>
 */
public class Widget {
    public synchronized void doSomething(){
        System.out.println("Hello from Widget class.");
    }


}

class LoggingWidget extends Widget{

    // Thread-A tries to acquire the lock on the Widget object as doSomething method is synchronized
    @Override
    public synchronized void doSomething(){
        System.out.println(this.toString() + ": calling doSomething");
        /*
        *  Thread-A again tries to acquire the lock on the Widget class and the request succeeds because intrinsic locks
        *  are reentrant, thus Thread-A can acquire the lock it already holds.
        *
        *  If intrinsic locks were not reentrant, the call to super.doSomething would never be able to acquire
        *  the lock because it would be considered already held and the Thread-A would wait forever for a lock it
        * can never acquire.
        * */
        super.doSomething();
    }
}
