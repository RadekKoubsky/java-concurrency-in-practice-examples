package org.rkoubsky.jcip.part1.fundamentals.chapter1.safetyhazards;

public class UnsafeSequence {
    private int value;

    public int getNext() {
        return this.value++;
    }
}
