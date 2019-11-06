package org.rkoubsky.jcip.part4.advancedtopics.chapter13.explicitlocks.reentrantlock.lockacquisition.polled;

import org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.DynamicOrderDeadlock;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Avoiding lock-ordering deadlock using tryLock
 *
 * DeadlockAvoidance shows an alternative way of addressing the dynamic ordering
 * deadlock from {@link DynamicOrderDeadlock}: use tryLock to attempt to acquire
 * both locks, but back off and retry if they cannot both be acquired.
 *
 * The sleep time has a fixed component and a random component to reduce the likelihood
 * of livelock. If the locks cannot be acquired within te specified time, the "transferMoney"
 * method returns a failure status so that the operation can fail gracefully.
 */
public class DeadlockAvoidance {
    private static final int DELAY_FIXED = 1;
    private static final int DELAY_RANDOM = 2;
    private static Random rnd = new Random();

    public boolean transferMoney(final Account fromAcct, final Account toAcct, final DollarAmount amount,
            final long timeout, final TimeUnit unit) throws InsufficientFundsException, InterruptedException {

        final long fixedDelay = getFixedDelayComponentNanos(timeout, unit);
        final long randMod = getRandomDelayModulusNanos(timeout, unit);
        final long stopTime = System.nanoTime() + unit.toNanos(timeout);

        while (true) {
            if (fromAcct.lock.tryLock()) {
                try {
                    if (toAcct.lock.tryLock()) {
                        try {
                            if (fromAcct.getBalance().compareTo(amount) < 0) {
                                throw new InsufficientFundsException();
                            } else {
                                fromAcct.debit(amount);
                                toAcct.credit(amount);
                                return true;
                            }
                        } finally {
                            toAcct.lock.unlock();
                        }
                    }
                }
                /**
                 * Failing to use "finally" to release a Lock is a ticking bomb.
                 * When it goes off, you will have a hard time tracking down its
                 * origin as there will be no record of where or when the Lock
                 * should have been released. This is one reason not to use
                 * ReentrantLock as a blanket substitute for "synchronized":
                 * it is more "dangerous" because it doesn't automatically
                 * clean up the lock when control leaves the guarded block.
                 *
                 * FindBugs has "unrealeased lock" detector for this.
                 */
                finally {
                    fromAcct.lock.unlock();
                }
            }
            if (System.nanoTime() < stopTime) {
                return false;
            }
            NANOSECONDS.sleep(fixedDelay + rnd.nextLong() % randMod);
        }
    }

    static long getFixedDelayComponentNanos(final long timeout, final TimeUnit unit) {
        return DELAY_FIXED;
    }

    static long getRandomDelayModulusNanos(final long timeout, final TimeUnit unit) {
        return DELAY_RANDOM;
    }

}
