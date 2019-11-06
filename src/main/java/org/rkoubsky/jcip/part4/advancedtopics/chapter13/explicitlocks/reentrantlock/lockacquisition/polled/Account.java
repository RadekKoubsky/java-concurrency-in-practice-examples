package org.rkoubsky.jcip.part4.advancedtopics.chapter13.explicitlocks.reentrantlock.lockacquisition.polled;

import java.util.concurrent.locks.Lock;

public class Account {
    public Lock lock;

    void debit(final DollarAmount d) {
    }

    void credit(final DollarAmount d) {
    }

    DollarAmount getBalance() {
        return null;
    }
}
