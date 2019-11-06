package org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock;

import org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.accounts.Account;
import org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.accounts.DollarAmount;
import org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.accounts.InsufficientFundsException;

/**
 * Dynamic lock-ordering deadlock
 *
 * How can "transferMoney" deadlock? It may appear as if all the threads
 * acquire their locks in the same order, but in fact the lock order
 * depends on the argument passed to "transferMoney", and these in turn
 * might depend on external inputs.
 *
 * Deadlock can occur if two threads call "transferMoney" at the same time,
 * one transferring from X to Y, and the other one doing the opposite:
 *
 * A: transferMoney(myAccount, yourAccount, 10);
 * B: transferMoney(yourAccount, myAccount, 20);
 *
 * With unlucky timing, A will acquire the lock on myAccount and wait for
 * the lock on yourAccount, while B is holding the lock on yourAccount and
 * waiting for the lock on myAccount.
 */
public class DynamicOrderDeadlock {
    // Warning: deadlock-prone!
    public static void transferMoney(final Account fromAccount, final Account toAccount, final DollarAmount amount)
            throws InsufficientFundsException {
        synchronized (fromAccount) {
            synchronized (toAccount) {
                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException();
                } else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }
    }

}
