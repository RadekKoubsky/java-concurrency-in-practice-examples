package org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock;

import org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.accounts.Account;
import org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.accounts.DollarAmount;
import org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.accounts.InsufficientFundsException;

/**
 * Inducing a lock order to avoid deadlock
 * <p>
 *
 * One way to induce an ordering on objects is to use System.identityHashcode,
 * which returns the value that would be returned by Object.hashcode.
 *
 * If Account has a unique, immutable, comparable key such as an account number,
 * inducing a lock ordering is even easier: order objects by their key, thus
 * eliminating the need for tie breaking lock.
 */
public class InduceLockOrder {
    private static final Object tieLock = new Object();

    public void transferMoney(final Account fromAcct, final Account toAcct, final DollarAmount amount)
            throws InsufficientFundsException {
        class Helper {
            public void transfer() throws InsufficientFundsException {
                if (fromAcct.getBalance()
                            .compareTo(amount) < 0) {
                    throw new InsufficientFundsException();
                } else {
                    fromAcct.debit(amount);
                    toAcct.credit(amount);
                }
            }
        }
        final int fromHash = System.identityHashCode(fromAcct);
        final int toHash = System.identityHashCode(toAcct);

        if (fromHash < toHash) {
            synchronized (fromAcct) {
                synchronized (toAcct) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAcct) {
                synchronized (fromAcct) {
                    new Helper().transfer();
                }
            }
        }
        /**
         * In the rare case that to objects have the same hashcode, we must use an
         * arbitrary means of ordering the lock acquisitions, an this reintroduces
         * the possibility of deadlock. To prevent inconsistent lock ordering in
         * this case, a third "tie breaking" lock is used.
         *
         * By acquiring the tie-breaking lock before acquiring either Account lock,
         * we ensure that only one thread at a time performs the risky task of
         * acquiring two locks in an arbitrary order, eliminating the possibility
         * of deadlock.
         */
        else {
            synchronized (tieLock) {
                synchronized (fromAcct) {
                    synchronized (toAcct) {
                        new Helper().transfer();
                    }
                }
            }
        }
    }
}
