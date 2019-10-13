package org.rkoubsky.chapter3.visibility.threadconfinment.stackconfinment;

public class AnimalPair {
    private final Animal one, two;

    public AnimalPair(final Animal one, final Animal two) {
        this.one = one;
        this.two = two;
    }
}
