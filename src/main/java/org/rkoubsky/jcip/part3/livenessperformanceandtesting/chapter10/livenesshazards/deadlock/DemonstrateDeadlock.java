package org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock;

import org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.accounts.Account;
import org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.accounts.DollarAmount;
import org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.accounts.InsufficientFundsException;

import java.util.Random;

import static org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.DynamicOrderDeadlock.transferMoney;

/**
 * DemonstrateDeadlock
 * <p/>
 * Driver loop that induces deadlock under typical conditions
 *
 * @author Brian Goetz and Tim Peierls
 */
public class DemonstrateDeadlock {
    private static final int NUM_THREADS = 20;
    private static final int NUM_ACCOUNTS = 5;
    private static final int NUM_ITERATIONS = 1000000;

    public static void main(final String[] args) {
        final Random rnd = new Random();
        final Account[] accounts = new Account[NUM_ACCOUNTS];

        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account();
        }

        class TransferThread extends Thread {
            @Override
            public void run() {
                for (int i = 0; i < NUM_ITERATIONS; i++) {
                    final int fromAcct = rnd.nextInt(NUM_ACCOUNTS);
                    final int toAcct = rnd.nextInt(NUM_ACCOUNTS);
                    final DollarAmount amount = new DollarAmount(rnd.nextInt(1000));
                    try {
                        transferMoney(accounts[fromAcct], accounts[toAcct], amount);
                    } catch (final InsufficientFundsException ignored) {
                    }
                }
            }
        }
        for (int i = 0; i < NUM_THREADS; i++) {
            new TransferThread().start();
        }
        System.out.println("Transferring funds finished.");
    }
}
