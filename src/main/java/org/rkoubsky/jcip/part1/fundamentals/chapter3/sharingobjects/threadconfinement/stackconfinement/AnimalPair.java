package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.threadconfinement.stackconfinement;

public class AnimalPair {
    private final Animal one, two;

    public AnimalPair(final Animal one, final Animal two) {
        this.one = one;
        this.two = two;
    }
}
