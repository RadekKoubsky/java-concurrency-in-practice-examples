package org.rkoubsky.jcip.part4.advancedtopics.chapter13.explicitlocks.reentrantlock.lockacquisition.polled;

public class DollarAmount implements Comparable<DollarAmount> {
    @Override
    public int compareTo(final DollarAmount other) {
        return 0;
    }

    DollarAmount(final int dollars) {
    }
}
