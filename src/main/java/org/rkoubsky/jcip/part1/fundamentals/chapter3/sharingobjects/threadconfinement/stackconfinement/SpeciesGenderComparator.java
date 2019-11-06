package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.threadconfinement.stackconfinement;

import java.util.Comparator;

public class SpeciesGenderComparator implements Comparator<Animal> {
    @Override
    public int compare(final Animal one, final Animal two) {
        final int speciesCompare = one.getSpecies().compareTo(two.getSpecies());
        return (speciesCompare != 0)
                ? speciesCompare
                : one.getGender().compareTo(two.getGender());
    }
}
