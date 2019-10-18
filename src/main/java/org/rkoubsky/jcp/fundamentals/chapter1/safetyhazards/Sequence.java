package org.rkoubsky.jcp.fundamentals.chapter1.safetyhazards;

public class Sequence {
    private int value;

    public synchronized int getNext() {
        return this.value++;
    }
}
