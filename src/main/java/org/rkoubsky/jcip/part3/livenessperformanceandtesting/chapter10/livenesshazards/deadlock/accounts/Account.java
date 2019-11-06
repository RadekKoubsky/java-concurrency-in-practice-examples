package org.rkoubsky.jcip.part3.livenessperformanceandtesting.chapter10.livenesshazards.deadlock.accounts;

import java.util.concurrent.atomic.AtomicInteger;

public class Account {
    private DollarAmount balance;
    private final int acctNo;
    private static final AtomicInteger sequence = new AtomicInteger();

    public Account() {
        this.acctNo = sequence.incrementAndGet();
    }

    public void debit(final DollarAmount d) {
        this.balance = this.balance.subtract(d);
    }

    public void credit(final DollarAmount d) {
        this.balance = this.balance.add(d);
    }

    public DollarAmount getBalance() {
        return this.balance;
    }

    public int getAcctNo() {
        return this.acctNo;
    }
}
