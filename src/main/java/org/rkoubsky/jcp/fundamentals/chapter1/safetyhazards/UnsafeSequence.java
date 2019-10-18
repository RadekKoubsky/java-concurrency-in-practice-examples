package org.rkoubsky.jcp.fundamentals.chapter1.safetyhazards;

public class UnsafeSequence {
    private int value;

    public int getNext() {
        return this.value++;
    }
}
