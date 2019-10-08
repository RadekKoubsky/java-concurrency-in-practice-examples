package org.rkoubsky.chapter1.safetyhazards;

public class Sequence {
    private int value;

    public synchronized int getNext() {
        return this.value++;
    }
}
