package org.rkoubsky.jcip.part1.fundamentals.chapter1.safetyhazards;

public class Sequence {
    private int value;

    public synchronized int getNext() {
        return this.value++;
    }
}
