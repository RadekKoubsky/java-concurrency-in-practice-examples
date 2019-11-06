package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.threadconfinement.stackconfinement;

import java.util.HashSet;
import java.util.Set;

public class Ark {
    private final Set<AnimalPair> loadedAnimals = new HashSet<AnimalPair>();

    public void load(final AnimalPair pair) {
        this.loadedAnimals.add(pair);
    }
}
