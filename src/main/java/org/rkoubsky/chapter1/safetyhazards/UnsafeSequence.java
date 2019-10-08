package org.rkoubsky.chapter1.safetyhazards;

public class UnsafeSequence {
    private int value;

    public int getNext() {
        return this.value++;
    }
}
